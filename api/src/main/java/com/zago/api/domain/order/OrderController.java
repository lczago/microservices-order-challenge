package com.zago.api.domain.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Operations related to orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(
        summary = "Get the total value of an order",
        description = "Retrieves the total monetary value of an order by summing the price * quantity of all items"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Order total retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BigDecimal.class))
        ),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}/total")
    public ResponseEntity<BigDecimal> getOrderTotal(
            @Parameter(description = "ID of the order to retrieve the total for") 
            @PathVariable("orderId") Long orderId) {
        BigDecimal total = orderService.getOrderTotal(orderId);
        return ResponseEntity.ok(total);
    }

    @Operation(
        summary = "Count orders by customer",
        description = "Returns the total number of orders placed by a specific customer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Order count retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))
        )
    })
    @GetMapping("/count/customer/{customerId}")
    public ResponseEntity<Long> getOrderCountByCustomer(
            @Parameter(description = "ID of the customer to count orders for") 
            @PathVariable("customerId") Long customerId) {
        long count = orderService.countOrdersByCustomer(customerId);
        return ResponseEntity.ok(count);
    }

    @Operation(
        summary = "Get all orders for a customer",
        description = "Retrieves all orders placed by a specific customer, including their items and total values"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Orders retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))
        )
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDto>> getOrdersByCustomer(
            @Parameter(description = "ID of the customer to retrieve orders for") 
            @PathVariable("customerId") Long customerId) {
        List<OrderDto> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
    }
}