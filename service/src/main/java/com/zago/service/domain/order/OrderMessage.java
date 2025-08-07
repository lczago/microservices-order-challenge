package com.zago.service.domain.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record OrderMessage(
    @JsonProperty("codigoPedido") Long orderId,
    @JsonProperty("codigoCliente") Long customerId,
    @JsonProperty("itens") List<OrderItemMessage> items
) {
    public BigDecimal getTotalValue() {
        return items.stream()
                .map(OrderItemMessage::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
