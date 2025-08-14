package com.loja.loja_api.infra.security;

import com.loja.loja_api.model.Session;
import com.loja.loja_api.model.User;
import com.loja.loja_api.repositories.SessionRepository;
import com.loja.loja_api.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (method.equals("GET") && path.startsWith("/api/produtos")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/chatbot") || path.startsWith("/public/chat") || path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = recoverToken(request);
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 🔐 Valida JWT
        String email = tokenService.validateToken(token);
        if (email == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 🔍 Valida sessão
        Optional<Session> sessionOpt = sessionRepository.findByJwtTokenAndActiveTrue(token);
        if (sessionOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Session session = sessionOpt.get();
        if (session.getLastActivity().isBefore(LocalDateTime.now().minusMinutes(30))) {
            session.setActive(false);
            sessionRepository.save(session);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // ✅ Atualiza atividade
        session.setLastActivity(LocalDateTime.now());
        sessionRepository.save(session);

        // 👤 Autentica usuário
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            String role = "ROLE_" + user.getUserType().name();
            var authorities = List.of(new SimpleGrantedAuthority(role));

            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7)
                : null;
    }
}
