import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { API_URL, STORAGE_KEYS } from '@/constants';

const api = axios.create({
  baseURL: API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - adiciona token nas requisições
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
    
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - trata erros globalmente
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<{ mensagem?: string; message?: string }>) => {
    if (error.response) {
      const { status, data } = error.response;
      const errorMsg = data?.mensagem || data?.message || 'Erro desconhecido';
      
      switch (status) {
        case 401:
          console.error('Token inválido ou expirado. Redirecionando para login...');
          localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
          localStorage.removeItem(STORAGE_KEYS.USER_DATA);
          window.location.href = '/login';
          break;
        case 403:
          console.error('Acesso negado:', errorMsg);
          break;
        case 404:
          console.error('Recurso não encontrado:', errorMsg);
          break;
        case 500:
          console.error('Erro no servidor:', errorMsg);
          break;
        default:
          console.error('Erro na requisição:', errorMsg);
      }
      
      return Promise.reject({ message: errorMsg, mensagem: errorMsg });
    } else if (error.request) {
      console.error('Erro de conexão com o servidor. Backend não está rodando ou não está acessível em', API_URL);
      return Promise.reject({
        message: 'Erro de conexão. Verifique se o backend está rodando em ' + API_URL,
        mensagem: 'Erro de conexão. Verifique se o backend está rodando em ' + API_URL,
      });
    } else {
      console.error('Erro desconhecido:', error.message);
      return Promise.reject({
        message: 'Erro inesperado. Tente novamente.',
      });
    }
  }
);

export default api;
