package com.evalforge.security;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtConfig(
                String secret,
                String issuer,
                Duration accessTtl
){
    
}
