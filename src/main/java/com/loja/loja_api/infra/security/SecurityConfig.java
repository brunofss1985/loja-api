package com.loja.loja_api.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Ajuste conforme sua lógica de autenticação
                )
                .cors(Customizer.withDefaults()) // Habilita CORS com configuração externa
                .csrf(csrf -> csrf.disable()); // Desativa CSRF para APIs públicas

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(false);
//        config.setAllowedOriginPatterns(Collections.singletonList("*"));
//        config.setAllowedHeaders(Arrays.asList(
//                "Origin", "Content-Type", "Accept", "Authorization",
//                "Access-Control-Request-Method", "Access-Control-Request-Headers",
//                "X-Requested-With", "Cache-Control", "*"
//        ));
//        config.setAllowedMethods(Arrays.asList(
//                "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
//        ));
//        config.setExposedHeaders(Arrays.asList(
//                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Content-Type"
//        ));
//        config.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return new CorsFilter(source);
//    }
}
