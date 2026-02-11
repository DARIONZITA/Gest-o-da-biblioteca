package com.example.bibliotecaapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ocr_logs")
@EntityListeners(AuditingEntityListener.class)
public class OcrLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_arquivo_original")
    private String nomeArquivoOriginal;

    @Column(name = "caminho_arquivo_temp")
    private String caminhoArquivoTemp;

    @Column(name = "data_processamento")
    @CreatedDate
    private LocalDateTime dataProcessamento;

    private Long tempoProcessamentoMs;

    private Boolean sucesso;

    private Double confianca; // 0-100%

    @Column(name = "titulo_identificado")
    private String tituloIdentificado;

    @Column(name = "isbn_identificado")
    private String isbnIdentificado;

    @Column(columnDefinition = "TEXT")
    private String resultadoJson; // Armazena o JSON do resultado para auditoria

    private String usuarioLogado; // Se houver autenticação
}
