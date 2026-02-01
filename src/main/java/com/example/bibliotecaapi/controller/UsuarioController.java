package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.dto.UsuarioRequestDTO;
import com.example.bibliotecaapi.dto.UsuarioResponseDTO;
import com.example.bibliotecaapi.model.PerfilUsuario;
import com.example.bibliotecaapi.model.StatusUsuario;
import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> salvar(@RequestBody @Valid UsuarioRequestDTO dto) {
        Usuario usuarioSalvo = service.salvarComDTO(dto);
        UsuarioResponseDTO response = mapearParaResponse(usuarioSalvo);

        return ResponseEntity.created(URI.create("/usuarios/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        List<Usuario> usuarios = service.listarTodos();

        List<UsuarioResponseDTO> listaDTO = usuarios.stream()
                .map(this::mapearParaResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id)
                .map(usuario -> ResponseEntity.ok(mapearParaResponse(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorEmail(@PathVariable String email) {
        return service.buscarPorEmail(email)
                .map(usuario -> ResponseEntity.ok(mapearParaResponse(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid UsuarioRequestDTO dto) {

        Usuario usuarioAtualizado = service.atualizar(id, dto);
        UsuarioResponseDTO response = mapearParaResponse(usuarioAtualizado);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UsuarioResponseDTO> alterarStatus(
            @PathVariable UUID id,
            @RequestParam StatusUsuario status) {

        Usuario usuarioAtualizado = service.alterarStatus(id, status);
        UsuarioResponseDTO response = mapearParaResponse(usuarioAtualizado);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/perfil")
    public ResponseEntity<UsuarioResponseDTO> alterarPerfil(
            @PathVariable UUID id,
            @RequestParam PerfilUsuario perfil) {

        Usuario usuarioAtualizado = service.alterarPerfil(id, perfil);
        UsuarioResponseDTO response = mapearParaResponse(usuarioAtualizado);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioResponseDTO mapearParaResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getMatricula(),
                usuario.getPerfil(),
                usuario.getStatus(),
                usuario.getDataCadastro(),
                usuario.getDataAtualizacao()
        );
    }
}