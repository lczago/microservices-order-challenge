package com.zago.api.e2e;

import com.zago.api.ApiApplication;
import com.zago.api.domain.order.dto.CustomerOrderQuantityDto;
import com.zago.api.domain.order.dto.OrderDto;
import com.zago.api.domain.order.dto.OrderTotalDto;
import com.zago.domain.order.OrderEntity;
import com.zago.domain.order.OrderRepository;
import com.zago.domain.order.item.OrderItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = ApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class OrderFullFlowE2ETest {

    private static final Long ORDER_ID = 2001L;
    private static final Long CUSTOMER_ID = 3001L;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();

        OrderEntity order = new OrderEntity(ORDER_ID, CUSTOMER_ID);

        OrderItemEntity item1 = new OrderItemEntity("Product X", 3, new BigDecimal("12.50"));
        OrderItemEntity item2 = new OrderItemEntity("Product Y", 2, new BigDecimal("8.75"));

        order.addItem(item1);
        order.addItem(item2);

        orderRepository.save(order);
    }

    @Test
    @DisplayName("Should return the total value of a specific order")
    void shouldGetOrderTotal() {
        BigDecimal expectedTotal = new BigDecimal("55.00");
        String url = "/api/orders/" + ORDER_ID + "/total";

        ResponseEntity<OrderTotalDto> response = restTemplate.getForEntity(url, OrderTotalDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().total()).isEqualByComparingTo(expectedTotal);
    }

    @Test
    @DisplayName("Should return the total number of orders for a specific customer")
    void shouldGetOrderCountByCustomer() {
        String url = "/api/orders/count/customer/" + CUSTOMER_ID;

        ResponseEntity<CustomerOrderQuantityDto> response = restTemplate.getForEntity(url, CustomerOrderQuantityDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().quantity()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should return all orders for a specific customer")
    void shouldGetOrdersByCustomer() {
        String url = "/api/orders/customer/" + CUSTOMER_ID;
        var responseType = new ParameterizedTypeReference<List<OrderDto>>() {
        };

        ResponseEntity<List<OrderDto>> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);

        OrderDto orderDto = response.getBody().getFirst();
        assertThat(orderDto.orderId()).isEqualTo(ORDER_ID);
        assertThat(orderDto.customerId()).isEqualTo(CUSTOMER_ID);
        assertThat(orderDto.totalValue()).isEqualByComparingTo(new BigDecimal("55.00"));
        assertThat(orderDto.items()).hasSize(2);
    }
}