package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.dto.EmprestimoRascunhoRequestDTO;
import com.example.bibliotecaapi.dto.EmprestimoRequestDTO;
import com.example.bibliotecaapi.dto.EmprestimoResponseDTO;
import com.example.bibliotecaapi.model.*;
import com.example.bibliotecaapi.repository.LivroRepository;
import com.example.bibliotecaapi.repository.UsuarioRepository;
import com.example.bibliotecaapi.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService service;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;

    public EmprestimoController(EmprestimoService service, UsuarioRepository usuarioRepository,
            LivroRepository livroRepository) {
        this.service = service;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmprestimoResponseDTO> salvar(@RequestBody @Valid EmprestimoRequestDTO dto) {
        // Criamos os objetos apenas com o ID para o JPA fazer o Join
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado!"));

        Livro livro = livroRepository.findById(dto.getLivroId())
                .orElseThrow(() -> new RuntimeException("Livro não encontrada!"));

        Emprestimo entity = Emprestimo.builder()
                .usuario(usuario)
                .livro(livro)
                .dataPrevista(dto.getDataPrevista())
                .build();

        Emprestimo salvo = service.salvar(entity);
        return ResponseEntity.created(URI.create("/emprestimos/" + salvo.getId())).body(mapearParaResponse(salvo));
    }

    @PostMapping("/rascunho")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<EmprestimoResponseDTO> criarRascunho(
            @RequestBody @Valid EmprestimoRascunhoRequestDTO dto,
            Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();

        Livro livro = livroRepository.findById(dto.getLivroId())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado!"));

        LocalDate dataPrevista = dto.getDataPrevista() != null
                ? dto.getDataPrevista()
                : LocalDate.now().plusDays(4);

        Emprestimo entity = Emprestimo.builder()
                .usuario(usuario)
                .livro(livro)
                .dataPrevista(dataPrevista)
                .status(StatusEmprestimo.PENDENTE)
                .build();

        Emprestimo salvo = service.criarRascunho(entity);
        return ResponseEntity.created(URI.create("/emprestimos/" + salvo.getId())).body(mapearParaResponse(salvo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emprestimo> buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/devolver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmprestimoResponseDTO> devolver(@PathVariable UUID id) {
        // 1. O Service procura o ID no banco (findById) e executa a lógica de datas e
        // multas
        Emprestimo emprestimoAtualizado = service.devolver(id);

        // 2. Retornamos o objeto atualizado convertido para DTO
        return ResponseEntity.ok(mapearParaResponse(emprestimoAtualizado));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos().stream()
                .map(this::mapearParaResponse).collect(Collectors.toList()));
    }

    @PatchMapping("/{id}/renovar")
    public ResponseEntity<Emprestimo> renovar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.renovar(id));
    }

    @PatchMapping("/{id}/aprovar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmprestimoResponseDTO> aprovar(@PathVariable UUID id) {
        Emprestimo atualizado = service.aprovarRascunho(id);
        return ResponseEntity.ok(mapearParaResponse(atualizado));
    }

    @PatchMapping("/{id}/pagar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmprestimoResponseDTO> pagarMulta(@PathVariable UUID id) {
        Emprestimo atualizado = service.pagarMulta(id);
        return ResponseEntity.ok(mapearParaResponse(atualizado));
    }

    @GetMapping("/meus")
    public ResponseEntity<List<EmprestimoResponseDTO>> meusEmprestimos(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(service.listarPorUsuario(usuario.getId()).stream()
                .map(this::mapearParaResponse).collect(Collectors.toList()));
    }

    private EmprestimoResponseDTO mapearParaResponse(Emprestimo e) {
        return new EmprestimoResponseDTO(
                e.getId(),
                e.getUsuario().getNome(), // Aqui o JPA busca o nome automaticamente
                e.getLivro().getTitulo(),
                e.getQtdRenovacoes(),
                e.getStatus(),
                e.getValorMulta(),
                e.getDataEmprestimo(),
                e.getDataPrevista(),
                e.getDataDevolucaoReal());
    }
}