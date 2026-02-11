package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.model.Autor;
import com.example.bibliotecaapi.repository.AutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AutorService {

    // Injeção de dependência (o Spring traz o repository pronto)
    private final AutorRepository repository;

    public AutorService(AutorRepository repository) {
        this.repository = repository;
    }

    public Autor salvar(Autor autor) {
        repository.findByNome(autor.getNome())
                .ifPresent(a -> {
                    throw new RuntimeException("Já existe um autor cadastrado com o nome: " + autor.getNome());
                });
        return repository.save(autor);
    }

    public List<Autor> listarTodos() {
        return repository.findAll();
    }

    public Optional<Autor> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    public void deletar(UUID id) {
        repository.deleteById(id);
    }

    public Autor findOrCreate(String nome) {
        if (nome == null || nome.trim().isEmpty())
            return null;
        String nomeTrim = nome.trim();
        return repository.findByNome(nomeTrim)
                .orElseGet(() -> {
                    Autor novo = new Autor();
                    novo.setNome(nomeTrim);
                    novo.setDescricao("Autor cadastrado automaticamente via OCR");
                    return repository.save(novo);
                });
    }
}