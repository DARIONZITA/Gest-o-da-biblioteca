package com.example.bibliotecaapi.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice // Diz ao Spring: "Eu cuido dos erros de todos os Controllers"
public class GlobalExceptionHandler {

    // ALVO: Erro de conversão (Letras no lugar de números)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> erroDeTipo(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "mensagem", "Erro de formato: esperado um número, mas recebeste texto."
        ));
    }

    // ALVO: Erros que TU lanças no Service (ex: Matrícula Duplicada)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> erroDeRegra(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "mensagem", ex.getMessage() // Vai mostrar o que escreveste no throw
        ));
    }
}

