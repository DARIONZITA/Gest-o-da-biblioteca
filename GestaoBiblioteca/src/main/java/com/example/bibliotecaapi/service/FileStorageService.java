package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.config.FileStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    private final Path fileStorageLocation;
    
    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {
        this.fileStorageLocation = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath()
                .normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível criar o diretório de upload.", e);
        }
    }
    
    public String salvarArquivo(MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio não pode ser enviado.");
        }
        
        // Validar tipo de arquivo
        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Apenas imagens são permitidas.");
        }
        
        // Gerar nome único
        String nomeOriginal = arquivo.getOriginalFilename();
        String extensao = "";
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }
        
        String nomeArquivo = UUID.randomUUID().toString() + extensao;
        
        try {
            Path targetLocation = this.fileStorageLocation.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return nomeArquivo;
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível salvar o arquivo " + nomeArquivo, e);
        }
    }
    
    public void deletarArquivo(String nomeArquivo) {
        try {
            Path filePath = this.fileStorageLocation.resolve(nomeArquivo).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível deletar o arquivo " + nomeArquivo, e);
        }
    }
    
    public Path carregarArquivo(String nomeArquivo) {
        Path filePath = this.fileStorageLocation.resolve(nomeArquivo).normalize();
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Arquivo não encontrado: " + nomeArquivo);
        }
        return filePath;
    }
}
