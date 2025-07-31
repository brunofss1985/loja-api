package com.loja.loja_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    private final RestTemplate restTemplate;

    public ChatbotService() {
        this.restTemplate = new RestTemplate();
    }

    public String processMessage(String userMessage) {
        try {
            String systemPrompt = """
                Você é um assistente inteligente de uma loja de suplementos chamada SupplementStore.
                
                Produtos disponíveis:
                - Whey Protein Premium: R$ 89,90 - Proteína para ganho de massa
                - BCAA 2:1:1: R$ 45,90 - Aminoácidos para recuperação
                - Creatina Monohidratada: R$ 35,90 - Aumenta força e potência
                
                Informações da loja:
                - Frete grátis acima de R$ 99,00
                - Entrega: 3-7 dias úteis
                - Horário: Segunda a sábado, 8h às 20h
                
                Seja sempre útil, amigável e focado em ajudar o cliente.
                Responda de forma concisa e objetiva.
                """;

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userMessage)
                    ),
                    "max_tokens", 300,
                    "temperature", 0.7
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/chat/completions",
                    entity,
                    Map.class
            );

            if (response.getBody() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices =
                        (List<Map<String, Object>>) response.getBody().get("choices");

                if (choices != null && !choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message =
                            (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }

            return "Desculpe, não consegui processar sua mensagem.";

        } catch (Exception e) {
            System.err.println("Erro na API OpenAI: " + e.getMessage());
            return "Desculpe, ocorreu um erro. Tente novamente em alguns instantes.";
        }
    }

    // Método para usar sem OpenAI (para testes)
    public String processMessageLocal(String userMessage) {
        String message = userMessage.toLowerCase();

        if (message.contains("massa") || message.contains("ganhar peso") || message.contains("hipertrofia")) {
            return "Para ganhar massa muscular, recomendo nosso **Whey Protein Premium** (R$ 89,90). " +
                    "É uma proteína de alta qualidade que ajuda na construção muscular. " +
                    "Combine com **Creatina** (R$ 35,90) para melhores resultados! 💪";
        }

        if (message.contains("whey") || message.contains("proteína")) {
            return "Nosso **Whey Protein Premium** custa R$ 89,90. " +
                    "É ideal para ganho de massa muscular. " +
                    "**Como usar**: 1 scoop (30g) com água após o treino. 🥤";
        }

        if (message.contains("bcaa") || message.contains("recuperação") || message.contains("aminoácido")) {
            return "O **BCAA 2:1:1** custa R$ 45,90 e é excelente para recuperação! " +
                    "**Como usar**: 5g antes e 5g após o treino. " +
                    "Ajuda a reduzir fadiga muscular. 💊";
        }

        if (message.contains("creatina")) {
            return "Nossa **Creatina Monohidratada** custa R$ 35,90. " +
                    "Aumenta força e potência muscular. " +
                    "**Como usar**: 3-5g diariamente, preferencialmente pós-treino. ⚡";
        }

        if (message.contains("frete") || message.contains("entrega") || message.contains("envio")) {
            return "🚚 **Informações de Frete**: " +
                    "✅ Frete grátis para compras acima de R$ 99,00 " +
                    "⏱️ Prazo: 3-7 dias úteis " +
                    "🌎 Entregamos para todo o Brasil";
        }

        if (message.contains("horário") || message.contains("atendimento")) {
            return "📅 **Horários**: " +
                    "🕐 Atendimento: Segunda a sábado, 8h às 20h " +
                    "🚚 Entregas: Segunda a sexta, 8h às 18h " +
                    "📦 Processamento: Pedidos em até 24h úteis";
        }

        if (message.contains("preço") || message.contains("valor") || message.contains("custa")) {
            return "💰 **Nossos preços**: " +
                    "🥤 Whey Protein Premium: R$ 89,90 " +
                    "💊 BCAA 2:1:1: R$ 45,90 " +
                    "⚡ Creatina Monohidratada: R$ 35,90 " +
                    "🎁 Frete grátis acima de R$ 99!";
        }

        if (message.contains("como usar") || message.contains("como tomar") || message.contains("dosagem")) {
            return "📋 **Como usar nossos produtos**: " +
                    "🥤 **Whey**: 1 scoop (30g) com água após treino " +
                    "💊 **BCAA**: 5g antes e 5g após treino " +
                    "⚡ **Creatina**: 3-5g diariamente pós-treino";
        }

        if (message.contains("oi") || message.contains("olá") || message.contains("bom dia") || message.contains("boa tarde")) {
            return "Olá! 😊 Bem-vindo à SupplementStore! " +
                    "Sou seu assistente inteligente. Posso ajudar com: " +
                    "• Recomendações de produtos " +
                    "• Informações sobre frete " +
                    "• Dicas de uso dos suplementos " +
                    "• Preços e promoções. Como posso ajudar?";
        }

        return "Entendi sua pergunta! 🤔 " +
                "Posso ajudar com informações sobre: " +
                "• **Produtos** (whey, BCAA, creatina) " +
                "• **Horários** de entrega e atendimento " +
                "• **Frete** e prazos " +
                "• **Dicas** de uso dos suplementos. " +
                "Pode me perguntar algo específico?";
    }
}