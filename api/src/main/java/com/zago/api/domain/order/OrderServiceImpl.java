package com.zago.api.domain.order;

import com.zago.api.domain.order.dto.OrderDto;
import com.zago.domain.order.OrderEntity;
import com.zago.domain.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

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
                .map(OrderDto::FromEntity)
                .collect(Collectors.toList());
    }
}