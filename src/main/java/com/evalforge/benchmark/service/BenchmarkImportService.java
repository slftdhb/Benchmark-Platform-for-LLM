package com.evalforge.benchmark.service;

import com.evalforge.benchmark.exception.BenchmarkVersionNotFoundException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.evalforge.benchmark.mapper.BenchmarkSampleImportMapper;
import com.evalforge.benchmark.mapper.BenchmarkVersionMapper;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.evalforge.benchmark.entity.BenchmarkVersion;
import com.evalforge.benchmark.dto.BenchmarkImportResponse;
import com.evalforge.benchmark.enums.BenchmarkVersionStatus;
import com.evalforge.benchmark.entity.BenchmarkSample;
import com.evalforge.benchmark.dto.BenchmarkSampleImportDTO;
import com.evalforge.benchmark.dto.BenchmarkSampleResponse;
import com.evalforge.benchmark.dto.PageResponse;
import com.evalforge.benchmark.exception.BenchmarkImportException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.ArrayList;

import com.evalforge.benchmark.importer.BenchmarkJsonlParser;
import com.evalforge.benchmark.importer.BenchmarkSampleValidator;


@Service
public class BenchmarkImportService {

    private final BenchmarkVersionMapper versionMapper;
    private final BenchmarkSampleImportMapper sampleMapper;
    private final ObjectMapper objectMapper;

    //依赖注入
    public BenchmarkImportService(BenchmarkVersionMapper versionMapper, BenchmarkSampleImportMapper sampleMapper,ObjectMapper objectMapper){
        this.versionMapper = versionMapper;
        this.sampleMapper = sampleMapper;
        this.objectMapper=objectMapper;
    }

    //原子导入一个文件的内容，中间失败回退
    @Transactional
    public BenchmarkImportResponse importJsonl(Long versionId,MultipartFile file){


        // 1. 查询版本并确认版本存在    
        BenchmarkVersion version = versionMapper.selectById(versionId);
        if(version == null) {
            throw new BenchmarkVersionNotFoundException(versionId);
        }
        // 2. 确认版本为 DRAFT
        if(!BenchmarkVersionStatus.DRAFT.getValue().equals(version.getStatus())) {
            throw new IllegalArgumentException("Benchmark version is not in DRAFT status");
        }

        // 3. 确认文件不为空
        if(file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        //初始化SHA256
        MessageDigest digest = createSha256Digest();

        //批处理,500一个批次处理
        List<BenchmarkSample> batch = new ArrayList<>(500);

        int lineNo = 0;
        int sampleCount = 0;

        
        //计算上传文件的SHA值
        try(
            DigestInputStream dis=new DigestInputStream(file.getInputStream(),digest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(dis,StandardCharsets.UTF_8));
        ){

            String line;

            BenchmarkJsonlParser parser =new BenchmarkJsonlParser();
            BenchmarkSampleValidator validator = new BenchmarkSampleValidator();


            while((line = reader.readLine())!= null){
                lineNo++;

                //空行跳过
                if(line.isBlank()){
                    continue;
                }

                BenchmarkSampleImportDTO dto;

                //这里进行解析
                try{
                    dto = parser.parse(line);
                }catch(Exception e){
                    throw new BenchmarkImportException(lineNo,"invalid Json"+ e.getMessage());
                }

                validator.validate(dto, lineNo);
                
                //这里从dto转向对象实体，并后续执行插入
                BenchmarkSample sample =new BenchmarkSample();
                sample.convert(versionId,lineNo,dto);

                //先使用单行插入,使用try-catch捕获原始的数据库异常，用数据库的uk键限制同一versionId下的相同sampleKey，并把原始异常给异常构造器。
                try{
                        int insertAffectedRows = sampleMapper.insertLine(sample);
                        if(insertAffectedRows != 1){
                                throw new BenchmarkImportException(lineNo, "Failed to insert sample at line ");
                }
                                sampleCount++;
                }catch(DuplicateKeyException ex){
                                throw new BenchmarkImportException(lineNo,"Duplicate key :"+sample.getSampleKey(),ex);
                }

                //这是批量插入的改进方法
                //batch.add(sample);
                //if(batch.size() == 500 ){
                //    sampleMapper.insertBatch(batch);
                //    batch.clear();
                //}

            }

                //如果还剩下部分sample，进行保存
                //if(!batch.isEmpty()){
                //    sampleMapper.insertBatch(batch);
                //    batch.clear();
                //}   

                //装成字符串
                String contentHash = HexFormat.of().formatHex(digest.digest());

                //全部成功后更新sample_count和content_hash
                int affectedRows = versionMapper.updateImportResult(
                        versionId,
                        sampleCount,
                        contentHash
                );
                
                if(affectedRows != 1){
                        throw new IllegalStateException("Version status changed during import");
                }


                return new BenchmarkImportResponse(
                            versionId,
                            file.getOriginalFilename(),
                            sampleCount,
                            contentHash,
                            version.getStatus()
                );
                
        }catch(IOException ex){
            throw new RuntimeException("Failed  to read uploaded file",ex);
        }

    }


    //分页查询样本
    public PageResponse<BenchmarkSampleResponse> listSamples(Long versionId,int page,int size){
        //参数校验
        if(page < 1){
            throw new IllegalArgumentException("Page number must be greater than 0");
        }
        if(size < 1 || size > 100){
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }

        int offset =(page-1) * size;

        List<BenchmarkSample> samples=sampleMapper.selectPage(versionId, offset, size);

        List <BenchmarkSampleResponse> data =samples.stream().map(sample-> new BenchmarkSampleResponse(
                                                                            sample.getId(),
                                                                            sample.getVersionId(),
                                                                            sample.getSampleKey(),
                                                                            parseJson(sample.getInputJson()),
                                                                            parseJson(sample.getGoldJson()),
                                                                            parseJson(sample.getMetadataJson()),
                                                                            sample.getLineNo(),
                                                                            sample.getCreatedAt()
        )).toList();


        long total =sampleMapper.countByVersionId(versionId);
        return new PageResponse<>(data,total,page,size);
    }   


    //getInstance会抛出受检异常，包装成私有方法，受检异常必须在catch或throw中处理，这里使用IllgalStateException的包装是因为java环境缺少SHA-256算法属于系统异常，而不是用户上传文件的问题
    private MessageDigest createSha256Digest() {
    try {
        return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException("SHA-256 is not supported", e);
    }

    }

    //用于转换String——>json
    private JsonNode parseJson(String json){
        if(json ==null) {
            return null;
        }
        try{
            return objectMapper.readTree(json);
        }catch(Exception ex){
            throw new IllegalStateException("Invalid json in database",ex);
        }
    }
}