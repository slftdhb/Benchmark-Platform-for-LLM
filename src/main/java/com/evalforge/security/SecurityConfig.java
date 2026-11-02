package com.evalforge.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.evalforge.common.web.JwtAuthenticationFilter;
import com.evalforge.common.web.RestAccessDeniedHandler;
import com.evalforge.common.web.RestAuthenticationEntryPoint;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration 
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtConfig.class)
public class SecurityConfig {
    
    @Bean 
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            TokenService tokenService,
                                            RestAuthenticationEntryPoint entryPoint,
                                            RestAccessDeniedHandler deniedHandler) throws Exception{

                        http.csrf(AbstractHttpConfigurer::disable)
                        .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(a->a.
                            requestMatchers("/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                            .requestMatchers("/error").permitAll()
                            .anyRequest().authenticated()
                        ).exceptionHandling(e->e.authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(deniedHandler)
                    ).addFilterBefore(new JwtAuthenticationFilter(tokenService,entryPoint), UsernamePasswordAuthenticationFilter.class);

                    return http.build();

    }
    

}
