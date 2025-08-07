package com.zago.service.domain.order;

import com.zago.domain.order.OrderEntity;
import com.zago.domain.order.OrderRepository;
import com.zago.domain.order.item.OrderItemEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderMessageListener.class);

    @Autowired
    private OrderRepository orderRepository;

    @RabbitListener(queues = "${rabbitmq.queue.order}")
    public void receiveOrderMessage(OrderMessage orderMessage) {
        logger.info("Received order message: {}", orderMessage.orderId());
        OrderEntity orderEntity = mapToEntity(orderMessage);
        orderRepository.save(orderEntity);
        logger.info("Order processed successfully: {}", orderMessage.orderId());
    }

    private OrderEntity mapToEntity(OrderMessage message) {
        OrderEntity orderEntity = new OrderEntity(message.orderId(), message.customerId());

        if (message.items() != null) {
            for (OrderItemMessage itemMessage : message.items()) {
                OrderItemEntity itemEntity = new OrderItemEntity(
                        itemMessage.product(),
                        itemMessage.quantity(),
                        itemMessage.price()
                );
                orderEntity.addItem(itemEntity);
            }
        }

        return orderEntity;
    }
}
