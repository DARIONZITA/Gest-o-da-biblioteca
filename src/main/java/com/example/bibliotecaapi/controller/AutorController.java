package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.dto.AutorRequestDTO;
import com.example.bibliotecaapi.dto.AutorResponseDTO;
import com.example.bibliotecaapi.model.Autor;
import com.example.bibliotecaapi.service.AutorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/autores")
public class AutorController {

    private final AutorService service;

    public AutorController(AutorService service) {
        this.service = service;
    }

    @PostMapping
    // 1. Recebe o RequestDTO e valida com @Valid
    public ResponseEntity<AutorResponseDTO> salvar(@RequestBody @Valid AutorRequestDTO dto) {

        // 2. Converte DTO para Entity (Preparar para o banco)
        Autor autorEntity = Autor.builder()
                    .nome(dto.getNome())
                    .descricao(dto.getDescricao())
                    .build();

        // 3. Chama o Service enviando a Entity
        Autor autorSalvo = service.salvar(autorEntity);

        // 4. Converte a Entity salva de volta para ResponseDTO (Enviar para o React)
        AutorResponseDTO response = mapearParaResponse(autorSalvo);

        return ResponseEntity.created(URI.create("/autores/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AutorResponseDTO>> listar() {
        List<Autor> autores = service.listarTodos();

        // Converte a lista de Entities para uma lista de DTOs
        List<AutorResponseDTO> listaDTO = autores.stream()
                .map(this::mapearParaResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutorResponseDTO> buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id)
                .map(autor -> ResponseEntity.ok(mapearParaResponse(autor)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Metodo auxiliar para não repetir código de conversão
    private AutorResponseDTO mapearParaResponse(Autor autor) {
        return new AutorResponseDTO(
                autor.getId(),
                autor.getNome(),
                autor.getDescricao(),
                autor.getDataCadastro()
        );
    }
}