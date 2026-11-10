package com.evalforge.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.s3")
public record S3Properties(
        String endpoint,
        String region,
        String accessKey,
        String secretKey,
        boolean pathStyle,
        String benchmarkBucket,
        String resultBucket
) {
    
}
