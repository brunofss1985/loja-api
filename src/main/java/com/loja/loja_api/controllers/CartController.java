package com.loja.loja_api.controllers;

import com.loja.loja_api.model.Cart;
import com.loja.loja_api.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable String userId) {
        return cartService.getCartByUserId(userId);
    }

    @PutMapping("/{userId}")
    public Cart updateCart(@PathVariable String userId, @RequestBody Cart cart) {
        return cartService.updateCart(userId, cart);
    }

    @DeleteMapping("/{userId}")
    public void clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
    }
}
