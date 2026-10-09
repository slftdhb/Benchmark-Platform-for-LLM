package com.evalforge.benchmark.service;

import com.evalforge.benchmark.entity.BenchmarkVersion;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import com.evalforge.benchmark.dto.UpdateBenchmarkVersionRequest;
import com.evalforge.benchmark.mapper.BenchmarkVersionMapper;
import com.evalforge.benchmark.mapper.BenchmarkMapper;
import com.evalforge.benchmark.entity.Benchmark;
import com.evalforge.benchmark.dto.BenchmarkResponse;

@Service
public class BenchmarkVersionService {

        private final BenchmarkVersionMapper benchmarkVersionMapper;
        private final BenchmarkMapper benchmarkMapper;

        //构造器注入
        public BenchmarkVersionService(BenchmarkVersionMapper benchmarkVersionMapper, BenchmarkMapper benchmarkMapper) {
                this.benchmarkVersionMapper = benchmarkVersionMapper;
                this.benchmarkMapper = benchmarkMapper;
        }


        //创建一个草稿版本,给一个benchmarkID，然后创建一个初始的版本,检查 Benchmark 是否存在。检查 Benchmark 是否已归档。检查是否已经存在 DRAFT。
        //查询最大版本号。计算下一个版本号。插入新的 DRAFT。返回新版本的 ID 或完整响应对象。
        //最后返回创建的草稿版本的id
        @Transactional
        public Long createDraftVersion(Long id){
                //根据返回的结果检查有没有这个benchmark
                BenchmarkResponse existBenchmark = benchmarkMapper.selectById(id).orElseThrow(()->{
                        return new RuntimeException("Benchmark not found");
                });

                if ("ARCHIVED".equals(existBenchmark.status())) {
                        throw new RuntimeException("Benchmark is archived");
                }    

                //检查是否已经有DRAFT存在
                int draftCount = benchmarkVersionMapper.countDraftByBenchmarkId(id);
                if (draftCount > 0) {
                        throw new RuntimeException("A draft version already exists for this benchmark");
                }

                //这里查询最大版本号
                Integer maxVersionNo = benchmarkVersionMapper.selectMaxVersionNoByBenchmarkId(id);

                BenchmarkVersion benchmarkVersion = new BenchmarkVersion();
                benchmarkVersion.setBenchmarkId(id);
                benchmarkVersion.setVersionNo(maxVersionNo == null? 1 : maxVersionNo + 1);
                benchmarkVersion.setStatus("DRAFT");

                benchmarkVersionMapper.insert(benchmarkVersion);

                //利用回填得到新创建的草稿版本的ID
                return benchmarkVersion.getId();
                
        }

        //修改草稿信息，根据benchmarkversion的id查，然后修改对应的benchmarkversion的信息
        @Transactional
        public int updateDraftNotes(Long id, UpdateBenchmarkVersionRequest updateBenchmarkVersionRequest){
                BenchmarkVersion existingVersion = benchmarkVersionMapper.selectById(id);

                if (existingVersion == null) {
                        throw new RuntimeException("Version not found");
                }

                if ("PUBLISHED".equals(existingVersion.getStatus())) {
                        throw new RuntimeException("Cannot update a published version");
                }
                
                return benchmarkVersionMapper.updateDraftNotes(id, updateBenchmarkVersionRequest);
        }

        //发布版本
        @Transactional
        public void publishVersion(Long id){

                int affected = benchmarkVersionMapper.publishVersion(id);
                
                if(affected != 1){
                    throw new IllegalStateException("version has been published or not exsit");
                }
        }

    
}