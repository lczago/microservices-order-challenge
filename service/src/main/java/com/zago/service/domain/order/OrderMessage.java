package com.zago.service.domain.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zago.domain.order.OrderEntity;

import java.util.List;

public record OrderMessage(
        @JsonProperty("codigoPedido") Long orderId,
        @JsonProperty("codigoCliente") Long customerId,
        @JsonProperty("itens") List<OrderItemMessage> items
) {
    public OrderEntity mapToEntity() {
        OrderEntity orderEntity = new OrderEntity(orderId, customerId);

        if (items != null) {
            items.stream()
                    .map(OrderItemMessage::mapToEntity)
                    .forEach(orderEntity::addItem);
        }

        return orderEntity;
    }
}
