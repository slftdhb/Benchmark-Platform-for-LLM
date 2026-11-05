package com.evalforge.common.hasher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import com.evalforge.evaluation.dto.CreateEvaluationJobRequest;

import tools.jackson.databind.ObjectMapper;

@Component 
public class EvaluationJobCreateRequestHasher {
        private final ObjectMapper objectMapper;

        public EvaluationJobCreateRequestHasher(ObjectMapper objectMapper) {
                this.objectMapper = objectMapper;
        }

        public String calculateRequestHash(CreateEvaluationJobRequest request) {
                String requestString = objectMapper.writeValueAsString(request);
                try{
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(requestString.getBytes());
                    return HexFormat.of().formatHex(hash);
                }catch(NoSuchAlgorithmException e){
                    throw new IllegalStateException("no algorthm sha-256");
                }
        }
}
