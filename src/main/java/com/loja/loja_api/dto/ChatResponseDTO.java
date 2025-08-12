package com.loja.loja_api.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatResponseDTO {
    private final String response;
    private final String timestamp;
    private final boolean success;

    public ChatResponseDTO(String response, boolean success) {
        this.response = response;
        this.success = success;
        this.timestamp = LocalDateTime.now().toString();
    }
}
