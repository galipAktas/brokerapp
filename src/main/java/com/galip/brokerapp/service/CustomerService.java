package com.galip.brokerapp.service;

import com.galip.brokerapp.model.Customer;
import com.galip.brokerapp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Customer registerCustomer(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepository.save(customer);
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer customer = getCustomerById(id);
        customer.setName(updatedCustomer.getName());
        customer.setIban(updatedCustomer.getIban());
        if (updatedCustomer.getPassword() != null && !updatedCustomer.getPassword().isEmpty()) {
            customer.setPassword(passwordEncoder.encode(updatedCustomer.getPassword()));
        }
        return customerRepository.save(customer);
    }

}
