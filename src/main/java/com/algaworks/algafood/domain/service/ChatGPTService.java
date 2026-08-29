package com.algaworks.algafood.domain.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGPTService {

	//NECESSARIO TER CREDITOS NA CONTA DO PERFIL DO OPEN AI PARA FUNCIONAR https://platform.openai.com/settings/organization/billing/overview
	// SE TIVER CREDITOS, TEM QUE GERAR NOVA CHAVE DE API https://platform.openai.com/settings/organization/api-keys
	
	private static final Logger logger = LoggerFactory.getLogger(ChatGPTService.class);

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    public String getChatGPTResponse(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("model", "gpt-4o-mini");
        requestPayload.put("messages", Arrays.asList(
            Map.of("role", "user", "content", prompt)
        ));
        requestPayload.put("max_tokens", 100);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestPayload, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            apiUrl,
            HttpMethod.POST,
            request,
            String.class
        );

        logger.info("Requisição enviada para ChatGPT com sucesso. Prompt: '{}' | Resposta recebida", prompt);

        return response.getBody();
    }
}
