import api from './api';
import { Usuario, UsuarioRequestData } from '@/types/usuario';

export const usuariosService = {
  async getUsuarios(): Promise<Usuario[]> {
    const response = await api.get<Usuario[]>('/usuarios');
    return response.data;
  },

  async getUsuarioById(id: string): Promise<Usuario> {
    const response = await api.get<Usuario>(`/usuarios/${id}`);
    return response.data;
  },

  async getMe(): Promise<Usuario> {
    const response = await api.get<Usuario>('/usuarios/me');
    return response.data;
  },

  async createUsuario(data: UsuarioRequestData): Promise<Usuario> {
    const response = await api.post<Usuario>('/usuarios', data);
    return response.data;
  },

  async deleteUsuario(id: string): Promise<void> {
    await api.delete(`/usuarios/${id}`);
  },
};
