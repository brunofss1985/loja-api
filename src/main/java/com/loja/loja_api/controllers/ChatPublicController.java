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
@RequestMapping("/public/chat")
//@CrossOrigin(
//        origins = "*",
//        allowedHeaders = "*",
//        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
//)
public class ChatPublicController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatResponseDTO> sendMessage(@RequestBody ChatRequestDTO request) {
        System.out.println("=== MENSAGEM RECEBIDA: " + request.getMessage() + " ===");

        // SEMPRE TENTA OPENAI PRIMEIRO - SEM CONDIÇÕES
        try {
            System.out.println("=== FORÇANDO TENTATIVA OPENAI ===");
            String response = chatbotService.processMessage(request.getMessage());

            System.out.println("=== ✅ SUCESSO OPENAI: " + response + " ===");
            return ResponseEntity.ok(new ChatResponseDTO(response, true));

        } catch (Exception e) {
            System.err.println("=== ❌ OPENAI FALHOU: " + e.getMessage() + " ===");

            // MENSAGEM DE ERRO ESPECÍFICA - NÃO USA FALLBACK
            String errorMessage = "⚠️ **Assistente temporariamente indisponível**\n\n" +
                    "Nosso assistente inteligente está com problemas técnicos no momento.\n\n" +
                    "**Para atendimento imediato:**\n" +
                    "📞 Telefone: (11) 3333-4444\n" +
                    "💬 WhatsApp: (11) 99999-9999\n" +
                    "📧 Email: contato@supplementstore.com\n\n" +
                    "**Horário**: Segunda a sábado, 8h às 20h\n\n" +
                    "Tente novamente em alguns minutos. Obrigado! 😊";

            System.out.println("=== RESPOSTA DE ERRO ENVIADA ===");
            return ResponseEntity.ok(new ChatResponseDTO(errorMessage, false));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        System.out.println("=== HEALTH ENDPOINT FUNCIONOU ===");

        boolean openaiConfigured = chatbotService.isOpenAIConfigured();

        return ResponseEntity.ok(Map.of(
                "status", "online",
                "service", "chatbot",
                "openai_configured", openaiConfigured,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}