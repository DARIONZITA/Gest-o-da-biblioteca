package com.example.bibliotecaapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para requisição à API do Groq
 * Documentação: https://console.groq.com/docs/api-reference#chat-create
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroqChatRequestDTO {

    /**
     * Modelo a ser usado (ex: llama-3.3-70b-versatile)
     */
    private String model;

    /**
     * Lista de mensagens da conversa
     */
    private List<Message> messages;

    /**
     * Temperatura (0-2). Valores mais altos = mais criativo
     */
    private Double temperature;

    /**
     * Máximo de tokens na resposta
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role; // "system", "user", "assistant"
        private String content;
    }
}
