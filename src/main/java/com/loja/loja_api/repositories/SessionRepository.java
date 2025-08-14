package com.loja.loja_api.repositories;

import com.loja.loja_api.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByJwtTokenAndActiveTrue(String jwtToken);
}
