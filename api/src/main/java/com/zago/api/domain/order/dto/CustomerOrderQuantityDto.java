package com.zago.api.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record CustomerOrderQuantityDto(
        @Schema(description = "Orders quantity", example = "50")
        @JsonProperty("quantity") Long quantity
) {
}
