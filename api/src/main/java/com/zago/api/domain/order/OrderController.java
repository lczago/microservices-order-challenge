package com.zago.api.domain.order;

import com.zago.api.domain.order.dto.CustomerOrderQuantityDto;
import com.zago.api.domain.order.dto.OrderDto;
import com.zago.api.domain.order.dto.OrderTotalDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
public class OrderController implements OrderControllerDoc {

    @Autowired
    private OrderService orderService;

    @Override
    public ResponseEntity<OrderTotalDto> getOrderTotal(Long orderId) {
        BigDecimal total = orderService.getOrderTotal(orderId);
        return ResponseEntity.ok(new OrderTotalDto(Optional.ofNullable(total).orElse(BigDecimal.ZERO)));
    }

    @Override
    public ResponseEntity<CustomerOrderQuantityDto> getOrderCountByCustomer(Long customerId) {
        long count = orderService.countOrdersByCustomer(customerId);
        return ResponseEntity.ok(new CustomerOrderQuantityDto(count));
    }

    @Override
    public ResponseEntity<List<OrderDto>> getOrdersByCustomer(Long customerId) {
        List<OrderDto> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
    }
}