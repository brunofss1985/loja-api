package com.loja.loja_api.infra.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Permite todas as URLs da API
                .allowedOrigins("http://localhost:4200") // Permite apenas o frontend no localhost:4200
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
                .allowedHeaders("Content-Type", "Authorization")  // Cabeçalhos permitidos
                .allowCredentials(true);
    }
}