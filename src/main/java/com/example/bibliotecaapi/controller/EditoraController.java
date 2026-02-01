package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.dto.EditoraRequestDTO;
import com.example.bibliotecaapi.dto.EditoraResponseDTO;
import com.example.bibliotecaapi.model.Editora;
import com.example.bibliotecaapi.service.EditoraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/editoras")
public class EditoraController {

    private final EditoraService service;

    public EditoraController(EditoraService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EditoraResponseDTO> salvar(@RequestBody @Valid EditoraRequestDTO dto) {
        Editora editoraEntity = Editora.builder()
                .nome(dto.getNome())
                .pais(dto.getPais())
                .build();

        Editora editoraSalva = service.salvar(editoraEntity);
        EditoraResponseDTO response = mapearParaResponse(editoraSalva);

        return ResponseEntity.created(URI.create("/editoras/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EditoraResponseDTO>> listar() {
        List<Editora> editoras = service.listarTodos();

        List<EditoraResponseDTO> listaDTO = editoras.stream()
                .map(this::mapearParaResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EditoraResponseDTO> buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id)
                .map(editora -> ResponseEntity.ok(mapearParaResponse(editora)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EditoraResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid EditoraRequestDTO dto) {

        Editora editoraAtualizada = service.atualizar(id, dto);
        EditoraResponseDTO response = mapearParaResponse(editoraAtualizada);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private EditoraResponseDTO mapearParaResponse(Editora editora) {
        return new EditoraResponseDTO(
                editora.getId(),
                editora.getNome(),
                editora.getPais(),
                editora.getDataCadastro()
        );
    }
}