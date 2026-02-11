package com.example.bibliotecaapi.repository;



import com.example.bibliotecaapi.model.Emprestimo;
import com.example.bibliotecaapi.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, UUID> {
    Optional<Emprestimo> findById(UUID id);

    java.util.List<Emprestimo> findByUsuarioId(UUID usuarioId);

    boolean existsByUsuarioIdAndLivroIdAndStatusIn(UUID usuarioId, UUID livroId, List<StatusEmprestimo> status);

    boolean existsByUsuarioIdAndLivroIdAndDataDevolucaoRealIsNull(UUID usuarioId, UUID livroId);

    @Query("SELECT COUNT(e) > 0 FROM Emprestimo e " +
            "WHERE e.usuario.id = :usuarioId " +
            "AND e.dataDevolucaoReal IS NULL " +
            "AND e.dataPrevista < :hoje")
    boolean existeAtrasoAtivo(@Param("usuarioId") UUID usuarioId, @Param("hoje") LocalDate hoje);

    // Queries para Sistema de Recomendações
    
    // Buscar IDs dos livros já emprestados por um usuário (histórico completo)
    @Query("SELECT DISTINCT e.livro.id FROM Emprestimo e WHERE e.usuario.id = :usuarioId")
    List<UUID> findLivrosIdsByUsuarioId(@Param("usuarioId") UUID usuarioId);

    // Buscar usuários que emprestaram livros similares (collaborative filtering)
    @Query("SELECT DISTINCT e.usuario.id FROM Emprestimo e " +
           "WHERE e.livro.id IN :livrosIds " +
           "AND e.usuario.id != :usuarioId")
    List<UUID> findUsuariosSimilares(@Param("livrosIds") List<UUID> livrosIds, @Param("usuarioId") UUID usuarioId);

    // Contar empréstimos por livro nos últimos N dias (popularidade)
    @Query("SELECT e.livro.id, COUNT(e) FROM Emprestimo e " +
           "WHERE e.dataEmprestimo >= :dataInicio " +
           "GROUP BY e.livro.id " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> findLivrosMaisEmprestados(@Param("dataInicio") LocalDate dataInicio);

    // Buscar categorias preferidas do usuário
    @Query("SELECT e.livro.categoria.id, COUNT(e) FROM Emprestimo e " +
           "WHERE e.usuario.id = :usuarioId " +
           "GROUP BY e.livro.categoria.id " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> findCategoriasPreferidas(@Param("usuarioId") UUID usuarioId);

}
