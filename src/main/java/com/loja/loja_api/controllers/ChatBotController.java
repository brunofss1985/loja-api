package com.loja.loja_api.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        System.out.println("=== HEALTH ENDPOINT FUNCIONOU ===");

        Map<String, Object> response = new HashMap<>();
        response.put("service", "chatbot");
        response.put("status", "online");
        response.put("openai_configured", isOpenAIConfigured());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        try {
            String userMessage = request.get("message");
            System.out.println("=== MENSAGEM RECEBIDA: " + userMessage + " ===");

            String aiResponse;
            String source;

            if (isOpenAIConfigured()) {
                try {
                    System.out.println("=== TENTANDO OPENAI ===");
                    aiResponse = callOpenAI(userMessage);
                    source = "openai";
                } catch (Exception openaiError) {
                    System.err.println("=== OPENAI FALHOU, USANDO FALLBACK ===");
                    System.err.println("Erro: " + openaiError.getMessage());
                    aiResponse = generateQuickResponse(userMessage);
                    source = "fallback";
                }
            } else {
                System.out.println("=== OPENAI NÃO CONFIGURADA, USANDO FALLBACK ===");
                aiResponse = generateQuickResponse(userMessage);
                source = "fallback";
            }

            Map<String, Object> response = new HashMap<>();
            response.put("response", aiResponse);
            response.put("source", source);
            response.put("timestamp", LocalDateTime.now());

            System.out.println("=== RESPOSTA ENVIADA (" + source + "): " + aiResponse + " ===");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Erro geral no chat: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("response", "Erro temporário. Tente novamente.");
            errorResponse.put("source", "error");
            errorResponse.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(errorResponse);
        }
    }

    private boolean isOpenAIConfigured() {
        return openaiApiKey != null &&
                !openaiApiKey.trim().isEmpty() &&
                !openaiApiKey.equals("sk-sua-chave-aqui");
    }

    private String callOpenAI(String message) {
        try {
            System.out.println("=== INICIANDO CHAMADA OPENAI ===");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", "Você é um assistente de loja online. Seja útil e direto."),
                    Map.of("role", "user", "content", message)
            ));
            requestBody.put("max_tokens", 150);
            requestBody.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    openaiApiUrl,
                    entity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> messageObj = (Map<String, Object>) firstChoice.get("message");

            return (String) messageObj.get("content");

        } catch (Exception e) {
            System.err.println("=== ERRO NA OPENAI: " + e.getMessage());
            throw new RuntimeException("Erro na OpenAI: " + e.getMessage());
        }
    }

    private String generateQuickResponse(String userMessage) {
        String message = userMessage.toLowerCase();

        if (message.contains("ola") || message.contains("oi")) {
            return "Olá! Como posso ajudar? 😊";
        }

        if (message.contains("produto")) {
            return "Temos vários produtos! O que procura?";
        }

        if (message.contains("preco")) {
            return "Os preços variam. Qual produto interessa?";
        }

        if (message.contains("entrega")) {
            return "Entregamos em todo Brasil! 📦";
        }

        if (message.contains("obrigado")) {
            return "De nada! Sempre à disposição! 😄";
        }

        // Respostas rápidas aleatórias
        String[] respostas = {
                "Posso ajudar com isso! 👍",
                "Interessante! Me conte mais.",
                "Estou aqui para ajudar! ✨",
                "Que legal! Como posso auxiliar?",
                "Entendi! Precisa de mais info?"
        };

        return respostas[random.nextInt(respostas.length)];
    }
}