package com.loja.loja_api.infra.security;

import com.loja.loja_api.domain.user.User;
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
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Verifica se o método é OPTIONS (CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        // Recupera o token do cabeçalho Authorization
        String token = recoverToken(request);

        if (token != null) {
            String login = tokenService.validateToken(token);

            if (login != null) {
                // Recupera o usuário a partir do e-mail
                User user = userRepository.findByEmail(login)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // Define as permissões do usuário (apenas ROLE_USER para o exemplo)
                var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

                // Cria o token de autenticação e armazena no contexto de segurança
                var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Prossegue com o filtro para a requisição
        filterChain.doFilter(request, response);
    }

    // Método para recuperar o token do cabeçalho Authorization
    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove o "Bearer " do início do token
        }
        return null;
    }
}
