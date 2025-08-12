package com.loja.loja_api.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ProductChatService productChatService;

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

        if (!isOpenAIConfigured()) {
            throw new RuntimeException("OpenAI API key não configurada");
        }

        try {
            // BUSCA PRODUTOS DINÂMICOS DO BANCO
            String dynamicProducts = productChatService.getProductsForChatbot();
            String storeInfo = productChatService.getStoreInfo();


            String systemPrompt = String.format("""
            Você é um assistente inteligente da SupplementStore, loja especializada em suplementos.

            %s

            %s

            INSTRUÇÕES DE USO GERAIS:
            - Whey Protein: 1 scoop (30g) com água após o treino
            - BCAA: 5g antes e 5g após o treino  
            - Creatina: 3-5g diariamente, preferencialmente pós-treino
            - Outros suplementos: consulte as instruções específicas do produto

            REGRAS IMPORTANTES:
            - Seja sempre útil, amigável e focado em ajudar o cliente
            - Responda de forma concisa e objetiva, usando emojis quando apropriado
            - Se perguntarem sobre produtos que não temos, sugira alternativas dos nossos produtos
            - Para informações específicas de produtos não listados, oriente a entrar em contato
            - Sempre mencione preços atualizados quando disponíveis
            - Incentive a compra destacando benefícios dos produtos
            - Se houver produtos com estoque limitado, mencione a urgência
            - Destaque produtos em promoção quando houver preço com desconto
            """, dynamicProducts, storeInfo);

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userMessage)
                    ),
                    "max_tokens", 400,
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

                    return content;
                }
            }

            throw new RuntimeException("Resposta inválida da OpenAI");

        } catch (Exception e) {
            System.err.println("=== ERRO NO SERVICE OPENAI: " + e.getMessage() + " ===");
            throw new RuntimeException("Erro na API OpenAI: " + e.getMessage());
        }
    }
}