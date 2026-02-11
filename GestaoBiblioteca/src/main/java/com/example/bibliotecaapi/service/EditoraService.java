package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.model.Editora;
import com.example.bibliotecaapi.repository.EditoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EditoraService {

    private final EditoraRepository repository;

    @Transactional
    public Editora findOrCreate(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }

        String nomeNormalizado = nome.trim();

        // Tenta encontrar por nome exato ou similar
        // A busca aqui pode ser melhorada com fuzzy search no futuro
        Optional<Editora> existing = repository.findByNomeContainingIgnoreCase(nomeNormalizado);

        return existing.orElseGet(() -> {
            Editora nova = new Editora();
            nova.setNome(nomeNormalizado);
            return repository.save(nova);
        });
    }
}
