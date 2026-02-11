package com.example.bibliotecaapi.service.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class GeminiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key:}")
    private String apiKey;

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    public GeminiClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    public String extractDataFromImage(byte[] imageBytes) {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("API Key do Gemini não configurada!");
            throw new RuntimeException("API Key do Gemini não configurada.");
        }

        try {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Build the request body as a JSON string directly for maximum control
            String requestJson = objectMapper.writeValueAsString(new GeminiRequest(
                    List.of(new GeminiRequest.Content(
                            List.of(
                                    new GeminiRequest.Part(
                                            "Analyze this book cover image. Extract the following information and return ONLY a raw JSON object (no markdown, no code fences) with these exact keys: title, author, isbn, publisher, publishedYear, pageCount, description. If a field is not found, set its value to null. For author, return a single string. For publishedYear and pageCount, return integers or null.",
                                            null),
                                    new GeminiRequest.Part(
                                            null,
                                            new GeminiRequest.InlineData("image/jpeg", base64Image)))))));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = API_URL + "?key=" + apiKey;

            log.info("Chamando Gemini API... (tamanho do payload: {} bytes)", requestJson.length());

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            log.info("Gemini API respondeu com status: {}", responseEntity.getStatusCode());

            if (responseEntity.getBody() != null) {
                GeminiResponse response = objectMapper.readValue(responseEntity.getBody(), GeminiResponse.class);
                if (response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                    String text = response.getCandidates().get(0).getContent().getParts().get(0).getText();
                    // Clean markdown code blocks if present
                    text = text.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
                    log.info("Gemini extraiu: {}", text.substring(0, Math.min(200, text.length())));
                    return text;
                }
            }

            log.warn("Gemini API retornou resposta vazia ou sem candidatos");
            return null;

        } catch (HttpClientErrorException e) {
            log.error("Erro HTTP ao chamar Gemini API: Status={}, Body={}", e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw new RuntimeException(
                    "Erro na comunicação com Gemini API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
                    e);
        } catch (Exception e) {
            log.error("Erro ao chamar Gemini API: {}", e.getMessage(), e);
            throw new RuntimeException("Erro na comunicação com Gemini API: " + e.getMessage(), e);
        }
    }

    // --- Request DTOs ---

    @Data
    public static class GeminiRequest {
        private final List<Content> contents;

        public GeminiRequest(List<Content> contents) {
            this.contents = contents;
        }

        @Data
        public static class Content {
            private final List<Part> parts;

            public Content(List<Part> parts) {
                this.parts = parts;
            }
        }

        @Data
        public static class Part {
            private final String text;

            @JsonProperty("inline_data")
            private final InlineData inlineData;

            public Part(String text, InlineData inlineData) {
                this.text = text;
                this.inlineData = inlineData;
            }
        }

        @Data
        public static class InlineData {
            @JsonProperty("mime_type")
            private final String mimeType;

            private final String data;

            public InlineData(String mimeType, String data) {
                this.mimeType = mimeType;
                this.data = data;
            }
        }
    }

    // --- Response DTOs ---

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeminiResponse {
        private List<Candidate> candidates;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Candidate {
            private Content content;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Content {
            private List<Part> parts;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Part {
            private String text;
        }
    }
}
