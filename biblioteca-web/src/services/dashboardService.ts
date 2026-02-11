import api from './api';
import { Livro } from '@/types/livro';
import { Usuario } from '@/types/usuario';
import { Emprestimo } from '@/types/emprestimo';
import { Reserva } from '@/types/reserva';

interface EstatisticasDashboard {
  totalLivros: number;
  totalUsuarios: number;
  emprestimosAtivos: number;
  emprestimosPendentes: number;
  reservasPendentes: number;
}

interface AcaoPendente {
  id: string;
  tipo: string;
  descricao: string;
}

interface LivroPopular {
  titulo: string;
  emprestimos: number;
}

export const dashboardService = {
  /** Agrega estatísticas a partir dos endpoints reais */
  async getEstatisticas(): Promise<EstatisticasDashboard> {
    try {
      const [livrosRes, usuariosRes, emprestimosRes, reservasRes] = await Promise.all([
        api.get<Livro[]>('/livros').catch(err => { console.error('Erro ao buscar livros:', err); throw err; }),
        api.get<Usuario[]>('/usuarios').catch(err => { console.error('Erro ao buscar usuários:', err); throw err; }),
        api.get<Emprestimo[]>('/emprestimos').catch(err => { console.error('Erro ao buscar empréstimos:', err); throw err; }),
        api.get<Reserva[]>('/reservas').catch(err => { console.error('Erro ao buscar reservas:', err); throw err; }),
      ]);

      const emprestimos = emprestimosRes.data;
      const reservas = reservasRes.data;

      return {
        totalLivros: livrosRes.data.length,
        totalUsuarios: usuariosRes.data.length,
        emprestimosAtivos: emprestimos.filter(e => e.status === 'ATIVO' || e.status === 'ATRASADO').length,
        emprestimosPendentes: emprestimos.filter(e => e.status === 'PENDENTE').length,
        reservasPendentes: reservas.filter(r => r.status === 'ATIVA').length,
      };
    } catch (error) {
      console.error('Erro em getEstatisticas:', error);
      throw error;
    }
  },

  async getAcoesPendentes(): Promise<AcaoPendente[]> {
    try {
      const response = await api.get<Emprestimo[]>('/emprestimos');
      const acoes: AcaoPendente[] = [];
      
      const pendentes = response.data.filter(e => e.status === 'PENDENTE');
      const atrasados = response.data.filter(e => e.status === 'ATRASADO');
      
      pendentes.forEach(e => {
        acoes.push({
          id: e.id,
          tipo: 'EMPRESTIMO_PENDENTE',
          descricao: `${e.nomeUsuario} aguarda aprovação para "${e.tituloLivro}"`,
        });
      });
      
      atrasados.forEach(e => {
        acoes.push({
          id: e.id,
          tipo: 'DEVOLUCAO_ATRASADA',
          descricao: `${e.nomeUsuario} — "${e.tituloLivro}" (multa: ${e.valorMulta} Kz)`,
        });
      });
      
      return acoes;
    } catch (error) {
      console.error('Erro em getAcoesPendentes:', error);
      return [];
    }
  },

  async getLivrosPopulares(): Promise<LivroPopular[]> {
    try {
      const response = await api.get<Livro[]>('/livros');
      return response.data.slice(0, 5).map(l => ({
        titulo: l.titulo,
        emprestimos: 0,
      }));
    } catch (error) {
      console.error('Erro em getLivrosPopulares:', error);
      return [];
    }
  },
};
