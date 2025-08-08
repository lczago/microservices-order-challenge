package com.zago.api.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record OrderTotalDto(
        @Schema(description = "Total value of the order", example = "100.00")
        @JsonProperty("total") BigDecimal total
) {
}
