package com.loja.loja_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.loja.loja_api.repositories")
public class LojaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LojaApiApplication.class, args);
    }

}