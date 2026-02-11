package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.dto.ChatMessageRequestDTO;
import com.example.bibliotecaapi.dto.ChatMessageResponseDTO;
import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.service.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para o chatbot da biblioteca
 */
@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chatbot", description = "Assistente virtual da biblioteca com IA")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/mensagem")
    @Operation(summary = "Enviar mensagem para o chatbot", 
               description = "Processa mensagem do usuário e retorna resposta com IA")
    public ResponseEntity<ChatMessageResponseDTO> enviarMensagem(
            @Valid @RequestBody ChatMessageRequestDTO request,
            Authentication authentication
    ) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            
            log.info("Mensagem recebida do chatbot - Usuário: {}, Mensagem: {}", 
                    usuario.getEmail(), request.getMensagem());

            ChatMessageResponseDTO response = chatbotService.processarMensagem(request, usuario);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao processar mensagem do chatbot", e);
            
            // Retornar resposta de erro amigável
            ChatMessageResponseDTO errorResponse = new ChatMessageResponseDTO();
            errorResponse.setResposta("Desculpe, ocorreu um erro ao processar sua mensagem. Por favor, tente novamente. 😔");
            errorResponse.setSessaoId(request.getSessaoId());
            errorResponse.setTimestamp(java.time.LocalDateTime.now());
            errorResponse.setConfianca(0);
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Verificar status do chatbot", 
               description = "Endpoint para verificar se o serviço está ativo")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chatbot operacional 🤖");
    }
}
