package com.example.bibliotecaapi.repository;

import com.example.bibliotecaapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    List<Usuario> findByNomeContaining(String nome);
    Optional<Usuario> findByMatricula(Integer matricula);
    Optional<Usuario> findByEmail(String email);
}