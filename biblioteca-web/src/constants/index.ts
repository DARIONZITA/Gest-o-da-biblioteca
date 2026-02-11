export const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export const ROUTES = {
  LOGIN: '/login',

  // Admin Routes
  ADMIN_DASHBOARD: '/admin/dashboard',
  ADMIN_LIVROS: '/admin/livros',
  ADMIN_LIVROS_NOVO: '/admin/livros/novo',
  ADMIN_LIVROS_EDITAR: '/admin/livros/:id/editar',
  ADMIN_MEMBROS: '/admin/membros',
  ADMIN_MEMBROS_NOVO: '/admin/membros/novo',
  ADMIN_DEVOLUCOES: '/admin/devolucoes',
  ADMIN_EMPRESTIMOS: '/admin/emprestimos',
  ADMIN_RESERVAS: '/admin/reservas',
  ADMIN_PAGAMENTOS: '/admin/pagamentos',
  ADMIN_CATEGORIAS: '/admin/categorias',
  ADMIN_AUTORES: '/admin/autores',
  ADMIN_RELATORIOS: '/admin/relatorios',

  // Member Routes
  MEMBRO_CATALOGO: '/membro/catalogo',
  MEMBRO_RECOMENDACOES: '/membro/recomendacoes',
  MEMBRO_EMPRESTIMOS: '/membro/emprestimos',
  MEMBRO_RESERVAS: '/membro/reservas',
  MEMBRO_PERFIL: '/membro/perfil',
} as const;

export const STORAGE_KEYS = {
  AUTH_TOKEN: '@biblioteca:token',
  USER_DATA: '@biblioteca:user',
} as const;

export const LOAN_DURATION_DAYS = 14;
export const MAX_RENEWALS = 2;
export const FINE_PER_DAY = 50; // Valor em Kz
export const RESERVATION_EXPIRY_HOURS = 48;
