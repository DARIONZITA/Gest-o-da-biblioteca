package com.example.bibliotecaapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LivroResponseDTO {
    private UUID id;
    private String titulo;
    private String isbn;
    private String nomeAutor;
    private String nomeCategoria;
    private String nomeEditora;
    private Integer anoPublicacao;
    private Integer qtdPaginas;
    private Integer qtdTotal;
    private Integer qtdDisponivel;
    private String localizacao;
    private String capaURL;
    private String sinopse;
}
