package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.dto.OcrResponseDTO;
import com.example.bibliotecaapi.service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller para processamento OCR de capas de livros
 */
@RestController
@RequestMapping("/ocr")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OCR", description = "Reconhecimento ótico de caracteres para catalogação de livros")
public class OcrController {

    private final OcrService ocrService;

    @PostMapping("/processar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @Operation(summary = "Processar imagem via OCR", description = "Envia uma imagem para extração de texto e dados do livro. Engine pode ser 'gemini' ou 'tesseract'.")
    public ResponseEntity<OcrResponseDTO> processarImagem(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "engine", defaultValue = "gemini") String engine) {
        try {
            log.info("Requisição OCR recebida - Arquivo: {}, Tamanho: {} bytes, Engine: {}",
                    file.getOriginalFilename(), file.getSize(), engine);

            // Validar tamanho (máximo 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                OcrResponseDTO error = new OcrResponseDTO();
                error.setSucesso(false);
                error.setMensagem("Arquivo muito grande. Tamanho máximo: 10MB");
                error.setConfianca(0.0);
                return ResponseEntity.badRequest().body(error);
            }

            OcrResponseDTO response = ocrService.processarImagem(file, engine);

            if (response.getSucesso()) {
                log.info("OCR bem-sucedido - Confiança: {}%", response.getConfianca());
                return ResponseEntity.ok(response);
            } else {
                log.warn("OCR falhou: {}", response.getMensagem());
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            log.error("Erro ao processar OCR", e);

            OcrResponseDTO error = new OcrResponseDTO();
            error.setSucesso(false);
            error.setMensagem("Erro interno ao processar imagem");
            error.setConfianca(0.0);

            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Verificar status do OCR", description = "Endpoint para verificar se o serviço OCR está ativo")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OCR Service operacional 🔍");
    }
}
