package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.model.Livro;
import com.example.bibliotecaapi.repository.LivroRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LivroService {
    private final LivroRepository repository;

    public LivroService(LivroRepository repository){
        this.repository = repository;
    }

    public Livro salvar(Livro livro) {
        // Validações de quantidade
        if (livro.getQtdTotal() == null || livro.getQtdTotal() <= 0) {
            throw new RuntimeException("A quantidade total deve ser maior que zero!");
        }
        
        repository.findByIsbn(livro.getIsbn()).ifPresent(l -> {
            throw new RuntimeException("Já existe um livro cadastrado com este ISBN!");
        });

        return repository.save(livro);
    }

    public List<Livro> listarTodos() {
        return repository.findAll();
    }

    public Optional<Livro> buscarPorIsbn(String isbn) {
        return repository.findByIsbn(isbn);
    }

    public void deletar(String isbn) {
        repository.deleteByIsbn(isbn);
    }

    public void baixarEstoque(UUID livroId) {
        Livro livro = repository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        if (livro.getQtdDisponivel()<= 0) {
            throw new RuntimeException("Não há exemplares disponíveis deste livro no momento!");
        }

        livro.setQtdDisponivel(livro.getQtdDisponivel()- 1);

        repository.save(livro);
    }

    // No LivroService.java
    public void aumentarEstoque(UUID livroId) {
        Livro livro = repository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        // Validação: Não permitir que qtdDisponivel ultrapasse qtdTotal
        if (livro.getQtdDisponivel() >= livro.getQtdTotal()) {
            throw new RuntimeException(
                "Erro: Não é possível aumentar o estoque disponível além da quantidade total cadastrada!"
            );
        }

        livro.setQtdDisponivel(livro.getQtdDisponivel() + 1);
        repository.save(livro);
    }
}
