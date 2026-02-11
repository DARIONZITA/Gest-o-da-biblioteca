import api from './api';
import { UsuarioAuth } from '@/types/usuario';
import { STORAGE_KEYS } from '@/constants';

interface LoginCredentials {
  email: string;
  senha: string;
}

/** Formato da resposta do POST /auth/login no backend */
interface LoginResponse {
  token: string;
  tokenType: string;
  expiresAt: number;
  usuario: {
    id: string;
    nome: string;
    email: string;
    perfil: string;
  };
}

export const authService = {
  async login(credentials: LoginCredentials): Promise<UsuarioAuth> {
    const response = await api.post<LoginResponse>('/auth/login', credentials);
    const { token, usuario } = response.data;

    const userData: UsuarioAuth = {
      id: usuario.id,
      nome: usuario.nome,
      email: usuario.email,
      perfil: usuario.perfil as UsuarioAuth['perfil'],
      token,
    };

    localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, token);
    localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(userData));
    return userData;
  },

  logout() {
    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER_DATA);
    window.location.href = '/login';
  },

  getCurrentUser(): UsuarioAuth | null {
    const userData = localStorage.getItem(STORAGE_KEYS.USER_DATA);
    return userData ? JSON.parse(userData) : null;
  },

  isAuthenticated(): boolean {
    const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
    return !!token;
  },

  getToken(): string | null {
    return localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
  },
};
