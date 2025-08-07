package com.zago.api.domain.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Represents an order with its items and total value")
public record OrderDto(
    @Schema(description = "Unique identifier of the order", example = "1001")
    @JsonProperty("orderId") Long orderId,
    
    @Schema(description = "Identifier of the customer who placed the order", example = "2001")
    @JsonProperty("customerId") Long customerId,
    
    @Schema(description = "List of items in the order")
    @JsonProperty("items") List<OrderItemDto> items,
    
    @Schema(description = "Total monetary value of the order", example = "125.50")
    @JsonProperty("totalValue") BigDecimal totalValue
) {
}