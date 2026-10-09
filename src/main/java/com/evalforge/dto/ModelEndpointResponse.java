package com.evalforge.dto;

public record ModelEndpointResponse(
        Long id,
        Boolean enabled,
        String name,
        String provider,
        String model_name,
        String base_url
) {}