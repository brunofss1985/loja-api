package com.loja.loja_api.dto;

public record RegisterRequestDTO(String name, String email, String password, String userType) {
}
