package com.evalforge.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import java.time.Instant;
import java.util.UUID;
import java.util.Base64; 
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;
import com.evalforge.user.entity.AppUser;



@Service 
public class TokenService {
            private final JwtConfig cfg;
            private final JwtEncoder encoder;
            private final JwtDecoder decoder;

            public TokenService(JwtConfig cfg) {
                this.cfg = cfg;

                byte[] keyBytes = Base64.getDecoder().decode(cfg.secret());
                if(keyBytes.length <32 ){
                    throw new IllegalStateException("JWT secret must be at least 32 bytes long");
                }
                SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
                this.encoder = NimbusJwtEncoder.withSecretKey(key).algorithm(MacAlgorithm.HS256).build();
                this.decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
            }

            // 根据访问的用户生成其访问token
            public String issueAccessToken(AppUser user){
                Instant now =Instant.now();
                JwtClaimsSet claims = JwtClaimsSet.builder()
                                        .issuer(cfg.issuer())
                                        .subject(String.valueOf(user.getId()))
                                        .issuedAt(now)
                                        .expiresAt(now.plus(cfg.accessTtl()))
                                        .id(UUID.randomUUID().toString())
                                        .claim("role",user.getRole())
                                        .claim("username",user.getUsername())
                                        .build();
                JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
                return encoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
            }

            public Jwt decode(String token){
                try{
                    return decoder.decode(token);
                }catch(JwtException e){
                    throw new BusinessException(ErrorCode.TOKEN_INVALID,"Invalid JWT token");
                }
            }

            public long accessTtlSeconds(){
                return cfg.accessTtl().toSeconds();
            }
            
}
    