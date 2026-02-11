package com.example.bibliotecaapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O nome da categoria é obrigatório")
    @Column(unique = true, nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    @Column(length = 255)
    private String descricao;
}