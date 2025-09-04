package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.LoginRequestDTO;
import com.loja.loja_api.dto.RegisterRequestDTO;
import com.loja.loja_api.dto.ResponseDTO;
import com.loja.loja_api.infra.security.TokenService;
import com.loja.loja_api.model.Session;
import com.loja.loja_api.model.User;
import com.loja.loja_api.repositories.SessionRepository;
import com.loja.loja_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body) {
        Optional<User> existingUser = repository.findByEmail(body.email());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email já está em uso.");
        }

        User newUser = new User();
        newUser.setName(body.name());
        newUser.setEmail(body.email());
        newUser.setPassword(passwordEncoder.encode(body.password()));
        newUser.setUserType(body.userType());
        repository.save(newUser);

        String token = tokenService.generateToken(newUser);

        Session session = new Session();
        session.setUserId(newUser.getId());
        session.setJwtToken(token);
        session.setDeviceInfo("registro"); // pode vir do frontend
        session.setCreatedAt(LocalDateTime.now());
        session.setLastActivity(LocalDateTime.now());
        session.setActive(true);
        sessionRepository.save(session);

        return ResponseEntity.ok(new ResponseDTO(
                newUser.getName(),
                token,
                newUser.getUserType().name(),
                session.getId()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
        Optional<User> userOpt = repository.findByEmail(body.email());
        if (userOpt.isEmpty() || !passwordEncoder.matches(body.password(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }

        User user = userOpt.get();
        String token = tokenService.generateToken(user);

        Session session = new Session();
        session.setUserId(user.getId());
        session.setJwtToken(token);
        session.setDeviceInfo(body.deviceInfo() != null ? body.deviceInfo() : "login");
        session.setCreatedAt(LocalDateTime.now());
        session.setLastActivity(LocalDateTime.now());
        session.setActive(true);
        sessionRepository.save(session);

        return ResponseEntity.ok(new ResponseDTO(
                user.getName(),
                token,
                user.getUserType().name(),
                session.getId()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.replace("Bearer ", "");
        Optional<Session> sessionOpt = sessionRepository.findByJwtTokenAndActiveTrue(jwt);
        sessionOpt.ifPresent(session -> {
            session.setActive(false);
            sessionRepository.save(session);
        });
        return ResponseEntity.ok("Sessão encerrada");
    }
}
