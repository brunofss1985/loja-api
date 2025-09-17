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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
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
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();

        // Ignora préflight
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // ✅ Caminhos totalmente públicos
        List<String> publicPaths = List.of(
                "/",
                "/auth/**",
                "/checkout/**",
                "/public/**",
                "/chatbot/**",
                "/webhooks/**",
                "/api/estoque/validade-alerta/**"
        );

        // Verifica se a requisição bate com qualquer caminho público
        for (String path : publicPaths) {
            AntPathRequestMatcher matcher = new AntPathRequestMatcher(path);
            if (matcher.matches(request)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = recoverToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String email = tokenService.validateToken(token);
            if (email == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido ou expirado.");
                return;
            }

            Optional<Session> sessionOpt = sessionRepository.findByJwtTokenAndActiveTrue(token);
            if (sessionOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sessão não encontrada ou inativa.");
                return;
            }

            Session session = sessionOpt.get();
            if (session.getLastActivity().isBefore(LocalDateTime.now().minusMinutes(30))) {
                session.setActive(false);
                sessionRepository.save(session);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada por inatividade.");
                return;
            }

            // Atualiza last activity
            session.setLastActivity(LocalDateTime.now());
            sessionRepository.save(session);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            String role = "ROLE_" + user.getUserType().name();
            var authorities = List.of(new SimpleGrantedAuthority(role));

            // Debug (pode remover depois)
            System.out.println(">> Autenticado: " + user.getEmail() + " | Role: " + role);

            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Falha na autenticação do token: " + e.getMessage());
        }
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7)
                : null;
    }
}
