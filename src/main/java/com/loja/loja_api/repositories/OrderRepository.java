package com.loja.loja_api.repositories;

import com.loja.loja_api.models.Order;
import com.loja.loja_api.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findFirstByCustomerOrderByCreatedAtDesc(Customer customer);
    List<Order> findByCustomerOrderByCreatedAtDesc(Customer customer);
}
