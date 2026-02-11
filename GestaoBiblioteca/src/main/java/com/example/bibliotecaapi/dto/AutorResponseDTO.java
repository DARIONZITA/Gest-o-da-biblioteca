package com.example.bibliotecaapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para retornar dados de Autor ao cliente
 */
@Data //cria getters e setters automatcly
@NoArgsConstructor //construtor pattern
@AllArgsConstructor //construtor com todos atributos
public class AutorResponseDTO {
    private UUID id;
    private String nome;
    private String descricao;
    private LocalDateTime dataCadastro;
}