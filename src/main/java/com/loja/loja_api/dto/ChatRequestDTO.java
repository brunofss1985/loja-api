package com.loja.loja_api.dto;

public class ChatRequestDTO {
    private String message;
    private String userId;
    
    public ChatRequestDTO() {}
    
    public ChatRequestDTO(String message, String userId) {
        this.message = message;
        this.userId = userId;
    }
    
    public String getMessage() { 
        return message; 
    }
    
    public void setMessage(String message) { 
        this.message = message; 
    }
    
    public String getUserId() { 
        return userId; 
    }
    
    public void setUserId(String userId) { 
        this.userId = userId; 
    }
}