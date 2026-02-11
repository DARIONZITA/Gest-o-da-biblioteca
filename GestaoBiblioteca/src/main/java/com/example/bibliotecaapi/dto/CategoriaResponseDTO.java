package com.example.bibliotecaapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CategoriaResponseDTO {
    private UUID id;
    private String nome;
    private String descricao;
}
