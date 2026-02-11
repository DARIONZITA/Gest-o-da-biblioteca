export enum StatusPagamento {
  PENDENTE = 'PENDENTE',
  PAGO = 'PAGO',
  CANCELADO = 'CANCELADO',
}

export interface Pagamento {
  id: string;
  usuarioId: string;
  usuarioNome: string;
  valor: number;
  motivo: string;
  status: StatusPagamento;
  dataPagamento?: string;
  dataCriacao: string;
  referencia?: string;
}

export interface PagamentoFormData {
  usuarioId: string;
  valor: number;
  referencia?: string;
}
