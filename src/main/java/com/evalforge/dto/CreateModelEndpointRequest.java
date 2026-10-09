package com.evalforge.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateModelEndpointRequest(
        @NotBlank
        @Size(max = 128)
        String name,

        @NotBlank
        String base_url,
        @NotBlank   
        String provider,
        @NotBlank
        String model_name

){
    public CreateModelEndpointRequest {
        if(name.equals("qwen3")){
            throw new IllegalArgumentException("Model name 'qwen3' is Already existed");
        }
    }
}
