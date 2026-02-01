package com.example.bibliotecaapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditoraResponseDTO {
    private UUID id;
    private String nome;
    private String pais;
    private LocalDateTime dataCadastro;
}