package com.example.bibliotecaapi.repository;

import com.example.bibliotecaapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    boolean existsByEmail(String email);
    boolean existsByMatricula(String matricula);
    boolean existsByEmailAndIdNot(String email, UUID id);
    boolean existsByMatriculaAndIdNot(String matricula, UUID id);
    Optional<Usuario> findByEmail(String email);
}