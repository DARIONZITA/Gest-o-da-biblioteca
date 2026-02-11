// @ts-nocheck
// This file is no longer used - all pages now use real API services
import { Livro, StatusLivro, CategoriaLivro } from '@/types/livro';
import { Usuario, TipoUsuario, StatusUsuario } from '@/types/usuario';
import { Categoria } from '@/types/categoria';
import { Autor } from '@/types/autor';
import { StatusPagamento, Pagamento } from '@/types/pagamento';
import { StatusEmprestimo } from '@/types/emprestimo';
import { StatusReserva } from '@/types/reserva';

// ===== LIVROS MOCK =====
export const MOCK_LIVROS: Livro[] = [
  {
    id: '1',
    isbn: '978-0132350884',
    titulo: 'Clean Code',
    autor: 'Robert C. Martin',
    editora: 'Prentice Hall',
    anoPublicacao: 2008,
    categoria: CategoriaLivro.TECNICO,
    quantidadeTotal: 5,
    quantidadeDisponivel: 3,
    localizacao: 'A-01-15',
    status: StatusLivro.DISPONIVEL,
    descricao: 'Um guia completo sobre como escrever código limpo e manutenível.',
    capaUrl: 'https://m.media-amazon.com/images/I/51E2055ZGUL._SX218_BO1,204,203,200_QL40_FMwebp_.jpg',
  },
  {
    id: '2',
    isbn: '978-0262033848',
    titulo: 'Introduction to Algorithms',
    autor: 'Thomas H. Cormen',
    editora: 'MIT Press',
    anoPublicacao: 2009,
    categoria: CategoriaLivro.TECNICO,
    quantidadeTotal: 3,
    quantidadeDisponivel: 0,
    localizacao: 'A-02-08',
    status: StatusLivro.EMPRESTADO,
    descricao: 'O livro definitivo sobre algoritmos e estruturas de dados.',
    capaUrl: 'https://m.media-amazon.com/images/I/61Pgdn8Ys-L._SY522_.jpg',
  },
  {
    id: '3',
    isbn: '978-9895556373',
    titulo: 'Mayombe',
    autor: 'Pepetela',
    editora: 'Leya',
    anoPublicacao: 2013,
    categoria: CategoriaLivro.LITERATURA,
    quantidadeTotal: 10,
    quantidadeDisponivel: 8,
    localizacao: 'L-12-03',
    status: StatusLivro.DISPONIVEL,
    descricao: 'Romance que retrata a luta pela independência de Angola.',
    capaUrl: 'https://img.wook.pt/images/mayombe-pepetela/MXwyMzExMzg5MHwxNzI4NTcyOHwxNDQwMTAyMDAwMDAw/500x',
  },
  {
    id: '4',
    isbn: '978-0596007126',
    titulo: 'Head First Design Patterns',
    autor: 'Eric Freeman',
    editora: "O'Reilly Media",
    anoPublicacao: 2004,
    categoria: CategoriaLivro.TECNICO,
    quantidadeTotal: 4,
    quantidadeDisponivel: 2,
    localizacao: 'A-01-22',
    status: StatusLivro.DISPONIVEL,
    descricao: 'Aprenda padrões de design de forma visual e interativa.',
  },
  {
    id: '5',
    isbn: '978-0201633610',
    titulo: 'Design Patterns',
    autor: 'Gang of Four',
    editora: 'Addison-Wesley',
    anoPublicacao: 1994,
    categoria: CategoriaLivro.TECNICO,
    quantidadeTotal: 2,
    quantidadeDisponivel: 2,
    localizacao: 'A-01-23',
    status: StatusLivro.DISPONIVEL,
    descricao: 'O livro clássico sobre padrões de design orientados a objetos.',
  },
];

