package com.galip.brokerapp.order;

import com.galip.brokerapp.model.Customer;
import com.galip.brokerapp.model.Order;
import com.galip.brokerapp.model.OrderStatus;
import com.galip.brokerapp.repository.CustomerRepository;
import com.galip.brokerapp.repository.OrderRepository;
import com.galip.brokerapp.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrder_InsufficientFunds() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setBalance(new BigDecimal("100.00"));

        Order order = new Order();
        order.setPrice(new BigDecimal("500.00"));
        order.setSize(new BigDecimal("1"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(RuntimeException.class, () -> orderService.createOrder(order));
    }

    @Test
    public void testCancelOrder_NotPending() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.MATCHED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(1L));
    }
}

