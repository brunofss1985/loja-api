package com.loja.loja_api.models;

import lombok.Getter;

@Getter
public class ChangePasswordResult {
    private final boolean success;
    private final String message;

    private ChangePasswordResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ChangePasswordResult success() {
        return new ChangePasswordResult(true, "Senha alterada com sucesso.");
    }

    public static ChangePasswordResult failure(String message) {
        return new ChangePasswordResult(false, message);
    }
}