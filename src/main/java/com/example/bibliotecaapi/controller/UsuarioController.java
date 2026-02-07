package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.dto.UsuarioRequestDTO;
import com.example.bibliotecaapi.dto.UsuarioResponseDTO;
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
        Usuario usuarioEntity = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .cpf(dto.getCpf())
                .senha(dto.getSenha()) // IMPORTANTE: Hash da senha deve ser feito aqui ou no service
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento())
                .tipo(dto.getTipo())
                .build();

        Usuario usuarioSalvo = service.salvar(usuarioEntity);
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

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorCpf(@PathVariable String cpf) {
        return service.buscarPorCpf(cpf)
                .map(usuario -> ResponseEntity.ok(mapearParaResponse(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorNome(@PathVariable String nome) {
        List<Usuario> usuarios = service.buscarPorNome(nome);
        List<UsuarioResponseDTO> listaDTO = usuarios.stream()
                .map(this::mapearParaResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid UsuarioRequestDTO dto) {

        Usuario usuarioAtualizado = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .cpf(dto.getCpf())
                .senha(dto.getSenha())
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento())
                .tipo(dto.getTipo())
                .build();

        Usuario usuario = service.atualizar(id, usuarioAtualizado);
        return ResponseEntity.ok(mapearParaResponse(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable UUID id) {
        service.ativarDesativar(id, true);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable UUID id) {
        service.ativarDesativar(id, false);
        return ResponseEntity.ok().build();
    }

    // Método auxiliar para conversão
    private UsuarioResponseDTO mapearParaResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getTelefone(),
                usuario.getDataNascimento(),
                usuario.getTipo(),
                usuario.getDataCadastro(),
                usuario.isAtivo()
        );
    }
}