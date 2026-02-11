package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.dto.ReservaRequestDTO;
import com.example.bibliotecaapi.dto.ReservaResponseDTO;
import com.example.bibliotecaapi.model.Reserva;
import com.example.bibliotecaapi.model.Livro;
import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.repository.LivroRepository;
import com.example.bibliotecaapi.repository.UsuarioRepository;
import com.example.bibliotecaapi.service.ReservaService;
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
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService service;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;

    public ReservaController(ReservaService service, UsuarioRepository usuarioRepository, LivroRepository livroRepository) {
        this.service = service;
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> salvar(@RequestBody @Valid ReservaRequestDTO dto) {
        // Buscar entidades reais do banco de dados
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
        
        Livro livro = livroRepository.findById(dto.getLivroId())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado!"));

        Reserva entity = Reserva.builder()
                .usuario(usuario)
                .livro(livro)
                .build();

        Reserva salva = service.salvar(entity);
        return ResponseEntity.created(URI.create("/reservas/" + salva.getId())).body(mapearParaResponse(salva));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservaResponseDTO>> listar() {
        List<ReservaResponseDTO> listaDTO = service.listarTodas().stream()
                .map(this::mapearParaResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<ReservaResponseDTO>> minhasReservas(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(service.listarPorUsuario(usuario.getId()).stream()
                .map(this::mapearParaResponse).collect(Collectors.toList()));
    }

    private ReservaResponseDTO mapearParaResponse(Reserva r) {
        return new ReservaResponseDTO(
                r.getId(),
                r.getUsuario().getNome(),
                r.getLivro().getTitulo(),
                r.getPosicaoFila(),
                r.getStatus(),
                r.getDataReserva()
        );
    }
}