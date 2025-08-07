package com.zago.service.domain.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record OrderItemMessage(
    @JsonProperty("produto") String product,
    @JsonProperty("quantidade") Integer quantity,
    @JsonProperty("preco") BigDecimal price
) {
    public BigDecimal getTotalValue() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}