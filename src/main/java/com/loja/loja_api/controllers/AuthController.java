package com.loja.loja_api.controllers;

import com.loja.loja_api.domain.user.User;
import com.loja.loja_api.dto.LoginRequestDTO;
import com.loja.loja_api.dto.RegisterRequestDTO;
import com.loja.loja_api.dto.ResponseDTO;
import com.loja.loja_api.infra.security.TokenService;
import com.loja.loja_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body) {
        Optional<User> user = repository.findByEmail(body.email());

        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setName(body.name());
            newUser.setEmail(body.email());
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setUserType(body.userType());
            repository.save(newUser);

            String token = tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
        }

        return ResponseEntity.badRequest().body("Email already in use.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
        User user = repository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }

        return ResponseEntity.badRequest().body("Invalid credentials");
    }
}