// ===== USUÁRIOS MOCK =====
export const MOCK_USUARIOS: Usuario[] = [
  {
    id: 'mock-admin-001',
    nome: 'João Silva',
    matricula: '20230001',
    email: 'joao.silva@isptec.co.ao',
    tipo: TipoUsuario.ADMIN,
    status: StatusUsuario.ATIVO,
    dataCadastro: '2024-01-15T10:00:00Z',
    multasPendentes: 0,
    emprestimosAtivos: 2,
  },
  {
    id: 'mock-student-002',
    nome: 'Maria Santos',
    matricula: '20230002',
    email: 'maria.santos@isptec.co.ao',
    tipo: TipoUsuario.ESTUDANTE,
    status: StatusUsuario.ATIVO,
    dataCadastro: '2024-02-01T10:00:00Z',
    multasPendentes: 0,
    emprestimosAtivos: 1,
    curso: 'Engenharia Informática',
    anoIngresso: 2023,
  },
  {
    id: 'mock-prof-003',
    nome: 'Dr. Carlos Mendes',
    matricula: '20230003',
    email: 'carlos.mendes@isptec.co.ao',
    tipo: TipoUsuario.PROFESSOR,
    status: StatusUsuario.ATIVO,
    dataCadastro: '2023-08-01T10:00:00Z',
    multasPendentes: 0,
    emprestimosAtivos: 3,
  },
  {
    id: 'mock-student-004',
    nome: 'Ana Costa',
    matricula: '20230004',
    email: 'ana.costa@isptec.co.ao',
    tipo: TipoUsuario.ESTUDANTE,
    status: StatusUsuario.ATIVO,
    dataCadastro: '2024-03-10T10:00:00Z',
    multasPendentes: 150,
    emprestimosAtivos: 0,
    curso: 'Gestão de TI',
    anoIngresso: 2023,
  },
  {
    id: 'mock-student-005',
    nome: 'Pedro Alves',
    matricula: '20230005',
    email: 'pedro.alves@isptec.co.ao',
    tipo: TipoUsuario.ESTUDANTE,
    status: StatusUsuario.BLOQUEADO,
    dataCadastro: '2023-09-01T10:00:00Z',
    multasPendentes: 500,
    emprestimosAtivos: 0,
    curso: 'Engenharia Informática',
    anoIngresso: 2023,
  },
];

// ===== EMPRÉSTIMOS MOCK =====
export const MOCK_EMPRESTIMOS: any[] = [
  {
    id: 'emp-001',
    livro: { id: '1', titulo: 'Clean Code', autor: 'Robert C. Martin' },
    usuario: { id: 'mock-student-002', nome: 'Maria Santos', matricula: '20230002' },
    dataEmprestimo: '2026-01-20T10:00:00Z',
    dataDevolucaoPrevista: '2026-02-03T10:00:00Z',
    dataDevolucaoReal: null,
    status: 'ATIVO',
    numeroRenovacoes: 0,
    multa: 0,
  },
  {
    id: 'emp-002',
    livro: { id: '2', titulo: 'Introduction to Algorithms', autor: 'Thomas H. Cormen' },
    usuario: { id: 'mock-prof-003', nome: 'Dr. Carlos Mendes', matricula: '20230003' },
    dataEmprestimo: '2026-01-10T10:00:00Z',
    dataDevolucaoPrevista: '2026-01-24T10:00:00Z',
    dataDevolucaoReal: null,
    status: 'ATRASADO',
    numeroRenovacoes: 1,
    multa: 800,
  },
  {
    id: 'emp-003',
    livro: { id: '4', titulo: 'Head First Design Patterns', autor: 'Eric Freeman' },
    usuario: { id: 'mock-student-004', nome: 'Ana Costa', matricula: '20230004' },
    dataEmprestimo: '2026-01-05T10:00:00Z',
    dataDevolucaoPrevista: '2026-01-19T10:00:00Z',
    dataDevolucaoReal: '2026-01-18T14:00:00Z',
    status: 'DEVOLVIDO',
    numeroRenovacoes: 0,
    multa: 0,
  },
];

// ===== RESERVAS MOCK =====
export const MOCK_RESERVAS: any[] = [
  {
    id: 'res-001',
    livro: { id: '2', titulo: 'Introduction to Algorithms', autor: 'Thomas H. Cormen' },
    usuario: { id: 'mock-student-002', nome: 'Maria Santos', matricula: '20230002' },
    dataReserva: '2026-02-01T10:00:00Z',
    posicaoFila: 1,
    status: 'PENDENTE',
  },
  {
    id: 'res-002',
    livro: { id: '1', titulo: 'Clean Code', autor: 'Robert C. Martin' },
    usuario: { id: 'mock-student-004', nome: 'Ana Costa', matricula: '20230004' },
    dataReserva: '2026-02-05T10:00:00Z',
    posicaoFila: 1,
    status: 'PENDENTE',
  },
];

