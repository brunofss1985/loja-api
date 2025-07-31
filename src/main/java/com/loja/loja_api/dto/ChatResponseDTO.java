package com.loja.loja_api.dto;

import java.time.LocalDateTime;

public class ChatResponseDTO {
    private String response;
    private String timestamp;
    private boolean success;

    public ChatResponseDTO() {}

    public ChatResponseDTO(String response, boolean success) {
        this.response = response;
        this.success = success;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}