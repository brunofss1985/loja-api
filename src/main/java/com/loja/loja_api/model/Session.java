package com.loja.loja_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Getter
@Setter
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String jwtToken;
    private String deviceInfo;

    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    private boolean active;
}
