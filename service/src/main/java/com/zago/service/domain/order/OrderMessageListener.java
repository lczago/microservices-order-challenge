package com.zago.service.domain.order;

import com.zago.domain.order.OrderEntity;
import com.zago.domain.order.OrderRepository;
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
        OrderEntity orderEntity = orderMessage.mapToEntity();
        orderRepository.save(orderEntity);
        logger.info("Order processed successfully: {}", orderMessage.orderId());
    }

}
