package com.evalforge.security;

public record CurrentUser(
        Long id,
        String username,
        String role
) {
    
}
