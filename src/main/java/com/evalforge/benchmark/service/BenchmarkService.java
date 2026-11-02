package com.evalforge.benchmark.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

import com.evalforge.benchmark.entity.Benchmark;
import com.evalforge.benchmark.entity.BenchmarkVersion;
import org.springframework.transaction.annotation.Transactional;

import com.evalforge.benchmark.mapper.BenchmarkMapper;
import com.evalforge.benchmark.mapper.BenchmarkVersionMapper;
import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;
import com.evalforge.benchmark.dto.CreateBenchmarkRequest;
import com.evalforge.benchmark.dto.UpdateBenchmarkRequest;
import com.evalforge.benchmark.dto.BenchmarkResponse;
import com.evalforge.benchmark.exception.BenchmarkNotFoundException;
import org.apache.ibatis.annotations.Param;
import com.evalforge.benchmark.dto.PageResponse;


@Service
public class BenchmarkService {

    private final BenchmarkMapper benchmarkMapper;
    private final BenchmarkVersionMapper versionMapper;


    //构造器注入
    public BenchmarkService(BenchmarkMapper benchmarkMapper, BenchmarkVersionMapper versionMapper){
        this.benchmarkMapper = benchmarkMapper;
        this.versionMapper = versionMapper;
    }

    @Transactional
    public Long create(Long ownerId,CreateBenchmarkRequest request){
        Benchmark benchmark = new Benchmark();
        benchmark.setOwnerId(ownerId);
        benchmark.setName(request.name());
        benchmark.setStatus("ACTIVE");
        benchmark.setDescription(request.description());

        try{
            benchmarkMapper.insert(benchmark);
        }catch(DuplicateKeyException ex){
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Benchmark name hash been existed : "+request.name(),Map.of("name",request.name()),ex);
        }

        //利用mybatis回填的id进行版本初始化和插入
        BenchmarkVersion version =new BenchmarkVersion();
        version.setBenchmarkId(benchmark.getId());
        version.setVersionNo(1);
        version.setStatus("DRAFT");
        versionMapper.insert(version);

        return benchmark.getId();
    }   


    //分页查询
    public PageResponse<BenchmarkResponse> list(Long userId,int page, int size){
        //参数校验
        if(page < 1){
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Page number must be greater than 0");
        }
        if(size < 1 || size > 100){
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Size must be between 1 and 100");
        }
        //计算偏移量
        long offset = (page -1 ) * size; 
        List<BenchmarkResponse> data = benchmarkMapper.selectPage(userId,offset,size);
        long total = benchmarkMapper.count(userId);
        return new PageResponse<>(data,total,page,size);

    }

    //查详情,根据当前用户和benchmarkid查结果
    public BenchmarkResponse getById(Long userId, Long id){
        return benchmarkMapper.selectOwnedById(userId,id).orElseThrow(()-> {
            return new BenchmarkNotFoundException(id);
        });
    }

    //修改信息
    //不能存在空名（@Valid就能干），不存在重复名称，不允许修改不存在的benchmark，修改benchmark的描述，不应该改变已发布版本的信息
    @Transactional
    public int update(Long userId,Long id, UpdateBenchmarkRequest request){
            
            BenchmarkResponse benchmark = benchmarkMapper.selectOwnedById(userId,id).orElse(null);

            if(benchmark == null){
                    throw new BenchmarkNotFoundException(id);
            }
            //这里不再需要遍历所有benchmark，因为已经通过selectOwnedById获取了当前benchmark
            //如果需要检查重复名称，可以通过其他方式实现，例如查询数据库是否存在同名benchmark

            List<BenchmarkResponse> allbenchmarks = benchmarkMapper.selectByUserIdForList(userId);
            if(allbenchmarks.isEmpty()){
                throw new BenchmarkNotFoundException(id);
            }
            for(BenchmarkResponse b : allbenchmarks){
                if(b.name().equals(request.name()) && !b.id().equals(id)){
                    throw new BusinessException(ErrorCode.BENCHMARK_VERSION_CONFLICT, "Benchmark name already exists");
                }
            }

            return benchmarkMapper.update(userId,id,request);
            
    }

    
    //删除benchmark
    @Transactional
    public void archive(Long userId,Long id){
        BenchmarkResponse benchmarks = benchmarkMapper.selectOwnedById(userId,id).orElse(null);

        if(benchmarks == null){
            throw new BenchmarkNotFoundException(id);
        }

        int affectedRows =benchmarkMapper.archive(userId,id);
        if(affectedRows == 0){
            throw new BenchmarkNotFoundException(id);
        }
        
    }

    //查询版本列表
    public List<Integer> listVersions(Long userId,Long id){
        List<BenchmarkVersion> versionList = versionMapper.selectByBenchmarkId(userId,id);
        
        if(versionList.isEmpty()){
            throw new BenchmarkNotFoundException(id);
        }

        return versionList.stream().map(BenchmarkVersion::getVersionNo).toList();
    }


}