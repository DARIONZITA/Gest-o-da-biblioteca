package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.dto.ChatMessageRequestDTO;
import com.example.bibliotecaapi.dto.ChatMessageResponseDTO;
import com.example.bibliotecaapi.dto.GroqChatRequestDTO;
import com.example.bibliotecaapi.dto.RecomendacaoResponseDTO;
import com.example.bibliotecaapi.model.Livro;
import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Serviço de Chatbot com RAG (Retrieval-Augmented Generation)
 * Combina IA conversacional com dados reais da biblioteca
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final GroqApiService groqApiService;
    private final LivroRepository livroRepository;
    private final RecomendacaoService recomendacaoService;

    // Armazenamento temporário de sessões (em produção, usar Redis ou DB)
    private final Map<String, List<GroqChatRequestDTO.Message>> sessoes = new ConcurrentHashMap<>();
    private static final int MAX_HISTORICO = 10; // Limitar histórico para não estourar tokens

    /**
     * Processa mensagem do usuário e retorna resposta do chatbot
     */
    public ChatMessageResponseDTO processarMensagem(ChatMessageRequestDTO request, Usuario usuario) {
        String sessaoId = request.getSessaoId() != null ? request.getSessaoId() : gerarSessaoId();
        String mensagem = request.getMensagem();

        log.info("Processando mensagem do chatbot - Usuário: {}, Sessão: {}", usuario.getNome(), sessaoId);

        // 1. Detectar intenção do usuário
        IntencaoUsuario intencao = detectarIntencao(mensagem);

        // 2. Buscar contexto relevante (RAG)
        String contexto = buscarContexto(intencao, mensagem, usuario);

        // 3. Montar system prompt com contexto
        String systemPrompt = montarSystemPrompt(contexto, usuario);

        // 4. Recuperar histórico da conversa
        List<GroqChatRequestDTO.Message> historico = sessoes.getOrDefault(sessaoId, new ArrayList<>());

        // 5. Enviar para Groq API
        String resposta = groqApiService.enviarMensagem(mensagem, systemPrompt, historico);

        // 6. Atualizar histórico
        atualizarHistorico(sessaoId, mensagem, resposta);

        // 7. Se for busca de livros, incluir sugestões
        List<RecomendacaoResponseDTO> livrosSugeridos = null;
        if (intencao == IntencaoUsuario.BUSCAR_LIVRO || intencao == IntencaoUsuario.RECOMENDAR) {
            livrosSugeridos = buscarLivrosSugeridos(mensagem, usuario);
        }

        return new ChatMessageResponseDTO(
                resposta,
                sessaoId,
                new java.time.LocalDateTime[]{java.time.LocalDateTime.now()}[0],
                livrosSugeridos,
                calcularConfianca(intencao)
        );
    }

    /**
     * Detecta a intenção do usuário na mensagem
     */
    private IntencaoUsuario detectarIntencao(String mensagem) {
        String lower = mensagem.toLowerCase();

        // Padrões de intenção
        if (lower.matches(".*(livro|obra|título|romance|ficção).*")) {
            if (lower.matches(".*(recomendar|sugerir|indicar).*")) {
                return IntencaoUsuario.RECOMENDAR;
            }
            return IntencaoUsuario.BUSCAR_LIVRO;
        }

        if (lower.matches(".*(emprestar|empréstimo|pegar).*")) {
            return IntencaoUsuario.EMPRESTIMO;
        }

        if (lower.matches(".*(reservar|reserva).*")) {
            return IntencaoUsuario.RESERVA;
        }

        if (lower.matches(".*(autor|escritor).*")) {
            return IntencaoUsuario.BUSCAR_AUTOR;
        }

        if (lower.matches(".*(horário|funciona|aberto|horarios).*")) {
            return IntencaoUsuario.INFORMACAO_BIBLIOTECA;
        }

        return IntencaoUsuario.CONVERSA_GERAL;
    }

    /**
     * Busca contexto relevante do banco de dados (RAG)
     */
    private String buscarContexto(IntencaoUsuario intencao, String mensagem, Usuario usuario) {
        StringBuilder contexto = new StringBuilder();

        switch (intencao) {
            case BUSCAR_LIVRO:
            case RECOMENDAR:
                // Buscar livros relacionados
                List<String> keywords = extrairKeywords(mensagem);
                if (!keywords.isEmpty()) {
                    List<Livro> livros = buscarLivrosPorKeywords(keywords);
                    if (!livros.isEmpty()) {
                        contexto.append("\n## Livros disponíveis relacionados:\n");
                        livros.stream().limit(5).forEach(livro -> {
                            contexto.append(String.format("- \"%s\" por %s (Categoria: %s, ISBN: %s)\n",
                                    livro.getTitulo(),
                                    livro.getAutor() != null ? livro.getAutor().getNome() : "Desconhecido",
                                    livro.getCategoria() != null ? livro.getCategoria().getNome() : "Sem categoria",
                                    livro.getIsbn()
                            ));
                            if (livro.getSinopse() != null) {
                                contexto.append("  Sinopse: ").append(livro.getSinopse().substring(0, Math.min(150, livro.getSinopse().length()))).append("...\n");
                            }
                        });
                    }
                }
                break;

            case EMPRESTIMO:
            case RESERVA:
                contexto.append("\n## Informações sobre empréstimos e reservas:\n");
                contexto.append("- Duração do empréstimo: 14 dias\n");
                contexto.append("- Máximo de 3 livros por vez\n");
                contexto.append("- Renovação permitida se não houver reserva\n");
                contexto.append("- Multa por atraso: R$ 1,00 por dia\n");
                break;

            case INFORMACAO_BIBLIOTECA:
                contexto.append("\n## Informações da Biblioteca:\n");
                contexto.append("- Horário: Segunda a Sexta, 8h às 20h | Sábado, 9h às 17h\n");
                contexto.append("- Localização: Campus Principal, Bloco A\n");
                contexto.append("- Acervo: Mais de 50.000 títulos\n");
                contexto.append("- Serviços: Empréstimo, Reserva, Consulta Local, Digitalização\n");
                break;

            default:
                // Nenhum contexto específico
                break;
        }

        return contexto.toString();
    }

    /**
     * Extrai palavras-chave da mensagem
     */
    private List<String> extrairKeywords(String mensagem) {
        // Remove stopwords e pontuação
        String[] stopwords = {"o", "a", "de", "do", "da", "em", "para", "com", "por", "um", "uma", 
                              "sobre", "livro", "obra", "quero", "gostaria", "procuro"};
        
        return Arrays.stream(mensagem.toLowerCase().split("\\s+"))
                .map(palavra -> palavra.replaceAll("[^a-záàâãéèêíïóôõöúçñ]", ""))
                .filter(palavra -> palavra.length() > 3)
                .filter(palavra -> !Arrays.asList(stopwords).contains(palavra))
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Busca livros por palavras-chave
     */
    @Cacheable(value = "livrosPorKeywords", key = "#keywords.toString()")
    private List<Livro> buscarLivrosPorKeywords(List<String> keywords) {
        Set<Livro> resultados = new HashSet<>();
        
        for (String keyword : keywords) {
            resultados.addAll(livroRepository.findByTituloContaining(keyword));
        }
        
        return new ArrayList<>(resultados);
    }

    /**
     * Monta o system prompt com contexto e instruções
     */
    private String montarSystemPrompt(String contexto, Usuario usuario) {
        return String.format("""
                Você é um assistente virtual inteligente de uma biblioteca universitária.
                
                **Seu papel:**
                - Ajudar usuários a encontrar livros
                - Fornecer recomendações personalizadas
                - Responder dúvidas sobre empréstimos, reservas e horários
                - Ser amigável, educado e prestativo
                
                **Nome do usuário:** %s
                **Tipo de usuário:** %s
                
                **Instruções importantes:**
                - Use linguagem clara e acessível
                - Se houver informações sobre livros disponíveis, cite-os especificamente
                - Se não souber algo, seja honesto e sugira alternativas
                - Mantenha respostas concisas (máximo 200 palavras)
                - Use emojis ocasionalmente para tornar a conversa mais amigável 📚
                
                %s
                """,
                usuario.getNome(),
                usuario.getPerfil(),
                contexto.isEmpty() ? "" : "**Contexto relevante:**" + contexto
        );
    }

    /**
     * Atualiza histórico da conversa
     */
    private void atualizarHistorico(String sessaoId, String mensagem, String resposta) {
        List<GroqChatRequestDTO.Message> historico = sessoes.computeIfAbsent(sessaoId, k -> new ArrayList<>());
        
        historico.add(new GroqChatRequestDTO.Message("user", mensagem));
        historico.add(new GroqChatRequestDTO.Message("assistant", resposta));
        
        // Limitar tamanho do histórico
        if (historico.size() > MAX_HISTORICO * 2) {
            historico.subList(0, historico.size() - MAX_HISTORICO * 2).clear();
        }
    }

    /**
     * Busca livros sugeridos baseados na mensagem
     */
    private List<RecomendacaoResponseDTO> buscarLivrosSugeridos(String mensagem, Usuario usuario) {
        try {
            List<String> keywords = extrairKeywords(mensagem);
            if (!keywords.isEmpty()) {
                List<Livro> livros = buscarLivrosPorKeywords(keywords);
                return livros.stream()
                        .limit(3)
                        .map(this::converterParaRecomendacao)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Erro ao buscar livros sugeridos", e);
        }
        return null;
    }

    /**
     * Converte Livro para RecomendacaoResponseDTO
     */
    private RecomendacaoResponseDTO converterParaRecomendacao(Livro livro) {
        RecomendacaoResponseDTO dto = new RecomendacaoResponseDTO();
        dto.setId(livro.getId());
        dto.setTitulo(livro.getTitulo());
        dto.setIsbn(livro.getIsbn());
        dto.setQtdDisponivel(livro.getQtdDisponivel());
        dto.setCapaURL(livro.getCapaURL());
        dto.setSinopse(livro.getSinopse());
        dto.setNomeAutor(livro.getAutor() != null ? livro.getAutor().getNome() : null);
        dto.setNomeCategoria(livro.getCategoria() != null ? livro.getCategoria().getNome() : null);
        dto.setScoreGeral(85.0); // Score fixo para resultados de busca
        dto.setMotivoRecomendacao("Relacionado à sua busca");
        return dto;
    }

    /**
     * Calcula confiança baseado na intenção
     */
    private Integer calcularConfianca(IntencaoUsuario intencao) {
        return switch (intencao) {
            case BUSCAR_LIVRO, RECOMENDAR -> 95;
            case EMPRESTIMO, RESERVA, INFORMACAO_BIBLIOTECA -> 90;
            case BUSCAR_AUTOR -> 85;
            default -> 75;
        };
    }

    /**
     * Gera ID único para sessão
     */
    private String gerarSessaoId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Limpa sessões antigas (pode ser chamado periodicamente)
     */
    public void limparSessoesAntigas() {
        // Em produção, implementar lógica com timestamp
        if (sessoes.size() > 1000) {
            sessoes.clear();
            log.info("Sessões de chat limpas");
        }
    }

    /**
     * Enum para intenções do usuário
     */
    private enum IntencaoUsuario {
        BUSCAR_LIVRO,
        RECOMENDAR,
        EMPRESTIMO,
        RESERVA,
        BUSCAR_AUTOR,
        INFORMACAO_BIBLIOTECA,
        CONVERSA_GERAL
    }
}
