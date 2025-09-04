package com.loja.loja_api.services;

import com.loja.loja_api.models.Cart;
import com.loja.loja_api.models.CartItem;
import com.loja.loja_api.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(null, userId, new ArrayList<>(), 0.0, 0.0, new java.util.Date())));
    }

    @Transactional
    public Cart updateCart(String userId, Cart updatedCart) {
        Cart cart = getCartByUserId(userId);

        // 🔧 SOLUÇÃO: Remove items um por um (evita orphan deletion)
        cart.getItems().removeIf(item -> true);

        // 🔧 Adiciona novos items diretamente na lista existente
        updatedCart.getItems().forEach(item -> {
            CartItem newItem = new CartItem();
            newItem.setId(null); // Força novo registro
            newItem.setProductId(item.getProductId());
            newItem.setName(item.getName());
            newItem.setDescription(item.getDescription());
            newItem.setPrice(item.getPrice());
            newItem.setQuantity(item.getQuantity());
            newItem.setIcon(item.getIcon());

            cart.getItems().add(newItem); // Adiciona à lista existente
        });

        cart.setDiscount(updatedCart.getDiscount());
        cart.setShipping(updatedCart.getShipping());
        cart.setUpdatedAt(new java.util.Date());

        return cartRepository.save(cart);
    }

    public void clearCart(String userId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().clear();
        cart.setDiscount(0.0);
        cartRepository.save(cart);
    }
}