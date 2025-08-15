package com.loja.loja_api.service;

import com.loja.loja_api.model.Customer;
import com.loja.loja_api.model.Order;
import com.loja.loja_api.repositories.CustomerRepository;
import com.loja.loja_api.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Optional<Order> getLastOrderByEmail(String email) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isEmpty()) return Optional.empty();

        return orderRepository.findFirstByCustomerOrderByCreatedAtDesc(customerOpt.get());
    }

    public List<Order> getAllOrdersByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(customer -> orderRepository.findByCustomerOrderByCreatedAtDesc(customer))
                .orElse(Collections.emptyList());
    }
}
