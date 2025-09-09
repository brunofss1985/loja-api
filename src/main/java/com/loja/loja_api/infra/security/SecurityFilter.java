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

    /**
     * Evita que o filtro seja executado para rotas públicas / OPTIONS / assets, etc.
     * Quando true, doFilterInternal NÃO será chamado.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();
        String contextPath = request.getContextPath() == null ? "" : request.getContextPath();
        String uri = request.getRequestURI();
        String path = uri.substring(contextPath.length());

        // Sempre ignorar préflight
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // Rotas públicas: cobre /api/produtos (com ou sem query string), subpaths e outras rotas públicas
        if (path.equals("/") ||
                path.startsWith("/api/produtos") ||
                path.startsWith("/auth") ||
                path.startsWith("/checkout") ||
                path.startsWith("/public") ||
                path.startsWith("/chatbot") ||
                path.startsWith("/webhooks")) {
            return true;
        }

        // Caso contrário, não ignora (ou seja, aplica o filtro)
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
            // Sem token: apenas segue (rotas protegidas serão negadas pelo Spring Security mais adiante)
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

            // atualizar last activity
            session.setLastActivity(LocalDateTime.now());
            sessionRepository.save(session);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            String role = "ROLE_" + user.getUserType().name();
            var authorities = List.of(new SimpleGrantedAuthority(role));

            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // segue com a requisição autenticada
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            // Em caso de erro inesperado, retornar 401 com mensagem para facilitar debug
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
