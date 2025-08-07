package com.zago.api.domain.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Represents an item within an order")
public record OrderItemDto(
    @Schema(description = "Name of the product", example = "Smartphone")
    @JsonProperty("product") String product,
    
    @Schema(description = "Quantity of the product ordered", example = "2")
    @JsonProperty("quantity") Integer quantity,
    
    @Schema(description = "Unit price of the product", example = "799.99")
    @JsonProperty("price") BigDecimal price,
    
    @Schema(description = "Total value of the item (price * quantity)", example = "1599.98")
    @JsonProperty("totalValue") BigDecimal totalValue
) {
    public OrderItemDto(String product, Integer quantity, BigDecimal price) {
        this(product, quantity, price, price.multiply(BigDecimal.valueOf(quantity)));
    }
}