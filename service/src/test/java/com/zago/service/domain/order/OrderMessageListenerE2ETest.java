package com.zago.service.domain.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zago.domain.order.OrderEntity;
import com.zago.domain.order.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * End-to-end test for the OrderMessageListener.
 * This test verifies that:
 * 1. The service can receive messages from RabbitMQ
 * 2. The messages are correctly processed and persisted to the database
 * 3. The data is stored with the expected values
 * 
 * The test uses Testcontainers to spin up PostgreSQL and RabbitMQ containers,
 * which ensures that the test runs in an isolated environment that closely
 * resembles the production environment.
 */

@SpringBootTest
@Testcontainers
public class OrderMessageListenerE2ETest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer(DockerImageName.parse("rabbitmq:latest"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("rabbitmq.queue.order", () -> "order-queue-test");
        registry.add("rabbitmq.queue.order.dlq", () -> "order-queue-test-dlq");
        registry.add("rabbitmq.exchange.order.dlx", () -> "order-exchange-test-dlx");
        registry.add("rabbitmq.listener.max-retries", () -> "3");
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderRepository orderRepository;

    private static final String ORDER_QUEUE = "order-queue-test";

    /**
     * Test that verifies the message consumption and persistence flow.
     * 
     * This test:
     * 1. Creates a test order message with two items
     * 2. Sends the message to the RabbitMQ queue
     * 3. Waits for the message to be consumed and processed
     * 4. Verifies that the order and its items are correctly persisted to the database
     * 5. Verifies that the total value is correctly calculated
     * 
     * The @Transactional annotation ensures that the test runs within a transaction,
     * which keeps the Hibernate session open and allows access to lazy-loaded collections.
     */
    @Test
    @Transactional
    void shouldConsumeOrderMessageAndPersistToDatabase() throws Exception {
        // Given: A test order message with two items
        Long orderId = 1001L;
        Long customerId = 2001L;
        
        // Ensure test isolation by cleaning up any existing data with the same ID
        orderRepository.deleteById(orderId);
        
        // Create first order item
        OrderItemMessage item1 = new OrderItemMessage(
                "Product 1",
                2,
                new BigDecimal("10.50")
        );
        
        // Create second order item
        OrderItemMessage item2 = new OrderItemMessage(
                "Product 2",
                1,
                new BigDecimal("25.75")
        );
        
        // Create the complete order message
        OrderMessage orderMessage = new OrderMessage(
                orderId,
                customerId,
                List.of(item1, item2)
        );

        // When: The message is sent to the RabbitMQ queue
        rabbitTemplate.convertAndSend(ORDER_QUEUE, orderMessage);
        
        // Then: The message should be consumed and the data should be persisted
        // Use Awaitility to wait for asynchronous processing to complete
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            // Retrieve the saved order from the database
            Optional<OrderEntity> savedOrder = orderRepository.findById(orderId);
            
            // Verify that the order exists and has the correct properties
            assertThat(savedOrder).isPresent();
            assertThat(savedOrder.get().getOrderId()).isEqualTo(orderId);
            assertThat(savedOrder.get().getCustomerId()).isEqualTo(customerId);
            assertThat(savedOrder.get().getItems()).hasSize(2);
            
            // Verify that the first item has the correct properties
            assertThat(savedOrder.get().getItems().get(0).getProduct()).isEqualTo("Product 1");
            assertThat(savedOrder.get().getItems().get(0).getQuantity()).isEqualTo(2);
            assertThat(savedOrder.get().getItems().get(0).getPrice()).isEqualByComparingTo(new BigDecimal("10.50"));
            
            // Verify that the second item has the correct properties
            assertThat(savedOrder.get().getItems().get(1).getProduct()).isEqualTo("Product 2");
            assertThat(savedOrder.get().getItems().get(1).getQuantity()).isEqualTo(1);
            assertThat(savedOrder.get().getItems().get(1).getPrice()).isEqualByComparingTo(new BigDecimal("25.75"));
            
            // Verify that the total value is correctly calculated
            BigDecimal expectedTotal = new BigDecimal("46.75"); // (10.50 * 2) + (25.75 * 1)
            assertThat(savedOrder.get().getTotalValue()).isEqualByComparingTo(expectedTotal);
        });
    }
}