package com.zago.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    long countByCustomerId(Long customerId);
    
    List<OrderEntity> findByCustomerId(Long customerId);

    @Query("SELECT SUM(i.price * i.quantity) FROM OrderEntity o JOIN o.items i WHERE o.orderId = :orderId")
    BigDecimal calculateOrderTotal(@Param("orderId") Long orderId);
}