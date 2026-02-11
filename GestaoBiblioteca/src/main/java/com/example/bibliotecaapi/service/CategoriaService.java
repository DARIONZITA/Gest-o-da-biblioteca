package com.example.bibliotecaapi.service;


import com.example.bibliotecaapi.model.Categoria;
import com.example.bibliotecaapi.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository repository;

    public Categoria salvar(Categoria categoria) {
        repository.findByNome(categoria.getNome())
                .ifPresent(c -> { throw new RuntimeException("Categoria já cadastrada!"); });
        return repository.save(categoria);
    }

    public List<Categoria> listarTodas() {
        return repository.findAll();
    }

    public Optional<Categoria> buscarPorId(UUID id) {return repository.findById(id);}

    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}
