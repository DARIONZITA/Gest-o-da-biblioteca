export enum StatusReserva {
  ATIVA = 'ATIVA',
  CONCLUIDA = 'CONCLUIDA',
  CANCELADA = 'CANCELADA',
}

/** Corresponde ao ReservaResponseDTO do backend */
export interface Reserva {
  id: string;
  nomeUsuario: string;
  tituloLivro: string;
  posicaoFila: number;
  status: StatusReserva;
  dataReserva: string;
}

export interface ReservaRequestData {
  usuarioId: string;
  livroId: string;
}
