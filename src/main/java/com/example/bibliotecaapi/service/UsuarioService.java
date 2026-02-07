package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        // Verifica se já existe usuário com mesmo email ou CPF
        List<Usuario> existentes = repository.findByEmailOrCpf(usuario.getEmail(), usuario.getCpf());
        if (!existentes.isEmpty()) {
            throw new IllegalArgumentException("Já existe um usuário com este email ou CPF");
        }
        return repository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<Usuario> buscarPorCpf(String cpf) {
        return repository.findByCpf(cpf);
    }

    public List<Usuario> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Usuario> buscarPorTipo(com.example.bibliotecaapi.model.TipoUsuario tipo) {
        return repository.findByTipo(tipo);
    }

    public List<Usuario> buscarAtivos(boolean ativo) {
        return repository.findByAtivo(ativo);
    }

    @Transactional
    public void deletar(UUID id) {
        // Em vez de deletar fisicamente, podemos inativar
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setAtivo(false);
        repository.save(usuario);

        // Ou deletar fisicamente:
        // repository.deleteById(id);
    }

    @Transactional
    public Usuario atualizar(UUID id, Usuario usuarioAtualizado) {
        return repository.findById(id)
                .map(usuario -> {
                    // Atualiza apenas os campos permitidos
                    usuario.setNome(usuarioAtualizado.getNome());
                    usuario.setEmail(usuarioAtualizado.getEmail());
                    usuario.setTelefone(usuarioAtualizado.getTelefone());
                    usuario.setDataNascimento(usuarioAtualizado.getDataNascimento());
                    usuario.setTipo(usuarioAtualizado.getTipo());
                    return repository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public void ativarDesativar(UUID id, boolean ativo) {
        repository.findById(id)
                .ifPresent(usuario -> {
                    usuario.setAtivo(ativo);
                    repository.save(usuario);
                });
    }
}