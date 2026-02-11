package com.example.bibliotecaapi.repository;

import com.example.bibliotecaapi.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    Optional<Categoria> findByNome(String nome);
}