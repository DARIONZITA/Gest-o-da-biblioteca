package com.example.bibliotecaapi.repository;

import com.example.bibliotecaapi.model.Autor;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// JpaRepository<Entidade, TipoDoID>
public interface AutorRepository extends JpaRepository<Autor, UUID>  {
    List<Autor> findByNomeContaining(String nome);

    Optional<Autor> findByNome(@NonNull @NotBlank(message = "O nome é obrigatorio") String nome);

    //@Query("SELECT a FROM Autor a WHERE a.nacionalidade = :pais AND a.nome LIKE %:prefixo% ORDER BY a.nome ASC")
    //List<Autor> buscarPorPaisENome(@Param("pais") String pais, @Param("prefixo") String prefixo)
}
