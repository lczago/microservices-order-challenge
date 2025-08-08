package com.zago.service.domain.order;

import com.zago.domain.order.OrderRepository;
import com.zago.service.TestcontainersConfiguration;
import com.zago.service.infrastructure.config.queue.RabbitMQConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@Import({RabbitMQConfig.class, TestcontainersConfiguration.class})
public class OrderMessageListenerE2ETest {
    private static final String ORDER_QUEUE = "order-queue-test";
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderRepository orderRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("rabbitmq.queue.order", () -> ORDER_QUEUE);
        registry.add("rabbitmq.queue.order.dlq", () -> "order-queue-test-dlq");
        registry.add("rabbitmq.exchange.order.dlx", () -> "order-exchange-test-dlx");
        registry.add("rabbitmq.listener.max-retries", () -> "3");
    }

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldConsumeOrderMessageAndPersistToDatabase() {
        Long orderId = 1001L;
        Long customerId = 2001L;

        OrderItemMessage item1 = new OrderItemMessage(
                "Product 1",
                2,
                new BigDecimal("10.50")
        );

        OrderItemMessage item2 = new OrderItemMessage(
                "Product 2",
                1,
                new BigDecimal("25.75")
        );


        OrderMessage orderMessage = new OrderMessage(
                orderId,
                customerId,
                List.of(item1, item2)
        );

        rabbitTemplate.convertAndSend(ORDER_QUEUE, orderMessage);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            var savedOrder = orderRepository.findByCustomerId(customerId);

            assertThat(savedOrder.getFirst().getOrderId()).isEqualTo(orderId);
            assertThat(savedOrder.getFirst().getCustomerId()).isEqualTo(customerId);
            assertThat(savedOrder.getFirst().getItems()).hasSize(2);

            assertThat(savedOrder.getFirst().getItems().getFirst().getProduct()).isEqualTo("Product 1");
            assertThat(savedOrder.getFirst().getItems().getFirst().getQuantity()).isEqualTo(2);
            assertThat(savedOrder.getFirst().getItems().getFirst().getPrice()).isEqualByComparingTo(new BigDecimal("10.50"));

            assertThat(savedOrder.getFirst().getItems().get(1).getProduct()).isEqualTo("Product 2");
            assertThat(savedOrder.getFirst().getItems().get(1).getQuantity()).isEqualTo(1);
            assertThat(savedOrder.getFirst().getItems().get(1).getPrice()).isEqualByComparingTo(new BigDecimal("25.75"));

            BigDecimal expectedTotal = new BigDecimal("46.75");
            assertThat(savedOrder.getFirst().getTotalValue()).isEqualByComparingTo(expectedTotal);
        });
    }
}