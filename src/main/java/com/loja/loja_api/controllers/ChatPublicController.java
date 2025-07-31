package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.ChatRequestDTO;
import com.loja.loja_api.dto.ChatResponseDTO;
import com.loja.loja_api.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/public/chat") // ENDPOINT DIFERENTE
public class ChatPublicController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatResponseDTO> sendMessage(@RequestBody ChatRequestDTO request) {
        try {
            System.out.println("=== MENSAGEM RECEBIDA: " + request.getMessage() + " ===");
            String response = chatbotService.processMessageLocal(request.getMessage());
            return ResponseEntity.ok(new ChatResponseDTO(response, true));

        } catch (Exception e) {
            System.err.println("Erro no chat: " + e.getMessage());
            return ResponseEntity.ok(
                    new ChatResponseDTO("Desculpe, ocorreu um erro. Tente novamente.", false)
            );
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        System.out.println("=== HEALTH ENDPOINT CHAMADO ===");
        return ResponseEntity.ok(Map.of(
                "status", "online",
                "service", "chatbot",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}