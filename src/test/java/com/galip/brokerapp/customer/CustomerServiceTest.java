package com.galip.brokerapp.customer;

import com.galip.brokerapp.model.Customer;
import com.galip.brokerapp.repository.CustomerRepository;
import com.galip.brokerapp.service.CustomerService;
import com.galip.brokerapp.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterCustomer() {
        Customer customer = new Customer();
        customer.setUsername("testUser");
        customer.setPassword("password");

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer registeredCustomer = customerService.registerCustomer(customer);

        assertEquals("encodedPassword", registeredCustomer.getPassword());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    public void testGetCustomerById() {
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer foundCustomer = customerService.getCustomerById(1L);

        assertEquals(1L, foundCustomer.getId());
    }

    @Test
    public void testGetCustomerById_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> customerService.getCustomerById(1L));
    }
}


