package com.example.bibliotecaapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    private String descricao;
}
