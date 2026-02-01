package com.example.bibliotecaapi.repository;

import com.example.bibliotecaapi.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    Optional<Categoria> findByNome(String nome);

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, UUID id);
}
