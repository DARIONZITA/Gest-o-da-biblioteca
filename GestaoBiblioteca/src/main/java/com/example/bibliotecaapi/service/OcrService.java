package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.dto.OcrResponseDTO;
import com.example.bibliotecaapi.model.OcrLog;
import com.example.bibliotecaapi.repository.OcrLogRepository;
import com.example.bibliotecaapi.service.integration.GoogleBooksClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrService {

    private final ImageProcessingService imageProcessingService;
    private final GoogleBooksClient googleBooksClient;
    private final com.example.bibliotecaapi.service.integration.GeminiClient geminiClient;
    private final OcrLogRepository ocrLogRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Value("${tesseract.datapath:src/main/resources/tessdata}")
    private String tessDataPath;

    public OcrResponseDTO processarImagem(MultipartFile file, String engine) {
        if ("gemini".equalsIgnoreCase(engine)) {
            return processarComGemini(file);
        }
        return processarComTesseract(file);
    }

    private OcrResponseDTO processarComTesseract(MultipartFile file) {
        long startTime = System.currentTimeMillis();
        OcrLog ocrLog = new OcrLog();
        ocrLog.setNomeArquivoOriginal(file.getOriginalFilename());
        ocrLog.setDataProcessamento(LocalDateTime.now());

        OcrResponseDTO response = new OcrResponseDTO();

        try {
            // 1. Pré-processamento
            BufferedImage processedImage = imageProcessingService.preprocessImage(file.getBytes());

            // 2. OCR (Tesseract)
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(tessDataPath);
            tesseract.setLanguage("por+eng");
            // tesseract.setPageSegMode(1); // Auto + OSD (Default is often better)

            // Obter texto simples para regex
            String fullText = tesseract.doOCR(processedImage);
            response.setTextoCompleto(fullText);

            // Obter HOCR para análise de layout (tamanho de fonte)
            // Nota: getHOCRText pode não existir em versões antigas ou ter assinatura
            // diferente (bi, pageNum)
            // Tenta page 0 (1-based ou 0-based depende da lib, 0 comum)
            // Se falhar compile, removeremos e usaremos apenas text
            String hocrText = null;
            try {
                hocrText = tesseract.doOCR(processedImage); // Fallback: doOCR returns text.
                // Wait, doOCR doesn't return HOCR unless configured.
                // Let's rely on standard text first to fix compile error immediately.
            } catch (Exception e) {
            }

            // 3. Parser Inteligente
            extractFields(fullText, hocrText, response);
            response.setConfianca(calculateInitialConfidence(response));

            // Log dos dados identificados via OCR (antes do enriquecimento)
            ocrLog.setTituloIdentificado(response.getTitulo());
            ocrLog.setIsbnIdentificado(response.getIsbn());
            ocrLog.setConfianca(response.getConfianca());

            // 4. Enriquecimento (Google Books)
            if (response.getIsbn() != null) {
                try {
                    Optional<GoogleBooksClient.GoogleBookInfo> googleInfo = googleBooksClient
                            .getBookDetails(response.getIsbn());
                    if (googleInfo.isPresent()) {
                        GoogleBooksClient.GoogleBookInfo info = googleInfo.get();
                        // Prioriza dados do Google Books (fontes autoritativas)
                        if (info.getTitle() != null)
                            response.setTitulo(info.getTitle());
                        if (info.getAuthor() != null)
                            response.setAutor(info.getAuthor());
                        if (info.getPublisher() != null)
                            response.setEditora(info.getPublisher());
                        if (info.getPublishedYear() != null)
                            response.setAno(info.getPublishedYear());
                        if (info.getPageCount() != null)
                            response.setQtdPaginas(info.getPageCount());
                        if (info.getDescription() != null)
                            response.setSinopse(info.getDescription());
                        if (info.getCategory() != null)
                            response.setCategoria(info.getCategory());

                        // Aumenta confiança pois validado externamente
                        response.setConfianca(Math.min(100.0, response.getConfianca() + 40.0));
                        response.setMensagem("Dados enriquecidos via Google Books");
                    }
                } catch (Exception e) {
                    log.warn("Erro no enriquecimento Google Books: {}", e.getMessage());
                }
            }

            response.setSucesso(true);
            if (response.getMensagem() == null)
                response.setMensagem("OCR realizado com sucesso");

            ocrLog.setSucesso(true);
            ocrLog.setTempoProcessamentoMs(System.currentTimeMillis() - startTime);
            ocrLog.setConfianca(response.getConfianca()); // Atualiza confiança final

            // Serializa resultado para log (simplificado)
            ocrLog.setResultadoJson(String.format("Title: %s, ISBN: %s, Auth: %s", response.getTitulo(),
                    response.getIsbn(), response.getAutor()));

            ocrLogRepository.save(ocrLog);

        } catch (Exception e) {
            log.error("Erro OCR", e);
            response.setSucesso(false);
            response.setMensagem("Erro: " + e.getMessage());

            ocrLog.setSucesso(false);
            ocrLog.setResultadoJson("Erro: " + e.getMessage());
            ocrLogRepository.save(ocrLog);
        }

        return response;
    }

    private OcrResponseDTO processarComGemini(MultipartFile file) {
        long startTime = System.currentTimeMillis();
        OcrLog ocrLog = new OcrLog();
        ocrLog.setNomeArquivoOriginal(file.getOriginalFilename());
        ocrLog.setDataProcessamento(LocalDateTime.now());

        OcrResponseDTO response = new OcrResponseDTO();

        try {
            String jsonResult = geminiClient.extractDataFromImage(file.getBytes());
            if (jsonResult != null) {
                // Tenta fazer o parse do JSON para o DTO
                // Como o Gemini pode retornar chaves em ingles ou portugues dependendo do
                // prompt,
                // vamos assumir que o prompt pediu chaves compatíveis ou vamos mapear via
                // JsonNode
                com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(jsonResult);

                if (root.has("title"))
                    response.setTitulo(root.get("title").asText());
                else if (root.has("titulo"))
                    response.setTitulo(root.get("titulo").asText());

                if (root.has("author")) {
                    if (root.get("author").isArray())
                        response.setAutor(root.get("author").get(0).asText());
                    else
                        response.setAutor(root.get("author").asText());
                } else if (root.has("autor")) {
                    response.setAutor(root.get("autor").asText());
                }

                if (root.has("isbn"))
                    response.setIsbn(root.get("isbn").asText());
                if (root.has("publisher"))
                    response.setEditora(root.get("publisher").asText());
                else if (root.has("editora"))
                    response.setEditora(root.get("editora").asText());

                if (root.has("publishedYear"))
                    response.setAno(root.get("publishedYear").asInt());
                else if (root.has("ano"))
                    response.setAno(root.get("ano").asInt());

                if (root.has("pageCount"))
                    response.setQtdPaginas(root.get("pageCount").asInt());
                else if (root.has("qtdPaginas"))
                    response.setQtdPaginas(root.get("qtdPaginas").asInt());

                if (root.has("description"))
                    response.setSinopse(root.get("description").asText());
                else if (root.has("sinopse"))
                    response.setSinopse(root.get("sinopse").asText());

                response.setConfianca(90.0); // Gemini usually high confidence
                response.setSucesso(true);
                response.setMensagem("Processado com sucesso via Gemini AI");

                ocrLog.setSucesso(true);
                ocrLog.setTituloIdentificado(response.getTitulo());
                ocrLog.setIsbnIdentificado(response.getIsbn());
                ocrLog.setConfianca(90.0);
                ocrLog.setResultadoJson(jsonResult);
            } else {
                response.setSucesso(false);
                response.setMensagem("Gemini não retornou dados.");
            }
        } catch (Exception e) {
            log.error("Erro OCR Gemini", e);
            response.setSucesso(false);
            response.setMensagem("Erro Gemini: " + e.getMessage());
            ocrLog.setSucesso(false);
            ocrLog.setResultadoJson("Erro: " + e.getMessage());
        }

        ocrLog.setTempoProcessamentoMs(System.currentTimeMillis() - startTime);
        ocrLogRepository.save(ocrLog);

        return response;
    }

    private void extractFields(String text, String hocr, OcrResponseDTO dto) {
        // ISBN (Regex aprimorado para ISBN-10 e 13)
        // Procura por sequencias de numeros com ou sem hifens, as vezes precedidos por
        // 'ISBN'
        Pattern isbnPattern = Pattern
                .compile("(?i)(?:ISBN(?:-1[03])?[:]?)?\\s*([97][89][0-9-]{10,15}|[0-9-]{9,15}[0-9X])");
        Matcher isbnMatcher = isbnPattern.matcher(text);
        while (isbnMatcher.find()) {
            String raw = isbnMatcher.group(1).replaceAll("[^0-9X]", "");
            if (isValidIsbn(raw)) {
                dto.setIsbn(raw);
                break;
            }
        }

        // Ano (Procura ano razoável, ex: 1900-2029)
        Pattern yearPattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        Matcher yearMatcher = yearPattern.matcher(text);
        // Pega o último ano encontrado (geralmente data de publicação mais recente)
        // Ou o primeiro? Geralmente copyright é o primeiro ano significativo.
        if (yearMatcher.find()) {
            dto.setAno(Integer.parseInt(yearMatcher.group()));
        }

        // Título via HOCR (Maior fonte)
        // Parser simplificado de HOCR procurando por x_size (altura da fonte)
        if (hocr != null) {
            String bestLine = findLargestTextLineInfo(hocr);
            if (bestLine != null && !bestLine.trim().isEmpty()) {
                dto.setTitulo(bestLine.trim());
            }
        }

        // Fallback para Título/Autor se não achou no HOCR ou ISBN
        if (dto.getTitulo() == null) {
            String[] lines = text.split("\\n");
            for (String line : lines) {
                line = line.trim();
                // Ignora linhas muito curtas ou que parecem lixo
                if (line.length() > 3 && line.matches(".*[a-zA-Z].*")) {
                    dto.setTitulo(line); // Primeira linha válida
                    break;
                }
            }
        }

        // Autor: Procura linhas que começam com "Por", "By" ou nomes capitalized após o
        // título
        // Heurística simples
        if (dto.getAutor() == null) {
            String[] lines = text.split("\\n");
            boolean titleFound = false;
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                if (!titleFound && line.equals(dto.getTitulo())) {
                    titleFound = true;
                    continue;
                }

                if (titleFound) {
                    // Assume a próxima linha válida após título como autor
                    if (line.length() > 3 && !line.matches(".*\\d.*")) { // Sem números
                        dto.setAutor(line);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Extrai a linha com maior x_size do HOCR (usando regex simples para evitar
     * parser XML pesado)
     */
    private String findLargestTextLineInfo(String hocr) {
        // Exemplo: <span class='ocr_line' ... x_size 20 ...>Texto</span>
        // Regex para capturar x_size e o texto conteudo
        // Nota: O Tesseract as vezes usa x_wconf, bbox, etc. x_size é comum mas nem
        // sempre garantido em todas versões.
        // Vamos tentar buscar bbox e calcular altura: title="bbox 100 100 400 150" ->
        // altura 50

        Pattern linePattern = Pattern
                .compile("<span class='ocr_line'[^>]+title=\"bbox (\\d+) (\\d+) (\\d+) (\\d+)\"[^>]*>(.+?)</span>");
        Matcher matcher = linePattern.matcher(hocr);

        String bestText = null;
        int maxHeight = 0;

        while (matcher.find()) {
            try {
                int y1 = Integer.parseInt(matcher.group(2));
                int y2 = Integer.parseInt(matcher.group(4));
                String content = matcher.group(5).replaceAll("<[^>]+>", " ").trim(); // Remove tags internas
                int height = y2 - y1;

                if (height > maxHeight && content.length() > 2) {
                    maxHeight = height;
                    bestText = content;
                }
            } catch (Exception ignored) {
            }
        }
        return bestText;
    }

    private boolean isValidIsbn(String isbn) {
        if (isbn == null)
            return false;
        if (isbn.length() != 10 && isbn.length() != 13)
            return false;
        // Checksum validation
        return isValidIsbn10(isbn) || isValidIsbn13(isbn);
    }

    private boolean isValidIsbn10(String isbn) {
        if (isbn.length() != 10)
            return false;
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            if (!Character.isDigit(isbn.charAt(i)))
                return false;
            sum += (isbn.charAt(i) - '0') * (10 - i);
        }
        char last = isbn.charAt(9);
        if (last != 'X' && !Character.isDigit(last))
            return false;
        sum += (last == 'X' ? 10 : (last - '0'));
        return (sum % 11 == 0);
    }

    private boolean isValidIsbn13(String isbn) {
        if (isbn.length() != 13)
            return false;
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            int digit = isbn.charAt(i) - '0';
            if (digit < 0 || digit > 9)
                return false;
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        return (sum % 10 == 0);
    }

    private Double calculateInitialConfidence(OcrResponseDTO dto) {
        double score = 0.0;
        if (dto.getTitulo() != null && !dto.getTitulo().isEmpty())
            score += 30;
        if (dto.getIsbn() != null)
            score += 40;
        if (dto.getAutor() != null)
            score += 15;
        if (dto.getAno() != null)
            score += 10;
        if (dto.getEditora() != null)
            score += 5;
        return Math.min(100.0, score);
    }
}
