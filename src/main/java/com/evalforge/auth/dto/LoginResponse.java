package com.evalforge.auth.dto;

public record LoginResponse(
            String accessToken,
            String tokenType,
            long expiresIn
) {
}
