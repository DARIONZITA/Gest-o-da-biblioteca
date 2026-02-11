package com.example.bibliotecaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de mensagem do chatbot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDTO {

    @NotBlank(message = "A mensagem não pode estar vazia")
    @Size(max = 1000, message = "A mensagem deve ter no máximo 1000 caracteres")
    private String mensagem;

    /**
     * ID da sessão de conversa (opcional, para manter contexto)
     */
    private String sessaoId;
}
