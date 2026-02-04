package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.dto.UsuarioRequestDTO;
import com.example.bibliotecaapi.model.PerfilUsuario;
import com.example.bibliotecaapi.model.StatusUsuario;
import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    @Transactional
    public Usuario salvar(Usuario usuario) {
        validarDadosUnicos(usuario.getEmail(), usuario.getMatricula(), null);

        // Definir status padrão se não informado
        if (usuario.getStatus() == null) {
            usuario.setStatus(StatusUsuario.ATIVO);
        }

        return repository.save(usuario);
    }

    @Transactional
    public Usuario salvarComDTO(UsuarioRequestDTO dto) {
        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha()) // Apenas texto - implemente criptografia depois
                .matricula(dto.getMatricula())
                .perfil(dto.getPerfil())
                .status(dto.getStatus() != null ? dto.getStatus() : StatusUsuario.ATIVO)
                .build();

        return salvar(usuario);
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

    @Transactional
    public Usuario atualizar(UUID id, UsuarioRequestDTO dto) {
        Usuario usuarioExistente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + id));

        validarDadosUnicos(dto.getEmail(), dto.getMatricula(), id);

        usuarioExistente.setNome(dto.getNome());
        usuarioExistente.setEmail(dto.getEmail());
        usuarioExistente.setMatricula(dto.getMatricula());
        usuarioExistente.setPerfil(dto.getPerfil());

        if (dto.getStatus() != null) {
            usuarioExistente.setStatus(dto.getStatus());
        }

        // Atualizar senha apenas se for fornecida uma nova
        if (dto.getSenha() != null && !dto.getSenha().trim().isEmpty()) {
            usuarioExistente.setSenha(dto.getSenha()); // Sem criptografia por enquanto
        }

        return repository.save(usuarioExistente);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public Usuario alterarStatus(UUID id, StatusUsuario novoStatus) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + id));

        usuario.setStatus(novoStatus);
        return repository.save(usuario);
    }

    @Transactional
    public Usuario alterarPerfil(UUID id, PerfilUsuario novoPerfil) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + id));

        usuario.setPerfil(novoPerfil);
        return repository.save(usuario);
    }

    private void validarDadosUnicos(String email, String matricula, UUID idExcluir) {
        boolean emailExistente;
        boolean matriculaExistente;

        if (idExcluir == null) {
            // Validação para novo usuário
            emailExistente = repository.existsByEmail(email);
            matriculaExistente = repository.existsByMatricula(matricula);
        } else {
            // Validação para atualização (excluir o próprio usuário)
            emailExistente = repository.existsByEmailAndIdNot(email, idExcluir);
            matriculaExistente = repository.existsByMatriculaAndIdNot(matricula, idExcluir);
        }

        if (emailExistente) {
            throw new IllegalArgumentException("Já existe um usuário com o email: " + email);
        }

        if (matriculaExistente) {
            throw new IllegalArgumentException("Já existe um usuário com a matrícula: " + matricula);
        }
    }
}