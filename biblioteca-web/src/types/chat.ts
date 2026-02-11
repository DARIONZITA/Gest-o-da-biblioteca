import { RecomendacaoResponseDTO } from './recomendacao';

export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  livrosSugeridos?: RecomendacaoResponseDTO[];
}

export interface ChatMessageRequest {
  mensagem: string;
  sessaoId?: string;
}

export interface ChatMessageResponse {
  resposta: string;
  sessaoId: string;
  timestamp: string;
  livrosSugeridos?: RecomendacaoResponseDTO[];
  confianca?: number;
}

export interface ChatSession {
  id: string;
  messages: ChatMessage[];
  createdAt: Date;
  updatedAt: Date;
}
