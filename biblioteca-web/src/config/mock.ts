// Configuração para ativar/desativar modo MOCK
// MOCK desactivado — todas as chamadas vão para o backend real
export const MOCK_ENABLED = false;

// Usuários mock para testes
export const MOCK_USERS = {
  ADMIN: {
    matricula: '20230001',
    senha: 'admin123',
    data: {
      id: 'mock-admin-001',
      nome: 'João Silva',
      matricula: '20230001',
      email: 'joao.silva@isptec.co.ao',
      tipo: 'ADMIN',
      status: 'ATIVO',
      dataCadastro: '2024-01-15T10:00:00Z',
      multasPendentes: 0,
      emprestimosAtivos: 2,
      token: 'mock-token-admin-' + Date.now(),
    },
  },
  ESTUDANTE: {
    matricula: '20230002',
    senha: 'estudante123',
    data: {
      id: 'mock-student-002',
      nome: 'Maria Santos',
      matricula: '20230002',
      email: 'maria.santos@isptec.co.ao',
      tipo: 'ESTUDANTE',
      status: 'ATIVO',
      dataCadastro: '2024-02-01T10:00:00Z',
      multasPendentes: 0,
      emprestimosAtivos: 1,
      curso: 'Engenharia Informática',
      anoIngresso: 2023,
      token: 'mock-token-student-' + Date.now(),
    },
  },
  PROFESSOR: {
    matricula: '20230003',
    senha: 'professor123',
    data: {
      id: 'mock-prof-003',
      nome: 'Dr. Carlos Mendes',
      matricula: '20230003',
      email: 'carlos.mendes@isptec.co.ao',
      tipo: 'PROFESSOR',
      status: 'ATIVO',
      dataCadastro: '2023-08-01T10:00:00Z',
      multasPendentes: 0,
      emprestimosAtivos: 3,
      token: 'mock-token-professor-' + Date.now(),
    },
  },
};

// Simula delay de rede (opcional)
export const simulateNetworkDelay = (ms: number = 500) => {
  return new Promise(resolve => setTimeout(resolve, ms));
};
