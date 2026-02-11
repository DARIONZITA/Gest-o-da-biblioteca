package com.example.bibliotecaapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacaoResponseDTO {
    private UUID id;
    private String titulo;
    private String isbn;
    private String nomeAutor;
    private String nomeCategoria;
    private Integer qtdPaginas;
    private Integer qtdDisponivel;
    private String localizacao;
    private String capaURL;
    private String sinopse;
    
    // Campos específicos de recomendação
    private Double scoreGeral;
    private Double scoreColaborativo;
    private Double scoreConteudo;
    private Double scorePopularidade;
    private String motivoRecomendacao;
    private Integer confianca; // 0-100
}
