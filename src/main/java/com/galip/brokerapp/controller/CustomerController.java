package com.galip.brokerapp.controller;

import com.galip.brokerapp.model.Customer;
import com.galip.brokerapp.service.CustomerService;
import com.galip.brokerapp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/register")
    public Customer registerCustomer(@RequestBody Customer customer) {
        return customerService.registerCustomer(customer);
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Customer customer = customerService.getCustomerById(id);

        if (!customer.getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to access");
        }

        return customer;
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id,
                                   @RequestBody Customer updatedCustomer,
                                   Authentication authentication) {
        String username = authentication.getName();
        Customer customer = customerService.getCustomerById(id);

        if (!customer.getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to update this customer");
        }

        return customerService.updateCustomer(id, updatedCustomer);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<String> deposit(@PathVariable Long id,
                                          @RequestParam BigDecimal amount,
                                          Authentication authentication) {
        String username = authentication.getName();
        Customer customer = customerService.getCustomerById(id);

        if (!customer.getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to perform this action");
        }

        orderService.depositMoney(id, amount);
        return ResponseEntity.ok("Deposit successful");
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<String> withdraw(@PathVariable Long id,
                                           @RequestParam BigDecimal amount,
                                           @RequestParam String iban,
                                           Authentication authentication) {
        String username = authentication.getName();
        Customer customer = customerService.getCustomerById(id);

        if (!customer.getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to perform this action");
        }

        orderService.withdrawMoney(id, amount, iban);
        return ResponseEntity.ok("Withdrawal successful");
    }

}
