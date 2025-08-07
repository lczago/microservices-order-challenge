package com.zago.api.domain.order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    BigDecimal getOrderTotal(Long orderId);
    
    long countOrdersByCustomer(Long customerId);
    
    List<OrderDto> getOrdersByCustomer(Long customerId);
}