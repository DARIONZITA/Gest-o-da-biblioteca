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
}