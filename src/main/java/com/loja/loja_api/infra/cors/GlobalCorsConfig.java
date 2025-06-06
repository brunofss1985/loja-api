package com.loja.loja_api.infra.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Libera envio de cookies ou cabeçalhos de autenticação
        config.setAllowCredentials(true);

        // Use allowedOriginPatterns ao invés de allowedOrigins quando allowCredentials=true
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:4200"
                // Adicione outros domínios específicos se necessário (como produção)
                // "https://app.loja.com.br"
        ));

        // Permite os headers usados pelo frontend
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Tempo de cache do preflight (em segundos)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
