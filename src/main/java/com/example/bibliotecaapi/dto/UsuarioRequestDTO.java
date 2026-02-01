package com.example.bibliotecaapi.dto;

import com.example.bibliotecaapi.model.PerfilUsuario;
import com.example.bibliotecaapi.model.StatusUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "O nome do usuário é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 100, message = "O email deve ter no máximo 100 caracteres")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
    private String senha;

    @NotBlank(message = "A matrícula é obrigatória")
    @Size(min = 3, max = 20, message = "A matrícula deve ter entre 3 e 20 caracteres")
    private String matricula;

    @NotNull(message = "O perfil é obrigatório")
    private PerfilUsuario perfil;

    private StatusUsuario status;
}