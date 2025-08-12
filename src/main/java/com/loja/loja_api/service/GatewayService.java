package com.loja.loja_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loja.loja_api.model.PagamentoResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GatewayService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ACCESS_TOKEN = "APP_USR-SEU_ACCESS_TOKEN";

    public PagamentoResponse criarPagamento() {
        String url = "https://api.mercadopago.com/v1/payments";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "transaction_amount", 100.0,
                "description", "Pagamento via Pix",
                "payment_method_id", "pix",
                "payer", Map.of("email", "cliente@email.com")
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        try {
            JsonNode json = objectMapper.readTree(response.getBody());

            PagamentoResponse resp = new PagamentoResponse();
            JsonNode transactionData = json.path("point_of_interaction").path("transaction_data");

            resp.setQrCode(transactionData.path("qr_code").asText(null));
            resp.setQrCodeBase64(transactionData.path("qr_code_base64").asText(null));
            resp.setBoletoUrl(json.path("transaction_details").path("external_resource_url").asText(null));

            return resp;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar resposta do Mercado Pago", e);
        }
    }
}
