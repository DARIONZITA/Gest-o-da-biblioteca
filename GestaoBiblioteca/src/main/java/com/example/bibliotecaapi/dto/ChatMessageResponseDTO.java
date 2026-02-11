package com.example.bibliotecaapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta do chatbot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDTO {

    private String resposta;
    
    private String sessaoId;
    
    private LocalDateTime timestamp;
    
    /**
     * Livros sugeridos pelo chatbot (se aplicável)
     */
    private List<RecomendacaoResponseDTO> livrosSugeridos;
    
    /**
     * Confidence score da resposta (0-100)
     */
    private Integer confianca;

    public ChatMessageResponseDTO(String resposta, String sessaoId) {
        this.resposta = resposta;
        this.sessaoId = sessaoId;
        this.timestamp = LocalDateTime.now();
        this.confianca = 100;
    }
}
