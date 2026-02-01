package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.dto.CategoriaRequestDTO;
import com.example.bibliotecaapi.model.Categoria;
import com.example.bibliotecaapi.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    @Transactional
    public Categoria salvar(Categoria categoria) {
        validarNomeUnico(categoria.getNome(), null);
        return repository.save(categoria);
    }

    public List<Categoria> listarTodos() {
        return repository.findAll();
    }

    public Optional<Categoria> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public Categoria atualizar(UUID id, CategoriaRequestDTO dto) {
        Categoria categoriaExistente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));

        validarNomeUnico(dto.getNome(), id);

        categoriaExistente.setNome(dto.getNome());
        categoriaExistente.setDescricao(dto.getDescricao());

        return repository.save(categoriaExistente);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Categoria não encontrada com id: " + id);
        }
        repository.deleteById(id);
    }

    private void validarNomeUnico(String nome, UUID idExcluir) {
        boolean nomeExistente;

        if (idExcluir == null) {
            // Validação para nova categoria
            nomeExistente = repository.existsByNome(nome);
        } else {
            // Validação para atualização (excluir a própria categoria)
            nomeExistente = repository.existsByNomeAndIdNot(nome, idExcluir);
        }

        if (nomeExistente) {
            throw new IllegalArgumentException("Já existe uma categoria com o nome: " + nome);
        }
    }
}