// ===== CATEGORIAS MOCK =====
export const MOCK_CATEGORIAS: Categoria[] = [
  { id: 'cat-01', nome: 'Técnico', descricao: 'Livros técnicos e de tecnologia', dataCadastro: '2024-01-01T10:00:00Z', totalLivros: 12 },
  { id: 'cat-02', nome: 'Literatura', descricao: 'Obras literárias e romances', dataCadastro: '2024-01-01T10:00:00Z', totalLivros: 8 },
  { id: 'cat-03', nome: 'Ciências', descricao: 'Livros de ciências naturais e exatas', dataCadastro: '2024-01-01T10:00:00Z', totalLivros: 5 },
  { id: 'cat-04', nome: 'História', descricao: 'Livros sobre história e sociedade', dataCadastro: '2024-01-01T10:00:00Z', totalLivros: 3 },
  { id: 'cat-05', nome: 'Filosofia', descricao: 'Obras de filosofia e pensamento', dataCadastro: '2024-01-01T10:00:00Z', totalLivros: 2 },
];

// ===== AUTORES MOCK =====
export const MOCK_AUTORES: Autor[] = [
  { id: 'aut-01', nome: 'Robert C. Martin', nacionalidade: 'Americano', bio: 'Autor consagrado na área de engenharia de software.', dataCadastro: '2024-01-10T10:00:00Z', totalLivros: 3 },
  { id: 'aut-02', nome: 'Thomas H. Cormen', nacionalidade: 'Americano', bio: 'Professor de Ciência da Computação em Dartmouth.', dataCadastro: '2024-01-10T10:00:00Z', totalLivros: 1 },
  { id: 'aut-03', nome: 'Pepetela', nacionalidade: 'Angolano', bio: 'Escritor angolano premiado, autor de Mayombe.', dataCadastro: '2024-01-10T10:00:00Z', totalLivros: 2 },
  { id: 'aut-04', nome: 'Eric Freeman', nacionalidade: 'Americano', bio: 'Co-autor de Head First Design Patterns.', dataCadastro: '2024-01-10T10:00:00Z', totalLivros: 1 },
];

// ===== PAGAMENTOS MOCK =====
export const MOCK_PAGAMENTOS: Pagamento[] = [
  {
    id: 'pag-001',
    usuarioId: 'mock-student-004',
    usuarioNome: 'Ana Costa',
    valor: 150,
    motivo: 'Multa por atraso - Head First Design Patterns',
    status: StatusPagamento.PENDENTE,
    dataCriacao: '2026-01-20T10:00:00Z',
  },
  {
    id: 'pag-002',
    usuarioId: 'mock-student-005',
    usuarioNome: 'Pedro Alves',
    valor: 500,
    motivo: 'Multa por atraso - Múltiplos livros',
    status: StatusPagamento.PENDENTE,
    dataCriacao: '2025-12-15T10:00:00Z',
  },
  {
    id: 'pag-003',
    usuarioId: 'mock-prof-003',
    usuarioNome: 'Dr. Carlos Mendes',
    valor: 200,
    motivo: 'Multa por atraso - Introduction to Algorithms',
    status: StatusPagamento.PAGO,
    dataPagamento: '2026-01-28T14:00:00Z',
    dataCriacao: '2026-01-25T10:00:00Z',
    referencia: 'REC-2026-003',
  },
];

// ===== ESTATÍSTICAS MOCK =====
export const MOCK_DASHBOARD_STATS = {
  totalLivros: 500,
  totalUsuarios: 120,
  emprestimosAtivos: 15,
  reservasPendentes: 12,
};

export const MOCK_ACOES_PENDENTES = [
  {
    id: '1',
    tipo: 'Devolução Atrasada',
    descricao: 'Clean Code - João Silva',
    dias: 4,
  },
  {
    id: '2',
    tipo: 'Reserva Disponível',
    descricao: 'Algorithms - Maria Santos',
    dias: 2,
  },
  {
    id: '3',
    tipo: 'Multa Pendente',
    descricao: 'Ana Costa - 150 Kz',
    dias: 10,
  },
];

export const MOCK_LIVROS_POPULARES = [
  { titulo: 'Clean Code', emprestimos: 45 },
  { titulo: 'Introduction to Algorithms', emprestimos: 38 },
  { titulo: 'Design Patterns', emprestimos: 32 },
  { titulo: 'Head First Design Patterns', emprestimos: 28 },
  { titulo: 'Mayombe', emprestimos: 25 },
];
