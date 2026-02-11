package com.example.bibliotecaapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "tb_livros")

public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    @NotBlank(message = "O título é obrigatorio")
    @Column(nullable = false, length = 150)
    private String titulo;

    @NonNull
    @NotBlank(message = "O código ISBN é obrigatorio")
    @Column(nullable = false, length = 150)
    private String isbn;

    @NotNull(message = "O número de páginas não pode ser vazio")
    @Min(value = 10, message = "O livro deve ter pelo menos 10 páginas")
    @Max(value = 99999, message = "Número de páginas inválido")
    @Column(nullable = false)
    private Integer qtdPaginas;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "autor_id", nullable = false)
    private Autor autor;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "editora_id")
    private Editora editora;

    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    @NotNull(message = "Deve colocar quantidade total deste livro no estoque")
    @PositiveOrZero(message = "Não são permitidas quantidades negativas, apenas 0 ou mais livros")
    @Column(nullable = false)
    private Integer qtdTotal;

    @Column(nullable = false)
    private Integer qtdDisponivel;

    private String localizacao;

    private String capaURL;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @PrePersist
    public void prePersist() {
        if (qtdDisponivel == null) {
            qtdDisponivel = qtdTotal;
        }
    }
}
