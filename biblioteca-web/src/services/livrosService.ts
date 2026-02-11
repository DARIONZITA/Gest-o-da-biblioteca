import api from './api';
import { Livro, LivroFormData } from '@/types/livro';

export const livrosService = {
  async getLivros(): Promise<Livro[]> {
    const response = await api.get<Livro[]>('/livros');
    return response.data;
  },

  async getLivroByIsbn(isbn: string): Promise<Livro> {
    const response = await api.get<Livro>(`/livros/${isbn}`);
    return response.data;
  },

  async createLivro(data: LivroFormData): Promise<Livro> {
    const response = await api.post<Livro>('/livros', data);
    return response.data;
  },

  async deleteLivro(isbn: string): Promise<void> {
    await api.delete(`/livros/${isbn}`);
  },

  async uploadCapa(file: File): Promise<string> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await api.post<{ capaURL: string }>('/livros/upload-capa', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data.capaURL;
  },
  confirmarDevolucao: async (id: string, bomEstado: boolean): Promise<void> => {
    await api.post(`/emprestimos/${id}/devolucao`, { bomEstado });
  },

  searchExternal: async (query: string): Promise<any[]> => {
    const response = await api.get<any[]>('/livros/external-search', {
      params: { query }
    });
    return response.data;
  }
};
