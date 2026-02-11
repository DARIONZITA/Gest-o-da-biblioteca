package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.dto.RecomendacaoResponseDTO;
import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.service.RecomendacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recomendacoes")
public class RecomendacaoController {

    private final RecomendacaoService recomendacaoService;

    public RecomendacaoController(RecomendacaoService recomendacaoService) {
        this.recomendacaoService = recomendacaoService;
    }

    /**
     * GET /recomendacoes
     * Retorna recomendações personalizadas para o usuário autenticado
     * Requer autenticação JWT
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RecomendacaoResponseDTO>> obterRecomendacoesParaUsuarioAutenticado(
            Authentication authentication) {
        
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<RecomendacaoResponseDTO> recomendacoes = recomendacaoService.obterRecomendacoes(usuario.getId());
        
        return ResponseEntity.ok(recomendacoes);
    }

    /**
     * GET /recomendacoes/{usuarioId}
     * Retorna recomendações para um usuário específico
     * Apenas ADMIN pode ver recomendações de outros usuários
     */
    @GetMapping("/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RecomendacaoResponseDTO>> obterRecomendacoesParaUsuario(
            @PathVariable UUID usuarioId) {
        
        List<RecomendacaoResponseDTO> recomendacoes = recomendacaoService.obterRecomendacoes(usuarioId);
        
        return ResponseEntity.ok(recomendacoes);
    }

    /**
     * GET /livros/populares
     * Retorna livros mais populares (últimos 30 dias)
     * Endpoint público - não requer autenticação
     */
    @GetMapping("/populares")
    public ResponseEntity<List<RecomendacaoResponseDTO>> obterLivrosPopulares() {
        List<RecomendacaoResponseDTO> populares = 
            recomendacaoService.obterRecomendacoesPorPopularidade(List.of());
        
        return ResponseEntity.ok(populares);
    }
}
