import api from './api';
import { Reserva, ReservaRequestData } from '@/types/reserva';

export const reservasService = {
  async getReservas(): Promise<Reserva[]> {
    const response = await api.get<Reserva[]>('/reservas');
    return response.data;
  },

  async createReserva(data: ReservaRequestData): Promise<Reserva> {
    const response = await api.post<Reserva>('/reservas', data);
    return response.data;
  },

  async cancelar(id: string): Promise<Reserva> {
    const response = await api.patch<Reserva>(`/reservas/${id}/cancelar`);
    return response.data;
  },

  async getMinhasReservas(): Promise<Reserva[]> {
    const response = await api.get<Reserva[]>('/reservas/minhas');
    return response.data;
  },
};
