package com.zago.domain.order;

import com.zago.domain.order.item.OrderItemEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {
    
    @Id
    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "customer_id")
    private Long customerId;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemEntity> items = new ArrayList<>();
    
    public OrderEntity() {
    }
    
    public OrderEntity(Long orderId, Long customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public List<OrderItemEntity> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemEntity> items) {
        this.items = items;
    }
    
    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }
    
    public void removeItem(OrderItemEntity item) {
        items.remove(item);
        item.setOrder(null);
    }
    
    public BigDecimal getTotalValue() {
        return items.stream()
                .map(OrderItemEntity::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}