package com.example.bibliotecaapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class EmprestimoRascunhoRequestDTO {

    @NotNull(message = "ID do livro é obrigatório")
    private UUID livroId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataPrevista;
}
