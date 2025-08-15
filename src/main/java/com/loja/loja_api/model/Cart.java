package com.loja.loja_api.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    private Double discount = 0.0;
    private Double shipping = 0.0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
}