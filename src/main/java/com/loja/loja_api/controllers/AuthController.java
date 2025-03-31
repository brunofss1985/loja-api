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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "Content-Type, Authorization")
public class AuthController {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body) {
        Optional<User> user = this.repository.findByEmail(body.email());

        // Verificar se o usuário já existe
        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));  // Codificando a senha
            newUser.setEmail(body.email());
            newUser.setName(body.name());
            newUser.setUserType(body.userType());
            this.repository.save(newUser);  // Salvando o novo usuário

            // Gerar o token
            String token = tokenService.generateToken(newUser);
            System.out.println("Generated Token: " + token);
            return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
        }

        // Retornar erro se o e-mail já existe
        return ResponseEntity.badRequest().body("Email already in use.");
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body) {
        // Buscar o usuário pelo e-mail
        User user = this.repository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));

        // Verificar se a senha fornecida é válida
        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            // Gerar token se a senha for válida
            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }

        // Caso a senha seja inválida, retornar uma resposta de erro
        return ResponseEntity.badRequest().build();
    }
}
