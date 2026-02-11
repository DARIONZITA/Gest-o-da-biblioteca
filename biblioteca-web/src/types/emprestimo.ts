export enum StatusEmprestimo {
  PENDENTE = 'PENDENTE',
  ATIVO = 'ATIVO',
  ATRASADO = 'ATRASADO',
  DEVOLVIDO = 'DEVOLVIDO',
}

/** Corresponde ao EmprestimoResponseDTO do backend */
export interface Emprestimo {
  id: string;
  nomeUsuario: string;
  tituloLivro: string;
  qtdRenovacoes: number;
  status: StatusEmprestimo;
  valorMulta: number;
  dataEmprestimo: string | null;
  dataPrevista: string;
  dataDevolucaoReal?: string;
}

export interface EmprestimoRequestData {
  usuarioId: string;
  livroId: string;
  dataPrevista: string; // yyyy-MM-dd
}

export interface EmprestimoRascunhoRequestData {
  livroId: string;
  dataPrevista?: string; // yyyy-MM-dd
}
