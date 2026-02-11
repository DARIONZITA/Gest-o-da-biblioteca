import api from './api';
import { Emprestimo, EmprestimoRequestData, EmprestimoRascunhoRequestData } from '@/types/emprestimo';

export const emprestimosService = {
  async getEmprestimos(): Promise<Emprestimo[]> {
    const response = await api.get<Emprestimo[]>('/emprestimos');
    return response.data;
  },

  async getEmprestimoById(id: string): Promise<Emprestimo> {
    const response = await api.get<Emprestimo>(`/emprestimos/${id}`);
    return response.data;
  },

  async createEmprestimo(data: EmprestimoRequestData): Promise<Emprestimo> {
    const response = await api.post<Emprestimo>('/emprestimos', data);
    return response.data;
  },

  async createRascunho(data: EmprestimoRascunhoRequestData): Promise<Emprestimo> {
    const response = await api.post<Emprestimo>('/emprestimos/rascunho', data);
    return response.data;
  },

  async devolver(id: string): Promise<Emprestimo> {
    const response = await api.patch<Emprestimo>(`/emprestimos/${id}/devolver`);
    return response.data;
  },

  async renovar(id: string): Promise<Emprestimo> {
    const response = await api.patch<Emprestimo>(`/emprestimos/${id}/renovar`);
    return response.data;
  },

  async aprovar(id: string): Promise<Emprestimo> {
    const response = await api.patch<Emprestimo>(`/emprestimos/${id}/aprovar`);
    return response.data;
  },

  async getMeusEmprestimos(): Promise<Emprestimo[]> {
    const response = await api.get<Emprestimo[]>('/emprestimos/meus');
    return response.data;
  },
};
