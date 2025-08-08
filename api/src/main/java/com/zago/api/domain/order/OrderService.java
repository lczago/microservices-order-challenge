package com.zago.api.domain.order;

import com.zago.api.domain.order.dto.OrderDto;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    BigDecimal getOrderTotal(Long orderId);
    
    long countOrdersByCustomer(Long customerId);
    
    List<OrderDto> getOrdersByCustomer(Long customerId);
}