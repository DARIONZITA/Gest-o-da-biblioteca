package com.example.bibliotecaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para receber dados de criação/atualização de Autor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutorRequestDTO {

    @NotBlank(message = "O nome do autor é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    private String nome;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    private String dataNascimento; // Formato: "1990-05-23"
}
