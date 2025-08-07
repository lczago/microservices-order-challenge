package com.zago.api.domain.order;

import com.zago.api.domain.order.dto.CustomerOrderQuantityDto;
import com.zago.api.domain.order.dto.OrderDto;
import com.zago.api.domain.order.dto.OrderTotalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Operations related to orders")
public interface OrderControllerDoc {
    @Operation(
            summary = "Get the total value of an order",
            description = "Retrieves the total monetary value of an order by summing the price * quantity of all items"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order total retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderTotalDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{orderId}/total")
    ResponseEntity<OrderTotalDto> getOrderTotal(
            @Parameter(description = "ID of the order to retrieve the total for")
            @PathVariable("orderId") Long orderId);

    @Operation(
            summary = "Count orders by customer",
            description = "Returns the total number of orders placed by a specific customer"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order count retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerOrderQuantityDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/count/customer/{customerId}")
    ResponseEntity<CustomerOrderQuantityDto> getOrderCountByCustomer(
            @Parameter(description = "ID of the customer to count orders for")
            @PathVariable("customerId") Long customerId);

    @Operation(
            summary = "Get all orders for a customer",
            description = "Retrieves all orders placed by a specific customer, including their items and total values"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = OrderDto.class)))
            ),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/customer/{customerId}")
    ResponseEntity<List<OrderDto>> getOrdersByCustomer(
            @Parameter(description = "ID of the customer to retrieve orders for")
            @PathVariable("customerId") Long customerId);
}