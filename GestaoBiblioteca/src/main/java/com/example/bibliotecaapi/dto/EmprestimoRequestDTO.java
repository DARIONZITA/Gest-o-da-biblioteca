package com.example.bibliotecaapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class EmprestimoRequestDTO {

    @NotNull(message = "ID do usuário é obrigatório")
    private UUID usuarioId;

    @NotNull(message = "ID do livro é obrigatório")
    private UUID livroId;

    @NotNull(message = "A data prevista de devolução é obrigatória")
    @Future(message = "A data prevista deve ser uma data futura")
    @JsonFormat(pattern = "yyyy-MM-dd") // Garante que o Spring entenda este formato
    private LocalDate dataPrevista;
}