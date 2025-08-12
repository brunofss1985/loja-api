package com.loja.loja_api.controllers;

import com.loja.loja_api.service.ProductChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")

public class ChatBotController {

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {

        Map<String, Object> response = new HashMap<>();
        response.put("service", "chatbot");
        response.put("status", "online");
        response.put("openai_configured", isOpenAIConfigured());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        // SEMPRE TENTA OPENAI - SEM VERIFICAÇÕES PRÉVIAS
        try {
            String aiResponse = callOpenAI(userMessage);

            Map<String, Object> response = new HashMap<>();
            response.put("response", aiResponse);
            response.put("source", "openai");
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            // MENSAGEM DE ERRO ESPECÍFICA
            String errorMessage = "⚠️ **Assistente temporariamente indisponível**\n\n" +
                    "Nosso assistente inteligente está com problemas técnicos.\n\n" +
                    "**Contate-nos:**\n" +
                    "📞 (11) 3333-4444 | 💬 (11) 99999-9999\n" +
                    "📧 contato@supplementstore.com\n\n" +
                    "**Horário**: Seg-Sáb, 8h-20h\n\n" +
                    "Tente novamente em alguns minutos! 😊";

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("response", errorMessage);
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

    @Autowired
    private ProductChatService productChatService;

    private String callOpenAI(String message) {

        if (!isOpenAIConfigured()) {
            throw new RuntimeException("API Key da OpenAI não configurada corretamente");
        }

        try {
            // BUSCA PRODUTOS DO BANCO VIA SERVICE
            String dynamicProducts = productChatService.getProductsForChatbot();
            String storeInfo = productChatService.getStoreInfo();


            String systemPrompt = String.format("""
        Você é um assistente inteligente da SupplementStore, especializada em suplementos.

        %s

        %s

        INSTRUÇÕES DE USO GERAIS:
        - Whey Protein: 1 scoop (30g) com água após treino
        - BCAA: 5g antes e 5g após treino
        - Creatina: 3-5g diariamente pós-treino
        - Outros: consulte instruções específicas

        Seja útil, amigável e use emojis. Foque em suplementos e informações da loja.
        Sempre mencione preços atualizados e incentive compras.
        Destaque produtos em promoção e estoques limitados.
        """, dynamicProducts, storeInfo);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", message)
            ));
            requestBody.put("max_tokens", 400);
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

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

                if (choices != null && !choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> firstChoice = choices.get(0);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> messageObj = (Map<String, Object>) firstChoice.get("message");

                    String content = (String) messageObj.get("content");
                    return content;
                }
            }

            throw new RuntimeException("Resposta inválida da OpenAI");

        } catch (Exception e) {
            System.err.println("=== ERRO DETALHADO NA OPENAI: " + e.getClass().getSimpleName() + " - " + e.getMessage() + " ===");
            throw new RuntimeException("Falha na comunicação com OpenAI: " + e.getMessage());
        }
    }
}