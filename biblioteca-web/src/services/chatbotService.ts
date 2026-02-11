import api from './api';
import { ChatMessageRequest, ChatMessageResponse } from '../types/chat';

/**
 * Serviço para comunicação com o chatbot
 */
class ChatbotService {
  /**
   * Envia mensagem para o chatbot
   */
  async enviarMensagem(request: ChatMessageRequest): Promise<ChatMessageResponse> {
    const response = await api.post<ChatMessageResponse>('/chatbot/mensagem', request);
    return response.data;
  }

  /**
   * Verifica status do chatbot
   */
  async verificarStatus(): Promise<boolean> {
    try {
      const response = await api.get<string>('/chatbot/health');
      return response.status === 200;
    } catch (error) {
      console.error('Erro ao verificar status do chatbot:', error);
      return false;
    }
  }
}

export default new ChatbotService();
