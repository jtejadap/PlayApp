package com.proyecto.PlayApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(20);

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    @Value("${gemini.base-url:https://generativelanguage.googleapis.com/v1beta}")
    private String baseUrl;

    public GeminiService(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setReadTimeout(READ_TIMEOUT)
                .build();
    }

    public String generateReply(String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY no configurada");
        }
        if (userMessage == null || userMessage.isBlank()) {
            throw new IllegalArgumentException("El mensaje del usuario es obligatorio");
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(normalizeBaseUrl(baseUrl))
                .pathSegment("models", model + ":generateContent")
                .queryParam("key", apiKey)
                .build()
                .toUri();

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", userMessage))
                ))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST, request, Map.class);
            return extractText(response.getBody());
        } catch (RestClientException ex) {
            throw new IllegalStateException("Error invocando Gemini", ex);
        }
    }

    private String extractText(Map body) {
        if (body == null) {
            throw new IllegalStateException("Respuesta vacia de Gemini");
        }

        Object candidatesObj = body.get("candidates");
        if (!(candidatesObj instanceof List<?> candidates) || candidates.isEmpty()) {
            throw new IllegalStateException("Gemini no devolvio candidatos");
        }

        Object firstObj = candidates.getFirst();
        if (!(firstObj instanceof Map<?, ?> firstCandidate)) {
            throw new IllegalStateException("Formato de respuesta invalido (candidate)");
        }

        Object contentObj = firstCandidate.get("content");
        if (!(contentObj instanceof Map<?, ?> contentMap)) {
            throw new IllegalStateException("Formato de respuesta invalido (content)");
        }

        Object partsObj = contentMap.get("parts");
        if (!(partsObj instanceof List<?> parts) || parts.isEmpty()) {
            throw new IllegalStateException("Formato de respuesta invalido (parts)");
        }

        Object firstPartObj = parts.getFirst();
        if (!(firstPartObj instanceof Map<?, ?> firstPart)) {
            throw new IllegalStateException("Formato de respuesta invalido (part)");
        }

        Object textObj = firstPart.get("text");
        if (!(textObj instanceof String text) || text.isBlank()) {
            throw new IllegalStateException("Gemini devolvio texto vacio");
        }

        return text.trim();
    }

    private String normalizeBaseUrl(String value) {
        if (value == null || value.isBlank()) {
            return "https://generativelanguage.googleapis.com/v1beta";
        }
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}
