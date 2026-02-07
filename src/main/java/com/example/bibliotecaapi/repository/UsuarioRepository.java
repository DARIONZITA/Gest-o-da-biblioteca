package com.example.bibliotecaapi.repository;

import com.example.bibliotecaapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    // Buscar por email (para login)
    Optional<Usuario> findByEmail(String email);

    // Buscar por CPF
    Optional<Usuario> findByCpf(String cpf);

    // Buscar por nome (contendo)
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    // Buscar por tipo de usuário
    List<Usuario> findByTipo(com.example.bibliotecaapi.model.TipoUsuario tipo);

    // Buscar usuários ativos/inativos
    List<Usuario> findByAtivo(boolean ativo);

    // Buscar por email ou CPF (para verificar duplicidade)
    @Query("SELECT u FROM Usuario u WHERE u.email = :email OR u.cpf = :cpf")
    List<Usuario> findByEmailOrCpf(@Param("email") String email, @Param("cpf") String cpf);

    // Buscar por data de nascimento após uma data específica
    List<Usuario> findByDataNascimentoAfter(java.time.LocalDate data);
}