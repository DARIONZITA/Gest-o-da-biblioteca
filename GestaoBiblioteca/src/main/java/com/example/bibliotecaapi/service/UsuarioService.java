package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario salvar(Usuario usuario) {


        repository.findByMatricula(usuario.getMatricula())
                .ifPresent(u -> {
                    throw new RuntimeException("Erro: Já existe um utilizador com a matrícula " + usuario.getMatricula());
                });
        repository.findByEmail(usuario.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("Este email já está em uso!"); });

        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new RuntimeException("A senha é obrigatória");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return repository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}