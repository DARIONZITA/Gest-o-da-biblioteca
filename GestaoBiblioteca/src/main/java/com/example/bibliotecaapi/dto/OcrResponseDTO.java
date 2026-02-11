package com.example.bibliotecaapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta do OCR
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrResponseDTO {

    /**
     * Texto completo extraído da imagem
     */
    private String textoCompleto;

    /**
     * Título do livro detectado
     */
    private String titulo;

    /**
     * Autor detectado
     */
    private String autor;

    /**
     * ISBN detectado
     */
    private String isbn;

    /**
     * Editora detectada
     */
    private String editora;

    /**
     * Ano de publicação detectado
     */
    private Integer ano;

    /**
     * Confiança da extração (0-100)
     */
    private Double confianca;

    private Integer qtdPaginas;
    private String sinopse;
    private String capaUrl;
    private String categoria;

    /**
     * Mensagem de status do processamento
     */
    private String mensagem;

    /**
     * Indica se o processamento foi bem-sucedido
     */
    private Boolean sucesso;
}
