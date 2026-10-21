package com.evalforge.dto;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.JsonNodeException;

public record ModelEndpointResponse(
        Long id,
        Boolean enabled,
        String name,
        String provider,
        String model_name,
        String base_url
) {


}
