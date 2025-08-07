package com.zago.api.domain.order;

import com.zago.api.domain.order.dto.CustomerOrderQuantityDto;
import com.zago.api.domain.order.dto.OrderDto;
import com.zago.api.domain.order.dto.OrderTotalDto;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class OrderController implements OrderControllerDoc {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Override
    public ResponseEntity<OrderTotalDto> getOrderTotal(Long orderId) {
        try {
            BigDecimal total = orderService.getOrderTotal(orderId);
            return ResponseEntity.ok(new OrderTotalDto(total));
        } catch (EntityNotFoundException e) {
            logger.warn("Order not found for id {}: {}", orderId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting total for order id {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<CustomerOrderQuantityDto> getOrderCountByCustomer(Long customerId) {
        try {
            long count = orderService.countOrdersByCustomer(customerId);
            return ResponseEntity.ok(new CustomerOrderQuantityDto(count));
        } catch (EntityNotFoundException e) {
            logger.warn("Customer not found for id {}: {}", customerId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error counting orders for customer id {}", customerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<OrderDto>> getOrdersByCustomer(Long customerId) {
        try {
            List<OrderDto> orders = orderService.getOrdersByCustomer(customerId);
            return ResponseEntity.ok(orders);
        } catch (EntityNotFoundException e) {
            logger.warn("Customer not found for id {}: {}", customerId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting orders for customer id {}", customerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}