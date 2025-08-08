package com.zago.service.domain.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zago.domain.order.item.OrderItemEntity;

import java.math.BigDecimal;

public record OrderItemMessage(
        @JsonProperty("produto") String product,
        @JsonProperty("quantidade") Integer quantity,
        @JsonProperty("preco") BigDecimal price
) {
    public OrderItemEntity mapToEntity() {
        return new OrderItemEntity(product, quantity, price);
    }
}