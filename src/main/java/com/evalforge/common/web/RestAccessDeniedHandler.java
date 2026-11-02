package com.evalforge.common.web;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.evalforge.common.error.ApiError;
import com.evalforge.common.error.ErrorCode;

import org.springframework.http.MediaType;  

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;  

@Component 
public class RestAccessDeniedHandler implements AccessDeniedHandler{
        private final ObjectMapper objectMapper;

        public RestAccessDeniedHandler(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override 
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ApiError body = new ApiError(ErrorCode.ACCESS_DENIED.name(), "ACCESS DENIED", requestId(request), Instant.now(), Map.of());

            response.getWriter().write(objectMapper.writeValueAsString(body));

        }

        private String requestId(HttpServletRequest request){
           Object value = request.getAttribute("requestId");
           return value instanceof String id ? id : "unknown";
        }
}
