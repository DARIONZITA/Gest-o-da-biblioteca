package com.example.bibliotecaapi.dto;

import com.example.bibliotecaapi.model.PerfilUsuario;
import com.example.bibliotecaapi.model.StatusUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private UUID id;
    private Integer matricula;
    private String nome;
    private String email;
    private PerfilUsuario perfil;
    private StatusUsuario status;
    private LocalDateTime dataCadastro;
}