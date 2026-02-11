package com.example.bibliotecaapi.repository;

import com.example.bibliotecaapi.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface LivroRepository  extends JpaRepository<Livro, UUID>{
    List<Livro> findByTituloContaining(String titulo);

    void deleteByIsbn(String isbn);

    Optional<Livro> findByIsbn(String isbn);

    // Queries para Sistema de Recomendações
    
    // Buscar livros por categoria que não foram lidos pelo usuário
    @Query("SELECT l FROM Livro l WHERE l.categoria.id = :categoriaId " +
           "AND l.id NOT IN :livrosLidosIds " +
           "AND l.qtdDisponivel > 0 " +
           "ORDER BY l.titulo")
    List<Livro> findByCategoriaExcludingLivros(
        @Param("categoriaId") UUID categoriaId, 
        @Param("livrosLidosIds") List<UUID> livrosLidosIds
    );

    // Buscar livros por autor que não foram lidos pelo usuário
    @Query("SELECT l FROM Livro l WHERE l.autor.id = :autorId " +
           "AND l.id NOT IN :livrosLidosIds " +
           "AND l.qtdDisponivel > 0 " +
           "ORDER BY l.titulo")
    List<Livro> findByAutorExcludingLivros(
        @Param("autorId") UUID autorId, 
        @Param("livrosLidosIds") List<UUID> livrosLidosIds
    );

    // Buscar livros disponíveis excluindo os já lidos
    @Query("SELECT l FROM Livro l WHERE l.qtdDisponivel > 0 " +
           "AND l.id NOT IN :livrosLidosIds")
    List<Livro> findDisponivelExcludingLivros(@Param("livrosLidosIds") List<UUID> livrosLidosIds);
}
