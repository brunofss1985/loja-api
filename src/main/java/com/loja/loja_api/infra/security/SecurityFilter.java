package com.loja.loja_api.infra.security;

import com.loja.loja_api.models.Session;
import com.loja.loja_api.models.User;
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

        // 🔓 Ignorar rotas públicas
        if (isPublicRoute(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = recoverToken(request);

        if (token != null) {
            try {
                String email = tokenService.validateToken(token);
                if (email == null) {
                    throw new RuntimeException("Token JWT inválido ou expirado.");
                }

                Optional<Session> sessionOpt = sessionRepository.findByJwtTokenAndActiveTrue(token);
                if (sessionOpt.isEmpty()) {
                    throw new RuntimeException("Sessão não encontrada ou inativa.");
                }

                Session session = sessionOpt.get();
                if (session.getLastActivity().isBefore(LocalDateTime.now().minusMinutes(30))) {
                    session.setActive(false);
                    sessionRepository.save(session);
                    throw new RuntimeException("Sessão expirada por inatividade.");
                }

                session.setLastActivity(LocalDateTime.now());
                sessionRepository.save(session);

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

                String role = "ROLE_" + user.getUserType().name();
                var authorities = List.of(new SimpleGrantedAuthority(role));

                var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                System.err.println("❌ Falha na autenticação do token: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7)
                : null;
    }

    private boolean isPublicRoute(String path) {
        return path.startsWith("/api/produtos")
                || path.startsWith("/auth")
                || path.startsWith("/checkout")
                || path.startsWith("/public")
                || path.startsWith("/chatbot")
                || path.startsWith("/webhooks")
                || path.equals("/");
    }
}
