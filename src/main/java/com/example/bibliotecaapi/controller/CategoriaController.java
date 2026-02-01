package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.dto.CategoriaRequestDTO;
import com.example.bibliotecaapi.dto.CategoriaResponseDTO;
import com.example.bibliotecaapi.model.Categoria;
import com.example.bibliotecaapi.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> salvar(@RequestBody @Valid CategoriaRequestDTO dto) {
        Categoria categoriaEntity = Categoria.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .build();

        Categoria categoriaSalva = service.salvar(categoriaEntity);
        CategoriaResponseDTO response = mapearParaResponse(categoriaSalva);

        return ResponseEntity.created(URI.create("/categorias/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listar() {
        List<Categoria> categorias = service.listarTodos();

        List<CategoriaResponseDTO> listaDTO = categorias.stream()
                .map(this::mapearParaResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id)
                .map(categoria -> ResponseEntity.ok(mapearParaResponse(categoria)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid CategoriaRequestDTO dto) {

        Categoria categoriaAtualizada = service.atualizar(id, dto);
        CategoriaResponseDTO response = mapearParaResponse(categoriaAtualizada);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private CategoriaResponseDTO mapearParaResponse(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.getDataCadastro()
        );
    }
}