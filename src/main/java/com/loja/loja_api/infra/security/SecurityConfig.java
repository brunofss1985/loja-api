package com.loja.loja_api.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // desativa CSRF (API REST)
                .csrf(AbstractHttpConfigurer::disable)
                // habilita CORS com a fonte abaixo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // permitir preflight globally
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // rotas públicas (raiz, produtos, auth, checkout, public, chatbot, webhooks)
                        .requestMatchers("/",
                                "/api/produtos", "/api/produtos/**",
                                "/auth/**",
                                "/checkout/**",
                                "/public/**",
                                "/chatbot/**",
                                "/webhooks/**").permitAll()

                        // rota autenticada específica
                        .requestMatchers("/api/user/change-password").authenticated()

                        // resto precisa estar autenticado
                        .anyRequest().authenticated()
                )
                // nosso filtro customizado antes do UsernamePasswordAuthenticationFilter
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origens (dev + prod)
        configuration.setAllowedOrigins(Arrays.asList(
                "https://lojabr.netlify.app",
                "http://localhost:4200"
        ));

        // Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

        // Permite qualquer header vindo do front (inclui Authorization)
        configuration.setAllowedHeaders(List.of("*"));

        // Cabeçalhos expostos ao front (se precisar ler)
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));

        // Necessário se front envia cookies ou Authorization com credentials
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // aplica para todas as rotas
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
