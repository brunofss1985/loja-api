package com.loja.loja_api.model;

import com.loja.loja_api.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String phone;
    private String address;

    private Integer points;
    private Double credits;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.points == null) this.points = 0;
        if (this.credits == null) this.credits = 0.0;
    }
}
