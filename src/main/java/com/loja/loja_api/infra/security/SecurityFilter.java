package com.loja.loja_api.infra.security;

import com.loja.loja_api.model.User;
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
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("=== SECURITY FILTER - PATH: " + path + " METHOD: " + method + " ===");

        // Libera requisições GET públicas aos produtos sem autenticação
        if (method.equals("GET") && path.startsWith("/api/produtos")) {
            System.out.println("=== LIBERANDO PRODUTOS ===");
            filterChain.doFilter(request, response);
            return;
        }

        // LIBERA ENDPOINTS DO CHATBOT
        if (path.startsWith("/chatbot") || path.startsWith("/public/chat")) {
            System.out.println("=== LIBERANDO CHATBOT: " + path + " ===");
            filterChain.doFilter(request, response);
            return;
        }

        // LIBERA ENDPOINTS DE AUTH
        if (path.startsWith("/auth/")) {
            System.out.println("=== LIBERANDO AUTH ===");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("=== VALIDANDO TOKEN ===");
        String token = recoverToken(request);
        String email = tokenService.validateToken(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
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