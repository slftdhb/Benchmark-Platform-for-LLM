package com.evalforge.common.hasher;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import com.evalforge.benchmark.dto.CreateBenchmarkRequest;

import tools.jackson.databind.ObjectMapper;

@Component 
public class BenchmarkCreateRequestHasher {
    private final ObjectMapper objectMapper;

    public BenchmarkCreateRequestHasher(ObjectMapper objectMapper){
        this.objectMapper=objectMapper;
    }

    public String calculateRequestHash(CreateBenchmarkRequest request){
            String json  = objectMapper.writeValueAsString(new Object[]{
                    request.name(),
                    request.description()
            });

            try{
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash=digest.digest(json.getBytes(StandardCharsets.UTF_8));
                return HexFormat.of().formatHex(hash);
            }catch(NoSuchAlgorithmException ex){
                throw new IllegalStateException("no algorthm sha-256");
            }
    }
}
