package com.example.bibliotecaapi.dto;

import com.example.bibliotecaapi.model.PerfilUsuario;
import com.example.bibliotecaapi.model.StatusUsuario;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

    @NotNull(message = "A matrícula é obrigatória")
    @Positive(message = "A matrícula deve ser superior a zero")
    private Integer matricula;

    @NotBlank(message = "O nome do usuário é obrigatório")
    @Size(min = 10, max = 60, message = "O nome deve ter entre 10 e 60 caracteres")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido, tente novamente com outro email")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
    private String senha;

    @NotNull
    private PerfilUsuario perfil;

    @NotNull
    private StatusUsuario status;
}