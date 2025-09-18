package com.loja.loja_api.controllers;

import com.loja.loja_api.models.Order;
import com.loja.loja_api.models.OrderStatusHistory;
import com.loja.loja_api.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ✅ Último pedido por email (usuário)
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getLastOrderByUser(@PathVariable String email) {
        return orderService.getLastOrderByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Todos os pedidos por email (usuário)
    @GetMapping("/user/{email}/all")
    public ResponseEntity<List<Order>> getAllOrdersByUser(@PathVariable String email) {
        List<Order> orders = orderService.getAllOrdersByEmail(email);
        return ResponseEntity.ok(orders); // mesmo se vazio
    }

    // ✅ Todos os pedidos (admin)
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // ✅ Detalhes de pedido por ID
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Histórico de status do pedido
    @GetMapping("/{orderId}/status-history")
    public ResponseEntity<List<OrderStatusHistory>> getStatusHistory(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getStatusHistoryByOrderId(orderId));
    }
}
