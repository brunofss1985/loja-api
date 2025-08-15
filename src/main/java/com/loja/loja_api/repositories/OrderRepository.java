package com.loja.loja_api.repositories;

import com.loja.loja_api.model.Order;
import com.loja.loja_api.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Retorna o último pedido do cliente
    Optional<Order> findFirstByCustomerOrderByCreatedAtDesc(Customer customer);

    // Retorna todos os pedidos do cliente, ordenados do mais recente ao mais antigo
    List<Order> findByCustomerOrderByCreatedAtDesc(Customer customer);
}
