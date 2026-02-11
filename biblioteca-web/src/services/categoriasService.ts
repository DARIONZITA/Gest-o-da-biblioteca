import api from './api';
import { Categoria, CategoriaFormData } from '@/types/categoria';

export const categoriasService = {
  async getCategorias(): Promise<Categoria[]> {
    const response = await api.get<Categoria[]>('/categorias');
    return response.data;
  },

  async getCategoriaById(id: string): Promise<Categoria> {
    const response = await api.get<Categoria>(`/categorias/${id}`);
    return response.data;
  },

  async createCategoria(data: CategoriaFormData): Promise<Categoria> {
    const response = await api.post<Categoria>('/categorias', data);
    return response.data;
  },

  async deleteCategoria(id: string): Promise<void> {
    await api.delete(`/categorias/${id}`);
  },
};
