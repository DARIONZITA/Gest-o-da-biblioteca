package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.dto.LivroResponseDTO;
import com.example.bibliotecaapi.dto.LivroRequestDTO;
import com.example.bibliotecaapi.model.Autor;
import com.example.bibliotecaapi.model.Categoria;
import com.example.bibliotecaapi.model.Livro;
import com.example.bibliotecaapi.repository.CategoriaRepository;
import com.example.bibliotecaapi.service.AutorService;
import com.example.bibliotecaapi.service.FileStorageService;
import com.example.bibliotecaapi.service.LivroService;
import com.example.bibliotecaapi.service.integration.GoogleBooksClient;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroService serviceLivro;
    private final AutorService autorService;
    private final CategoriaRepository categoriaRepository;
    private final com.example.bibliotecaapi.service.EditoraService editoraService;
    private final FileStorageService fileStorageService;
    private final GoogleBooksClient googleBooksClient;

    public LivroController(LivroService serviceLivro,
            AutorService autorService,
            CategoriaRepository categoriaRepository,
            com.example.bibliotecaapi.service.EditoraService editoraService,
            FileStorageService fileStorageService,
            GoogleBooksClient googleBooksClient) {
        this.serviceLivro = serviceLivro;
        this.autorService = autorService;
        this.categoriaRepository = categoriaRepository;
        this.editoraService = editoraService;
        this.fileStorageService = fileStorageService;
        this.googleBooksClient = googleBooksClient;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LivroResponseDTO> salvar(@RequestBody @Valid LivroRequestDTO dto) {
        Autor autor;
        if (dto.getAutorId() != null) {
            autor = autorService.buscarPorId(dto.getAutorId())
                    .orElseThrow(() -> new RuntimeException("Autor não encontrado!"));
        } else if (dto.getNomeAutor() != null && !dto.getNomeAutor().isEmpty()) {
            autor = autorService.findOrCreate(dto.getNomeAutor());
        } else {
            throw new RuntimeException("Autor é obrigatório (ID ou Nome)!");
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));

        com.example.bibliotecaapi.model.Editora editora = null;
        if (dto.getNomeEditora() != null && !dto.getNomeEditora().isEmpty()) {
            editora = editoraService.findOrCreate(dto.getNomeEditora());
        }

        // 2. Converte DTO para Entity (Preparar para o banco)
        Livro livroEntity = Livro.builder()
                .titulo(dto.getTitulo())
                .isbn(dto.getIsbn())
                .autor(autor)
                .categoria(categoria)
                .editora(editora)
                .anoPublicacao(dto.getAnoPublicacao())
                .qtdPaginas(dto.getQtdPaginas())
                .qtdTotal(dto.getQtdTotal())
                .localizacao(dto.getLocalizacao())
                .capaURL(dto.getCapaURL())
                .sinopse(dto.getSinopse())
                .build();

        // 3. Chama o Service enviando a Entity
        Livro livroSalvo = serviceLivro.salvar(livroEntity);

        // 4. Converte a Entity salva de volta para ResponseDTO (Enviar para o React)
        LivroResponseDTO response = mapearParaResponse(livroSalvo);

        return ResponseEntity.created(URI.create("/livros/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LivroResponseDTO>> listar() {
        List<Livro> livros = serviceLivro
                .listarTodos();

        // Converte a lista de Entities para uma lista de DTOs
        List<LivroResponseDTO> listaDTO = livros.stream()
                .map(this::mapearParaResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<LivroResponseDTO> buscarPorIsbn(@PathVariable String isbn) {
        return serviceLivro.buscarPorIsbn(isbn)
                .map(livro -> ResponseEntity.ok(mapearParaResponse(livro)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{isbn}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable String isbn) {
        serviceLivro.deletar(isbn);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-capa")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadCapa(@RequestParam("file") MultipartFile arquivo) {
        String nomeArquivo = fileStorageService.salvarArquivo(arquivo);
        String capaURL = "/api/uploads/capas/" + nomeArquivo;

        Map<String, String> response = new HashMap<>();
        response.put("capaURL", capaURL);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/external-search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GoogleBooksClient.GoogleBookInfo>> searchExternal(@RequestParam String query) {
        return ResponseEntity.ok(googleBooksClient.searchBooks(query));
    }

    private LivroResponseDTO mapearParaResponse(Livro livro) {
        return new LivroResponseDTO(
                livro.getId(),
                livro.getTitulo(),
                livro.getIsbn(),
                livro.getAutor().getNome(),
                livro.getCategoria().getNome(),
                livro.getEditora() != null ? livro.getEditora().getNome() : null,
                livro.getAnoPublicacao(),
                livro.getQtdPaginas(),
                livro.getQtdTotal(),
                livro.getQtdDisponivel(),
                livro.getLocalizacao(),
                livro.getCapaURL(),
                livro.getSinopse());
    }
}
