package com.example.bibliotecaapi.dto;

import com.example.bibliotecaapi.model.StatusReserva;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {
    private UUID id;
    private String nomeUsuario;
    private String tituloLivro;
    private Integer posicaoFila;
    private StatusReserva status;
    private LocalDate dataReserva;
}