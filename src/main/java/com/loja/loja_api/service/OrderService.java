package com.loja.loja_api.service;

import com.loja.loja_api.model.Customer;
import com.loja.loja_api.model.Order;
import com.loja.loja_api.model.OrderStatusHistory;
import com.loja.loja_api.repositories.CustomerRepository;
import com.loja.loja_api.repositories.OrderRepository;
import com.loja.loja_api.repositories.OrderStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderStatusHistoryRepository statusHistoryRepository;

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

    public void saveStatusHistory(Order order, String status) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(status)
                .changedAt(Instant.now())
                .build();
        statusHistoryRepository.save(history);
    }
}
