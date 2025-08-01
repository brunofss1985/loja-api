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

    public boolean isOpenAIConfigured() {
        return openaiApiKey != null &&
                !openaiApiKey.trim().isEmpty() &&
                !openaiApiKey.equals("sk-sua-chave-aqui");
    }

    public String processMessage(String userMessage) {
        System.out.println("=== PROCESSANDO MENSAGEM VIA OPENAI ===");

        if (!isOpenAIConfigured()) {
            throw new RuntimeException("OpenAI API key não configurada");
        }

        try {
            String systemPrompt = """
            Você é um assistente inteligente da SupplementStore, loja especializada em suplementos.

            PRODUTOS DISPONÍVEIS:
            - Whey Protein Premium: R$ 89,90 - Proteína para ganho de massa muscular
            - BCAA 2:1:1: R$ 45,90 - Aminoácidos para recuperação muscular
            - Creatina Monohidratada: R$ 35,90 - Aumenta força e potência muscular

            INFORMAÇÕES DA LOJA:
            - Frete grátis acima de R$ 99,00
            - Entrega: 3-7 dias úteis para todo o Brasil
            - Horário de atendimento: Segunda a sábado, 8h às 20h
            - Processamento de pedidos: até 24h úteis

            INSTRUÇÕES DE USO:
            - Whey: 1 scoop (30g) com água após o treino
            - BCAA: 5g antes e 5g após o treino
            - Creatina: 3-5g diariamente, preferencialmente pós-treino

            Seja sempre útil, amigável e focado em ajudar o cliente com informações sobre suplementos.
            Responda de forma concisa e objetiva, usando emojis quando apropriado.
            Se perguntarem sobre produtos que não temos, sugira alternativas dos nossos produtos.
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

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.openai.com/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");

                if (choices != null && !choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");

                    System.out.println("=== OPENAI RESPONDEU COM SUCESSO ===");
                    return content;
                }
            }

            throw new RuntimeException("Resposta inválida da OpenAI");

        } catch (Exception e) {
            System.err.println("=== ERRO NO SERVICE OPENAI: " + e.getMessage() + " ===");
            throw new RuntimeException("Erro na API OpenAI: " + e.getMessage());
        }
    }

    // MÉTODO REMOVIDO - processMessageLocal() não é mais usado
}