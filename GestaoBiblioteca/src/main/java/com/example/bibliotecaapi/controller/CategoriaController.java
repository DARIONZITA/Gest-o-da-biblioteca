package com.example.bibliotecaapi.controller;


import com.example.bibliotecaapi.dto.CategoriaRequestDTO;
import com.example.bibliotecaapi.dto.CategoriaResponseDTO;
import com.example.bibliotecaapi.dto.UsuarioResponseDTO;
import com.example.bibliotecaapi.model.Categoria;
import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
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
        List<Categoria> categorias = service.listarTodas();

        List<CategoriaResponseDTO> listaDTO = categorias.stream()
                .map(this::mapearParaResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable UUID id) {
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

    private CategoriaResponseDTO mapearParaResponse(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao()
        );
    }

}