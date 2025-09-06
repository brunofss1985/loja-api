package com.loja.loja_api.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.loja.loja_api.dto.LoginRequestDTO;
import com.loja.loja_api.dto.RegisterRequestDTO;
import com.loja.loja_api.dto.ResponseDTO;
import com.loja.loja_api.enums.UserType;
import com.loja.loja_api.infra.security.TokenService;
import com.loja.loja_api.models.Session;
import com.loja.loja_api.models.User;
import com.loja.loja_api.repositories.SessionRepository;
import com.loja.loja_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Value("${google.client-id}")
    private String googleClientId;

    public ResponseDTO register(RegisterRequestDTO body) {
        Optional<User> existingUser = repository.findByEmail(body.email());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email já está em uso.");
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
        session.setDeviceInfo("registro");
        session.setCreatedAt(LocalDateTime.now());
        session.setLastActivity(LocalDateTime.now());
        session.setActive(true);
        sessionRepository.save(session);

        return new ResponseDTO(
                newUser.getName(),
                token,
                newUser.getUserType().name(),
                session.getId()
        );
    }

    public ResponseDTO login(LoginRequestDTO body) {
        Optional<User> userOpt = repository.findByEmail(body.email());
        if (userOpt.isEmpty() || !passwordEncoder.matches(body.password(), userOpt.get().getPassword())) {
            throw new RuntimeException("Credenciais inválidas.");
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

        return new ResponseDTO(
                user.getName(),
                token,
                user.getUserType().name(),
                session.getId()
        );
    }

    // Novo método para login com o Google
    public ResponseDTO googleLogin(String idTokenString) {
        // Inicializa o verificador com a Client ID do Google
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        try {
            // Tenta validar o token
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                // Se o token for válido, extrai as informações do payload
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                // Procura o usuário no banco de dados
                Optional<User> existingUser = repository.findByEmail(email);

                User user;
                if (existingUser.isPresent()) {
                    // Se o usuário já existe, usa o registro existente
                    user = existingUser.get();
                } else {
                    // Se o usuário não existe, cria um novo
                    user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setUserType(UserType.USER); // Define o tipo de usuário padrão
                    repository.save(user);
                }

                // Gera um novo token JWT e uma nova sessão
                String token = tokenService.generateToken(user);

                Session session = new Session();
                session.setUserId(user.getId());
                session.setJwtToken(token);
                session.setDeviceInfo("google-login");
                session.setCreatedAt(LocalDateTime.now());
                session.setLastActivity(LocalDateTime.now());
                session.setActive(true);
                sessionRepository.save(session);

                return new ResponseDTO(
                        user.getName(),
                        token,
                        user.getUserType().name(),
                        session.getId()
                );
            } else {
                throw new RuntimeException("Token do Google inválido.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao validar o token do Google: " + e.getMessage());
        }
    }

    public void logout(String jwt) {
        Optional<Session> sessionOpt = sessionRepository.findByJwtTokenAndActiveTrue(jwt);
        sessionOpt.ifPresent(session -> {
            session.setActive(false);
            sessionRepository.save(session);
        });
    }
}