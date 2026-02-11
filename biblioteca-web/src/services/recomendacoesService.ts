import api from './api';
import { Recomendacao } from '../types/recomendacao';

/**
 * Serviço de Recomendações de Livros
 * Sistema de IA que sugere livros baseado em histórico e preferências
 */

/**
 * Obtém recomendações personalizadas para o usuário autenticado
 */
export const getRecomendacoes = async (): Promise<Recomendacao[]> => {
  const response = await api.get<Recomendacao[]>('/recomendacoes');
  return response.data;
};

/**
 * Obtém recomendações para um usuário específico (ADMIN only)
 */
export const getRecomendacoesUsuario = async (usuarioId: string): Promise<Recomendacao[]> => {
  const response = await api.get<Recomendacao[]>(`/recomendacoes/${usuarioId}`);
  return response.data;
};

/**
 * Obtém livros mais populares (últimos 30 dias)
 * Endpoint público - não requer autenticação
 */
export const getLivrosPopulares = async (): Promise<Recomendacao[]> => {
  const response = await api.get<Recomendacao[]>('/recomendacoes/populares');
  return response.data;
};

export default {
  getRecomendacoes,
  getRecomendacoesUsuario,
  getLivrosPopulares,
};
