package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.dto.EditoraRequestDTO;
import com.example.bibliotecaapi.model.Editora;
import com.example.bibliotecaapi.repository.EditoraRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EditoraService {

    private final EditoraRepository repository;

    @Transactional
    public Editora salvar(Editora editora) {
        validarNomeUnico(editora.getNome(), null);
        return repository.save(editora);
    }

    public List<Editora> listarTodos() {
        return repository.findAll();
    }

    public Optional<Editora> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public Editora atualizar(UUID id, EditoraRequestDTO dto) {
        Editora editoraExistente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Editora não encontrada com id: " + id));

        validarNomeUnico(dto.getNome(), id);

        editoraExistente.setNome(dto.getNome());
        editoraExistente.setPais(dto.getPais());

        return repository.save(editoraExistente);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Editora não encontrada com id: " + id);
        }
        repository.deleteById(id);
    }

    private void validarNomeUnico(String nome, UUID idExcluir) {
        boolean nomeExistente;

        if (idExcluir == null) {
            // Validação para nova editora
            nomeExistente = repository.existsByNome(nome);
        } else {
            // Validação para atualização (excluir a própria editora)
            nomeExistente = repository.existsByNomeAndIdNot(nome, idExcluir);
        }

        if (nomeExistente) {
            throw new IllegalArgumentException("Já existe uma editora com o nome: " + nome);
        }
    }
}