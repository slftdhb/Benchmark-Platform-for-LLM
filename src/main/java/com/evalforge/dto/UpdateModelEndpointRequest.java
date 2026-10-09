package com.evalforge.dto;

public record UpdateModelEndpointRequest(
    boolean enabled,
    String name,
    String provider,
    String base_url,
    String model_name
) {}