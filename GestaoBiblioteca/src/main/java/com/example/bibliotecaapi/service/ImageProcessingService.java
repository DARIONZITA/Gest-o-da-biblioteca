package com.example.bibliotecaapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Serviço para pré-processamento de imagens antes do OCR
 * Melhora a precisão da extração de texto
 */
@Service
@Slf4j
public class ImageProcessingService {

    /**
     * Processa imagem para melhorar precisão do OCR
     * - Converte para escala de cinza
     * - Aumenta contraste e aplica binarização
     * - Tenta rotação básica (se necessário, Tesseract faz o fino ajuste)
     */
    public BufferedImage preprocessImage(byte[] imageBytes) throws IOException {
        BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageBytes));

        if (original == null) {
            throw new IOException("Não foi possível ler a imagem");
        }

        log.info("Processando imagem original: {}x{}", original.getWidth(), original.getHeight());

        // 1. Redimensionar se necessário
        BufferedImage resized = optimizeSize(original);

        // 2. Corrigir inclinação (Deskew)
        BufferedImage deskewed = deskewImage(resized);

        // 3. Escala de cinza
        BufferedImage grayscale = net.sourceforge.tess4j.util.ImageHelper.convertImageToGrayscale(deskewed);

        // 4. Binarização (Otsu ou similar via Helper)
        BufferedImage binary = net.sourceforge.tess4j.util.ImageHelper.convertImageToBinary(grayscale);

        log.info("Imagem processada para OCR: {}x{}", binary.getWidth(), binary.getHeight());
        return binary;
    }

    private BufferedImage deskewImage(BufferedImage image) {
        try {
            com.recognition.software.jdeskew.ImageDeskew deskew = new com.recognition.software.jdeskew.ImageDeskew(
                    image);
            double imageSkewAngle = deskew.getSkewAngle();

            if ((imageSkewAngle > 0.05 || imageSkewAngle < -0.05)) {
                log.info("Detectada inclinação de {} graus. Corrigindo...", imageSkewAngle);
                return net.sourceforge.tess4j.util.ImageHelper.rotateImage(image, -imageSkewAngle);
            }
        } catch (Exception e) {
            log.warn("Falha ao tentar deskew da imagem: {}", e.getMessage());
        }
        return image;
    }

    private BufferedImage optimizeSize(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Se muito grande, reduz
        if (width > 2500 || height > 2500) {
            double scale = Math.min(2500.0 / width, 2500.0 / height);
            int newW = (int) (width * scale);
            int newH = (int) (height * scale);
            return net.sourceforge.tess4j.util.ImageHelper.getScaledInstance(image, newW, newH);
        }

        // Se muito pequeno (ex: thumbnail), aumenta
        if (width < 800) {
            return net.sourceforge.tess4j.util.ImageHelper.getScaledInstance(image, width * 2, height * 2);
        }

        return image;
    }

    public byte[] toByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }
}
