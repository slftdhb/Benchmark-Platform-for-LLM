package com.evalforge.common.web;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.evalforge.common.error.BusinessException;
import com.evalforge.security.CurrentUser;
import com.evalforge.security.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter  extends OncePerRequestFilter{
            private final TokenService tokenService;
            private final RestAuthenticationEntryPoint entryPoint;

            public JwtAuthenticationFilter(TokenService tokenService, RestAuthenticationEntryPoint entryPoint) {
                this.tokenService = tokenService;
                this.entryPoint = entryPoint;
            }
            
            //注册或者登录
            //这个函数会由父类调用
            @Override 
            protected boolean shouldNotFilter(HttpServletRequest request){
                String path = request.getServletPath();
                return "/api/v1/auth/register".equals(path) || "/api/v1/auth/login".equals(path);
            }

            @Override
            protected void doFilterInternal(HttpServletRequest request ,HttpServletResponse response, FilterChain chain) throws ServletException,IOException{
                    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
                    if( header == null || !header.startsWith("Bearer ")){
                        chain.doFilter(request,response);
                        return;
                    }

                    try {
                        Jwt jwt = tokenService.decode(header.substring(7));

                        CurrentUser principal = new CurrentUser(Long.valueOf(jwt.getSubject()),jwt.getClaimAsString("username"), jwt.getClaimAsString("role"));

                        var authentication = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of( new SimpleGrantedAuthority("ROLE_"+principal.role()))
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        //SecurityContext默认在线程上下文
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(authentication);
                        SecurityContextHolder.setContext(context);

                    }catch(BusinessException | NumberFormatException e){
                        SecurityContextHolder.clearContext();
                        entryPoint.commence(request,response,new BadCredentialsException("INVALID or EXPIRED token",e));

                        return;
                    }

                    chain.doFilter(request,response);
            }

}
