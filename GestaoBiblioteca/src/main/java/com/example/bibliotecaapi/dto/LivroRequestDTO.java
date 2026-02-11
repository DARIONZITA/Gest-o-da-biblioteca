package com.example.bibliotecaapi.dto;

import jakarta.persistence.Column;
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
public class LivroRequestDTO {

    @NonNull
    @NotBlank(message = "O título é obrigatorio")
    private String titulo;

    @NonNull
    @NotBlank(message = "O código ISBN é obrigatorio")
    private String isbn;

    @Min(value = 1, message = "O livro deve ter pelo menos 1 página")
    @Max(value = 99999, message = "Número de páginas inválido")
    private int qtdPaginas;

    private Integer anoPublicacao;

    // Nomes para busca/criação automática (se IDs não enviados)
    private String nomeAutor;
    private String nomeEditora;
    private String sinopse;

    // Torna autorId não obrigatório se nomeAutor for enviado (lógica tratada no
    // controller)
    private UUID autorId;

    @NotNull(message = "Id da categoria obrigatória")
    private UUID categoriaId;

    @NotNull(message = "Quantidade total é obrigatória")
    private Integer qtdTotal;

    private String localizacao;

    private String capaURL;
}
