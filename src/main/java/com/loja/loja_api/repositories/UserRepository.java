package com.loja.loja_api.repositories;

import com.loja.loja_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
Optional<User> findByEmail(String email);
}
