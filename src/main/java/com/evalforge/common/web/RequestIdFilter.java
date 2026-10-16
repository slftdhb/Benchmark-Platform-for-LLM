package com.evalforge.common.web;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
public class RequestIdFilter extends OncePerRequestFilter{

    @Override
    protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain chain
    )throws ServletException,IOException{
                    String requestId = UUID.randomUUID().toString();

                    try{
                        MDC.put("requestId",requestId);
                        request.setAttribute("requestId",requestId);
                        response.setHeader("X-Request-Id",requestId);
                        chain.doFilter(request, response);

                    }finally{
                        MDC.remove("requestId");
                    }

    }

}
