package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.dto.GroqChatRequestDTO;
import com.example.bibliotecaapi.dto.GroqChatResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para comunicação com a API do Groq
 * Documentação: https://console.groq.com/docs/quickstart
 */
@Service
@Slf4j
public class GroqApiService {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;

    public GroqApiService(
            @Value("${groq.api.url:https://api.groq.com/openai/v1}") String apiUrl,
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.api.model:llama-3.3-70b-versatile}") String model,
            WebClient.Builder webClientBuilder
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Envia uma mensagem para o chatbot da Groq
     *
     * @param mensagem Mensagem do usuário
     * @param systemPrompt Prompt do sistema (contexto/instruções)
     * @param historico Histórico da conversa (opcional)
     * @return Resposta do chatbot
     */
    public String enviarMensagem(String mensagem, String systemPrompt, List<GroqChatRequestDTO.Message> historico) {
        try {
            // Construir lista de mensagens
            List<GroqChatRequestDTO.Message> messages = new ArrayList<>();
            
            // 1. System prompt (instruções)
            messages.add(new GroqChatRequestDTO.Message("system", systemPrompt));
            
            // 2. Histórico (se existir)
            if (historico != null && !historico.isEmpty()) {
                messages.addAll(historico);
            }
            
            // 3. Mensagem atual
            messages.add(new GroqChatRequestDTO.Message("user", mensagem));

            // Criar request
            GroqChatRequestDTO request = new GroqChatRequestDTO(
                    model,
                    messages,
                    0.7, // Temperatura moderada para respostas equilibradas
                    1024 // Max tokens
            );

            log.info("Enviando mensagem para Groq API - Modelo: {}, Tokens máx: {}", model, 1024);

            // Fazer chamada
            GroqChatResponseDTO response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GroqChatResponseDTO.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (response != null && response.getConteudo() != null) {
                log.info("Resposta recebida da Groq API - Tokens usados: {}", 
                        response.getUsage() != null ? response.getUsage().getTotalTokens() : "N/A");
                return response.getConteudo();
            }

            log.warn("Resposta vazia da Groq API");
            return "Desculpe, não consegui processar sua mensagem no momento.";

        } catch (WebClientResponseException e) {
            log.error("Erro na comunicação com Groq API - Status: {}, Body: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            return "Desculpe, ocorreu um erro ao processar sua mensagem. Tente novamente.";
        } catch (Exception e) {
            log.error("Erro inesperado ao comunicar com Groq API", e);
            return "Desculpe, nosso assistente está temporariamente indisponível.";
        }
    }

    /**
     * Envia mensagem simples sem histórico
     */
    public String enviarMensagem(String mensagem, String systemPrompt) {
        return enviarMensagem(mensagem, systemPrompt, null);
    }
}
