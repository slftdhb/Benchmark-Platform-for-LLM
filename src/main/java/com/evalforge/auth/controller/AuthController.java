package com.evalforge.auth.controller;
    
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.evalforge.auth.dto.LoginRequest;
import com.evalforge.auth.dto.LoginResponse;
import com.evalforge.auth.dto.RegisterRequest;
import com.evalforge.auth.service.AuthService;
import com.evalforge.security.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag( name = "Authentication")
@RestController 
@RequestMapping("api/v1/auth")
public class AuthController {   

        private final AuthService authService;

        public AuthController(AuthService authService){
            this.authService = authService;
        }
        @Operation(summary = "register a new user")
        @PostMapping("/register")
        public Long register(@Valid @RequestBody RegisterRequest request){
            return authService.register(request);
        }
        
        @Operation(summary = "login and get a jwt")
        @PostMapping("/login")
        public LoginResponse login(@Valid @RequestBody LoginRequest request){
            return authService.login(request);
        }

        @Operation(summary = "get current user")
        @GetMapping("/me")
        public CurrentUser me(@AuthenticationPrincipal CurrentUser currentUser){
            return currentUser;
        }
        
}
