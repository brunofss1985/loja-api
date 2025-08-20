package com.loja.loja_api.controllers;

import com.loja.loja_api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/user/{email}")
    public ResponseEntity<?> getLastOrderByUser(@PathVariable String email) {
        return orderService.getLastOrderByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{email}/all")
    public ResponseEntity<?> getAllOrdersByUser(@PathVariable String email) {
        var orders = orderService.getAllOrdersByEmail(email);
        if (orders.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(orders);
    }
}
