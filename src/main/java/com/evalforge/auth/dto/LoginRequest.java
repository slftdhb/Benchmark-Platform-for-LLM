package com.evalforge.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
                @NotBlank 
                @Size (max = 64, min = 6)
                String username,
                @NotBlank
                String password
) {
    
}
