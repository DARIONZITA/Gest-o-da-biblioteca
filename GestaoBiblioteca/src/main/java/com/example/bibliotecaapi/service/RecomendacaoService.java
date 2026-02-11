package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.dto.RecomendacaoResponseDTO;
import com.example.bibliotecaapi.model.Livro;
import com.example.bibliotecaapi.repository.EmprestimoRepository;
import com.example.bibliotecaapi.repository.LivroRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecomendacaoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;

    // Pesos para cálculo do score final (ajustáveis)
    private static final double PESO_COLABORATIVO = 0.4;
    private static final double PESO_CONTEUDO = 0.4;
    private static final double PESO_POPULARIDADE = 0.2;

    public RecomendacaoService(EmprestimoRepository emprestimoRepository, LivroRepository livroRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
    }

    /**
     * Obtém recomendações personalizadas para um usuário
     * Usa cache para otimizar performance
     */
    @Cacheable(value = "recomendacoes", key = "#usuarioId")
    public List<RecomendacaoResponseDTO> obterRecomendacoes(UUID usuarioId) {
        // 1. Buscar histórico de livros do usuário
        List<UUID> livrosLidosIds = emprestimoRepository.findLivrosIdsByUsuarioId(usuarioId);

        // Se usuário não tem histórico, retornar apenas por popularidade
        if (livrosLidosIds.isEmpty()) {
            return obterRecomendacoesPorPopularidade(Collections.emptyList());
        }

        // 2. Calcular scores de recomendação com as 3 estratégias
        Map<UUID, RecomendacaoScores> scoresMap = new HashMap<>();

        // 2.1 Filtragem Colaborativa
        calcularScoreColaborativo(usuarioId, livrosLidosIds, scoresMap);

        // 2.2 Baseado em Conteúdo
        calcularScoreConteudo(usuarioId, livrosLidosIds, scoresMap);

        // 2.3 Popularidade
        calcularScorePopularidade(scoresMap);

        // 3. Combinar scores e ordenar
        List<RecomendacaoResponseDTO> recomendacoes = combinarScoresEGerarRecomendacoes(scoresMap, livrosLidosIds);

        // 4. Retornar top 10
        return recomendacoes.stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Obtém livros mais populares (para usuários sem histórico ou página pública)
     */
    public List<RecomendacaoResponseDTO> obterRecomendacoesPorPopularidade(List<UUID> livrosExcluirIds) {
        LocalDate dataInicio = LocalDate.now().minusDays(30);
        List<Object[]> livrosPopulares = emprestimoRepository.findLivrosMaisEmprestados(dataInicio);

        List<RecomendacaoResponseDTO> recomendacoes = new ArrayList<>();

        for (Object[] resultado : livrosPopulares) {
            UUID livroId = (UUID) resultado[0];
            Long contagem = (Long) resultado[1];

            // Pular livros já lidos
            if (livrosExcluirIds.contains(livroId)) {
                continue;
            }

            Optional<Livro> livroOpt = livroRepository.findById(livroId);
            if (livroOpt.isPresent() && livroOpt.get().getQtdDisponivel() > 0) {
                Livro livro = livroOpt.get();
                RecomendacaoResponseDTO dto = mapearParaDTO(livro);
                dto.setScorePopularidade(contagem.doubleValue());
                dto.setScoreGeral(contagem.doubleValue());
                dto.setMotivoRecomendacao("📈 Em alta - " + contagem + " empréstimos no último mês");
                dto.setConfianca(100);
                recomendacoes.add(dto);
            }

            if (recomendacoes.size() >= 10) {
                break;
            }
        }

        return recomendacoes;
    }

    /**
     * Estratégia 1: Filtragem Colaborativa
     * "Usuários com gostos similares também leram..."
     */
    private void calcularScoreColaborativo(UUID usuarioId, List<UUID> livrosLidosIds, Map<UUID, RecomendacaoScores> scoresMap) {
        // Buscar usuários que leram livros similares
        List<UUID> usuariosSimilares = emprestimoRepository.findUsuariosSimilares(livrosLidosIds, usuarioId);

        if (usuariosSimilares.isEmpty()) {
            return;
        }

        // Para cada usuário similar, buscar seus livros
        Map<UUID, Integer> livrosContagem = new HashMap<>();
        for (UUID usuarioSimilarId : usuariosSimilares) {
            List<UUID> livrosDoUsuarioSimilar = emprestimoRepository.findLivrosIdsByUsuarioId(usuarioSimilarId);
            for (UUID livroId : livrosDoUsuarioSimilar) {
                if (!livrosLidosIds.contains(livroId)) {
                    livrosContagem.put(livroId, livrosContagem.getOrDefault(livroId, 0) + 1);
                }
            }
        }

        // Calcular score normalizado (0-100)
        int maxContagem = livrosContagem.values().stream().max(Integer::compare).orElse(1);
        for (Map.Entry<UUID, Integer> entry : livrosContagem.entrySet()) {
            UUID livroId = entry.getKey();
            double score = (entry.getValue() * 100.0) / maxContagem;
            
            RecomendacaoScores scores = scoresMap.computeIfAbsent(livroId, k -> new RecomendacaoScores());
            scores.scoreColaborativo = score;
            scores.motivoColaborativo = entry.getValue() + " usuários similares também leram";
        }
    }

    /**
     * Estratégia 2: Baseado em Conteúdo
     * "Porque você leu livros da mesma categoria/autor..."
     */
    private void calcularScoreConteudo(UUID usuarioId, List<UUID> livrosLidosIds, Map<UUID, RecomendacaoScores> scoresMap) {
        // Buscar categorias preferidas do usuário
        List<Object[]> categoriasPreferidas = emprestimoRepository.findCategoriasPreferidas(usuarioId);

        if (categoriasPreferidas.isEmpty()) {
            return;
        }

        // Buscar livros das categorias preferidas
        int totalBuscado = 0;
        for (Object[] resultado : categoriasPreferidas) {
            UUID categoriaId = (UUID) resultado[0];
            Long contagem = (Long) resultado[1];

            List<Livro> livrosCategoria = livroRepository.findByCategoriaExcludingLivros(categoriaId, livrosLidosIds);

            for (Livro livro : livrosCategoria) {
                // Score baseado na preferência da categoria (normalizado)
                double score = (contagem * 100.0) / livrosLidosIds.size();
                
                RecomendacaoScores scores = scoresMap.computeIfAbsent(livro.getId(), k -> new RecomendacaoScores());
                scores.scoreConteudo = Math.max(scores.scoreConteudo, score);
                scores.motivoConteudo = "Você leu " + contagem + " livros de " + livro.getCategoria().getNome();
                
                totalBuscado++;
                if (totalBuscado >= 30) {
                    break;
                }
            }

            if (totalBuscado >= 30) {
                break;
            }
        }
    }

    /**
     * Estratégia 3: Popularidade
     * "Mais emprestados recentemente..."
     */
    private void calcularScorePopularidade(Map<UUID, RecomendacaoScores> scoresMap) {
        LocalDate dataInicio = LocalDate.now().minusDays(30);
        List<Object[]> livrosPopulares = emprestimoRepository.findLivrosMaisEmprestados(dataInicio);

        if (livrosPopulares.isEmpty()) {
            return;
        }

        // Normalizar score de popularidade
        long maxEmprestimos = ((Long) livrosPopulares.get(0)[1]);

        for (Object[] resultado : livrosPopulares) {
            UUID livroId = (UUID) resultado[0];
            Long contagem = (Long) resultado[1];

            double score = (contagem * 100.0) / maxEmprestimos;

            RecomendacaoScores scores = scoresMap.computeIfAbsent(livroId, k -> new RecomendacaoScores());
            scores.scorePopularidade = score;
            scores.motivoPopularidade = "📈 " + contagem + " empréstimos no último mês";
        }
    }

    /**
     * Combina os 3 scores e gera lista final de recomendações
     */
    private List<RecomendacaoResponseDTO> combinarScoresEGerarRecomendacoes(
            Map<UUID, RecomendacaoScores> scoresMap, List<UUID> livrosLidosIds) {

        List<RecomendacaoResponseDTO> recomendacoes = new ArrayList<>();

        for (Map.Entry<UUID, RecomendacaoScores> entry : scoresMap.entrySet()) {
            UUID livroId = entry.getKey();
            RecomendacaoScores scores = entry.getValue();

            // Pular livros já lidos
            if (livrosLidosIds.contains(livroId)) {
                continue;
            }

            Optional<Livro> livroOpt = livroRepository.findById(livroId);
            if (livroOpt.isEmpty() || livroOpt.get().getQtdDisponivel() <= 0) {
                continue;
            }

            Livro livro = livroOpt.get();

            // Calcular score final ponderado
            double scoreFinal = (scores.scoreColaborativo * PESO_COLABORATIVO) +
                               (scores.scoreConteudo * PESO_CONTEUDO) +
                               (scores.scorePopularidade * PESO_POPULARIDADE);

            // Determinar melhor motivo (usar o score mais alto)
            String motivo = determinarMelhorMotivo(scores);

            // Calcular confiança (baseado em quantas estratégias recomendaram)
            int confianca = calcularConfianca(scores);

            RecomendacaoResponseDTO dto = mapearParaDTO(livro);
            dto.setScoreGeral(scoreFinal);
            dto.setScoreColaborativo(scores.scoreColaborativo);
            dto.setScoreConteudo(scores.scoreConteudo);
            dto.setScorePopularidade(scores.scorePopularidade);
            dto.setMotivoRecomendacao(motivo);
            dto.setConfianca(confianca);

            recomendacoes.add(dto);
        }

        // Ordenar por score final (decrescente)
        recomendacoes.sort((a, b) -> Double.compare(b.getScoreGeral(), a.getScoreGeral()));

        return recomendacoes;
    }

    /**
     * Determina o melhor motivo baseado nos scores
     */
    private String determinarMelhorMotivo(RecomendacaoScores scores) {
        double maxScore = Math.max(scores.scoreColaborativo, 
                         Math.max(scores.scoreConteudo, scores.scorePopularidade));

        if (maxScore == scores.scoreColaborativo && scores.motivoColaborativo != null) {
            return "👥 " + scores.motivoColaborativo;
        } else if (maxScore == scores.scoreConteudo && scores.motivoConteudo != null) {
            return "📚 " + scores.motivoConteudo;
        } else if (scores.motivoPopularidade != null) {
            return scores.motivoPopularidade;
        }

        return "Recomendado para você";
    }

    /**
     * Calcula confiança da recomendação (0-100)
     * Baseado em quantas estratégias recomendaram o livro
     */
    private int calcularConfianca(RecomendacaoScores scores) {
        int estrategiasAtivas = 0;
        double somaScores = 0;

        if (scores.scoreColaborativo > 0) {
            estrategiasAtivas++;
            somaScores += scores.scoreColaborativo;
        }
        if (scores.scoreConteudo > 0) {
            estrategiasAtivas++;
            somaScores += scores.scoreConteudo;
        }
        if (scores.scorePopularidade > 0) {
            estrategiasAtivas++;
            somaScores += scores.scorePopularidade;
        }

        if (estrategiasAtivas == 0) {
            return 0;
        }

        // Confiança maior quando múltiplas estratégias concordam
        double mediaScore = somaScores / estrategiasAtivas;
        int bonus = (estrategiasAtivas - 1) * 10; // +10% para cada estratégia adicional

        return Math.min(100, (int) mediaScore + bonus);
    }

    /**
     * Mapeia entidade Livro para DTO de Recomendação
     */
    private RecomendacaoResponseDTO mapearParaDTO(Livro livro) {
        RecomendacaoResponseDTO dto = new RecomendacaoResponseDTO();
        dto.setId(livro.getId());
        dto.setTitulo(livro.getTitulo());
        dto.setIsbn(livro.getIsbn());
        dto.setNomeAutor(livro.getAutor() != null ? livro.getAutor().getNome() : null);
        dto.setNomeCategoria(livro.getCategoria() != null ? livro.getCategoria().getNome() : null);
        dto.setQtdPaginas(livro.getQtdPaginas());
        dto.setQtdDisponivel(livro.getQtdDisponivel());
        dto.setLocalizacao(livro.getLocalizacao());
        dto.setCapaURL(livro.getCapaURL());
        dto.setSinopse(livro.getSinopse());
        return dto;
    }

    /**
     * Classe interna para armazenar scores das 3 estratégias
     */
    private static class RecomendacaoScores {
        double scoreColaborativo = 0;
        double scoreConteudo = 0;
        double scorePopularidade = 0;
        String motivoColaborativo;
        String motivoConteudo;
        String motivoPopularidade;
    }
}
