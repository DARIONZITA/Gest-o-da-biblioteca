import api from './api';
import { Autor, AutorFormData } from '@/types/autor';

export const autoresService = {
  async getAutores(): Promise<Autor[]> {
    const response = await api.get<Autor[]>('/autores');
    return response.data;
  },

  async getAutorById(id: string): Promise<Autor> {
    const response = await api.get<Autor>(`/autores/${id}`);
    return response.data;
  },

  async createAutor(data: AutorFormData): Promise<Autor> {
    const response = await api.post<Autor>('/autores', data);
    return response.data;
  },

  async deleteAutor(id: string): Promise<void> {
    await api.delete(`/autores/${id}`);
  },
};
