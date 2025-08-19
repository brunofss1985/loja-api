package com.loja.loja_api.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import java.util.Collections;
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
                // Desabilita CSRF, pois a autenticação é via token
                .csrf(AbstractHttpConfigurer::disable)
                // Habilita e configura o CORS usando o Bean 'corsConfigurationSource'
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Define as regras de autorização para as requisições
                .authorizeHttpRequests(auth -> auth
                        // Rotas públicas que não precisam de autenticação
                        .requestMatchers("/checkout", "/checkout/**").permitAll()
                        .requestMatchers("/api/produtos/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/chatbot/**").permitAll()
                        .requestMatchers("/webhooks/**").permitAll()
                        // Todas as outras rotas exigem autenticação
                        .anyRequest().authenticated()
                )
                // Adiciona o filtro de segurança personalizado antes do filtro padrão do Spring
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
        // Permite requisições de qualquer origem
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        // Define os métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        // Define os cabeçalhos permitidos
        configuration.setAllowedHeaders(List.of("*"));
        // Importante para alguns cenários, mas mantenha false se não usar cookies/sessões cross-origin
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica a configuração a todas as rotas da aplicação
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}