package com.evalforge.common.web;

import org.springframework.stereotype.Component;

import com.evalforge.common.error.ApiError;
import com.evalforge.common.error.ErrorCode;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import javax.print.attribute.standard.Media;

@Component 
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint{
        private final ObjectMapper objectMapper;

        public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;  
        }
        
        @Override 
        public void commence(HttpServletRequest request, HttpServletResponse response,AuthenticationException authException) throws IOException,ServletException{
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");

                    ApiError body = new ApiError(
                        ErrorCode.AUTHENTICATION_FAILED.name(),
                        "Authentication failed",
                        requestId(request),
                        Instant.now(),
                        Map.of()
                    );

                    response.getWriter().write(objectMapper.writeValueAsString(body));
        }      
        
        private String requestId(HttpServletRequest request){
            Object value = request.getAttribute("requestId");
            return value instanceof String id ? id : "unknown";
        }
}
