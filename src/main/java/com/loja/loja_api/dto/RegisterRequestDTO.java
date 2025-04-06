package com.loja.loja_api.dto;

import com.loja.loja_api.domain.user.UserType;

public record RegisterRequestDTO(String name, String email, String password, UserType userType) {}
