package com.zago.api.domain.order;

import com.zago.domain.order.OrderEntity;
import com.zago.domain.order.OrderRepository;
import com.zago.domain.order.item.OrderItemEntity;
import com.zago.domain.order.item.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public BigDecimal getOrderTotal(Long orderId) {
        return orderRepository.calculateOrderTotal(orderId);
    }

    @Override
    public long countOrdersByCustomer(Long customerId) {
        return orderRepository.countByCustomerId(customerId);
    }

    @Override
    public List<OrderDto> getOrdersByCustomer(Long customerId) {
        List<OrderEntity> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    private OrderDto mapToDto(OrderEntity entity) {
        List<OrderItemDto> itemDtos = entity.getItems().stream()
                .map(this::mapToItemDto)
                .collect(Collectors.toList());
                
        return new OrderDto(
                entity.getOrderId(),
                entity.getCustomerId(),
                itemDtos,
                entity.getTotalValue()
        );
    }
    
    private OrderItemDto mapToItemDto(OrderItemEntity entity) {
        return new OrderItemDto(
                entity.getProduct(),
                entity.getQuantity(),
                entity.getPrice(),
                entity.getTotalValue()
        );
    }
}