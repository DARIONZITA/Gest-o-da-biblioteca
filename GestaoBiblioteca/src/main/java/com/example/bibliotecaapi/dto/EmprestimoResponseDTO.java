package com.example.bibliotecaapi.dto;

import com.example.bibliotecaapi.model.StatusEmprestimo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoResponseDTO {
    private UUID id;
    private String nomeUsuario;
    private String tituloLivro;
    private Integer qtdRenovacoes;
    private StatusEmprestimo status;
    private Double valorMulta;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevista;
    private LocalDate dataDevolucaoReal;
}