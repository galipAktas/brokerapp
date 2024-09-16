package com.galip.brokerapp.repository;

import com.galip.brokerapp.model.Order;
import com.galip.brokerapp.model.OrderSide;
import com.galip.brokerapp.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdAndCreateDateBetween(Long customerId,
                                                     LocalDateTime startDate,
                                                     LocalDateTime endDate);


    List<Order> findByStatusAndOrderSide(OrderStatus status, OrderSide orderSide);
}
