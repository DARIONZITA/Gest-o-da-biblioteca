import api from './api';
import { OcrResponse } from '../types/ocr';

/**
 * Serviço para OCR (Optical Character Recognition)
 */
class OcrService {
  /**
   * Processa imagem e extrai informações do livro
   */
  async processarImagem(file: File, engine: 'gemini' | 'tesseract' = 'gemini'): Promise<OcrResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('engine', engine);

    const response = await api.post<OcrResponse>('/ocr/processar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  }

  /**
   * Verifica status do serviço OCR
   */
  async verificarStatus(): Promise<boolean> {
    try {
      const response = await api.get<string>('/ocr/health');
      return response.status === 200;
    } catch (error) {
      console.error('Erro ao verificar status do OCR:', error);
      return false;
    }
  }
}

export default new OcrService();
