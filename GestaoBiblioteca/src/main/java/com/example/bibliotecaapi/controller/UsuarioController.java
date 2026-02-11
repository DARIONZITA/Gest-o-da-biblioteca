package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.dto.UsuarioRequestDTO;
import com.example.bibliotecaapi.dto.UsuarioResponseDTO;
import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.repository.UsuarioRepository;
import com.example.bibliotecaapi.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;
    private final UsuarioRepository repository;

    public UsuarioController(UsuarioService service, UsuarioRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> salvar(@RequestBody @Valid UsuarioRequestDTO dto) {

        Usuario usuarioEntity = Usuario.builder()
                .matricula(dto.getMatricula())
                .nome(dto.getNome())
                .email(dto.getEmail())
            .senha(dto.getSenha())
                .perfil(dto.getPerfil())
                .status(dto.getStatus())
                .build();

        Usuario usuarioSalvo = service.salvar(usuarioEntity);

        UsuarioResponseDTO response = mapearParaResponse(usuarioSalvo);

        return ResponseEntity.created(URI.create("/usuarios/" + response.getId())).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        List<Usuario> usuarios = service.listarTodos();

        List<UsuarioResponseDTO> listaDTO = usuarios.stream()
                .map(this::mapearParaResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public ResponseEntity<UsuarioResponseDTO> me(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(mapearParaResponse(usuario));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id)
                .map(usuario -> ResponseEntity.ok(mapearParaResponse(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioResponseDTO mapearParaResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getMatricula(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getStatus(),
                usuario.getDataCadastro()
        );
    }
}