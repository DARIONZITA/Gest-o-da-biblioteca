import api from './api';

export interface DashboardData {
    emprestimosTotal: number;
    emprestimosAtivos: number;
    emprestimosAtrasados: number;
    devolucoesMes: number;
    livrosTotal: number;
    livrosDisponiveis: number;
    livrosEmprestados: number;
    membrosTotal: number;
    membrosAtivos: number;
    membrosBloqueados: number;
    multasTotalPendentes: number;
    multasTotalPagas: number;
    emprestimosPoMes: { mes: string; qtd: number }[];
    categoriasMaisPopulares: { nome: string; total: number; pct: number }[];
    livrosMaisEmprestados: { titulo: string; emprestimos: number }[];
}

export const relatoriosService = {
    getDashboardData: async (): Promise<DashboardData> => {
        const response = await api.get<DashboardData>('/relatorios/dashboard');
        return response.data;
    },
};
