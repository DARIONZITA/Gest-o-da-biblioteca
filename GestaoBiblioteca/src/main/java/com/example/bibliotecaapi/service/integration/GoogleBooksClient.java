package com.example.bibliotecaapi.service.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GoogleBooksClient {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @org.springframework.beans.factory.annotation.Value("${google.books.api.key:}")
    private String apiKey;

    public GoogleBooksClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    private String appendKey(String url) {
        if (apiKey != null && !apiKey.isBlank()) {
            return url + "&key=" + apiKey;
        }
        return url;
    }

    public Optional<GoogleBookInfo> getBookDetails(String isbn) {
        String url = appendKey("https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn);
        try {
            log.info("Google Books - Buscando ISBN: {}", isbn);
            String response = restTemplate.getForObject(url, String.class);
            if (response != null) {
                JsonNode root = objectMapper.readTree(response);
                if (root.has("items") && root.get("items").size() > 0) {
                    JsonNode volumeInfo = root.get("items").get(0).get("volumeInfo");
                    return Optional.of(mapToInfo(volumeInfo));
                }
            }
            log.warn("Google Books - Nenhum resultado para ISBN: {}", isbn);
        } catch (Exception e) {
            log.warn("Erro ao consultar Google Books API para ISBN {}: {}", isbn, e.getMessage());
        }
        return Optional.empty();
    }

    public List<GoogleBookInfo> searchBooks(String query) {
        String encodedQuery = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
        String url = appendKey("https://www.googleapis.com/books/v1/volumes?q=" + encodedQuery + "&maxResults=20");
        try {
            log.info("Google Books - Pesquisando: '{}' -> URL: {}", query, url);
            String response = restTemplate.getForObject(url, String.class);
            log.info("Google Books - Resposta recebida, tamanho: {} bytes", response != null ? response.length() : 0);
            if (response != null) {
                JsonNode root = objectMapper.readTree(response);
                log.info("Google Books - totalItems: {}",
                        root.has("totalItems") ? root.get("totalItems").asInt() : "N/A");
                if (root.has("items")) {
                    List<GoogleBookInfo> books = new ArrayList<>();
                    for (JsonNode item : root.get("items")) {
                        JsonNode volumeInfo = item.get("volumeInfo");
                        books.add(mapToInfo(volumeInfo));
                    }
                    log.info("Google Books - {} livros encontrados", books.size());
                    return books;
                }
            }
        } catch (Exception e) {
            log.error("Erro ao pesquisar no Google Books API para query '{}': {}", query, e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private GoogleBookInfo mapToInfo(JsonNode volumeInfo) {
        GoogleBookInfo info = new GoogleBookInfo();
        if (volumeInfo.has("title"))
            info.setTitle(volumeInfo.get("title").asText());
        if (volumeInfo.has("authors")) {
            JsonNode authors = volumeInfo.get("authors");
            if (authors.isArray() && authors.size() > 0) {
                info.setAuthor(authors.get(0).asText());
            }
        }
        if (volumeInfo.has("publisher"))
            info.setPublisher(volumeInfo.get("publisher").asText());
        if (volumeInfo.has("publishedDate")) {
            String date = volumeInfo.get("publishedDate").asText();
            if (date.length() >= 4) {
                try {
                    info.setPublishedYear(Integer.parseInt(date.substring(0, 4)));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (volumeInfo.has("pageCount"))
            info.setPageCount(volumeInfo.get("pageCount").asInt());
        if (volumeInfo.has("description"))
            info.setDescription(volumeInfo.get("description").asText());
        if (volumeInfo.has("categories")) {
            JsonNode cats = volumeInfo.get("categories");
            if (cats.isArray() && cats.size() > 0) {
                info.setCategory(cats.get(0).asText());
            }
        }

        // Identificadores (ISBN)
        if (volumeInfo.has("industryIdentifiers")) {
            for (JsonNode id : volumeInfo.get("industryIdentifiers")) {
                if ("ISBN_13".equals(id.get("type").asText())) {
                    info.setIsbn(id.get("identifier").asText());
                } else if (info.getIsbn() == null && "ISBN_10".equals(id.get("type").asText())) {
                    info.setIsbn(id.get("identifier").asText());
                }
            }
        }

        if (volumeInfo.has("imageLinks")) {
            JsonNode images = volumeInfo.get("imageLinks");
            if (images.has("thumbnail"))
                info.setThumbnailUrl(images.get("thumbnail").asText());
            else if (images.has("smallThumbnail"))
                info.setThumbnailUrl(images.get("smallThumbnail").asText());
        }

        return info;
    }

    @Data
    public static class GoogleBookInfo {
        private String title;
        private String author;
        private String publisher;
        private Integer publishedYear;
        private Integer pageCount;
        private String description;
        private String category;
        private String isbn;
        private String thumbnailUrl;
    }
}
