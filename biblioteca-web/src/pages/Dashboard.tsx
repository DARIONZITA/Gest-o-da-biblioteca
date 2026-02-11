import React, { useEffect, useState } from 'react';
import AdminLayout from '@/layouts/AdminLayout';
import StatCard from '@/components/StatCard';
import Card from '@/components/Card';
import EmptyState from '@/components/EmptyState';
import Button from '@/components/Button';
import { BookOpen, Users, BookMarked, Bookmark, TrendingUp, Plus, BarChart3, Clock } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '@/constants';
import { dashboardService } from '@/services/dashboardService';
import { toast } from '@/utils/toast';

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalLivros: 0,
    totalUsuarios: 0,
    emprestimosAtivos: 0,
    emprestimosPendentes: 0,
    reservasPendentes: 0,
  });
  const [acoesPendentes, setAcoesPendentes] = useState<any[]>([]);
  const [livrosMaisEmprestados, setLivrosMaisEmprestados] = useState<any[]>([]);

  // Carregar dados do dashboard
  useEffect(() => {
    const loadDashboard = async () => {
      try {
        setLoading(true);
        const [estatisticas, acoes, livrosPopulares] = await Promise.all([
          dashboardService.getEstatisticas(),
          dashboardService.getAcoesPendentes(),
          dashboardService.getLivrosPopulares(),
        ]);
        
        setStats(estatisticas);
        setAcoesPendentes(acoes);
        setLivrosMaisEmprestados(livrosPopulares);
      } catch (error: any) {
        console.error('Erro ao carregar dashboard:', error);
        const errorMsg = error?.response?.data?.mensagem || error?.message || 'Erro desconhecido';
        toast.error(`Erro ao carregar dashboard: ${errorMsg}`);
        
        // Define valores padrão para evitar quebra da UI
        setStats({
          totalLivros: 0,
          totalUsuarios: 0,
          emprestimosAtivos: 0,
          emprestimosPendentes: 0,
          reservasPendentes: 0,
        });
        setAcoesPendentes([]);
        setLivrosMaisEmprestados([]);
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

  if (loading) {
    return (
      <AdminLayout>
        <div className="flex items-center justify-center h-96">
          <div className="text-center">
            <div className="w-12 h-12 border-[3px] border-gray-200 border-t-primary-600 rounded-full animate-spin mx-auto"></div>
            <p className="mt-4 text-sm text-gray-400 font-medium">Carregando dashboard...</p>
          </div>
        </div>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout>
      <div className="space-y-8">
        {/* Header */}
        <div className="flex items-center justify-between animate-slide-up">
          <div>
            <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Dashboard</h1>
            <p className="text-sm text-gray-400 mt-0.5 font-medium">Visão geral do sistema</p>
          </div>
          <Button
            variant="primary"
            onClick={() => navigate(ROUTES.ADMIN_LIVROS_NOVO)}
          >
            <Plus className="w-4 h-4" />
            Novo Livro
          </Button>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-5">
          <div className="animate-slide-up stagger-1">
            <StatCard
              title="Total de Livros"
              value={stats.totalLivros}
              icon={BookOpen}
              color="primary"
            />
          </div>
          <div className="animate-slide-up stagger-2">
            <StatCard
              title="Usuários Ativos"
              value={stats.totalUsuarios}
              icon={Users}
              color="success"
            />
          </div>
          <div className="animate-slide-up stagger-3">
            <StatCard
              title="Empréstimos Ativos"
              value={stats.emprestimosAtivos}
              icon={BookMarked}
              color="secondary"
            />
          </div>
          <div className="animate-slide-up stagger-4">
            <StatCard
              title="Empréstimos Pendentes"
              value={stats.emprestimosPendentes}
              icon={Clock}
              color="warning"
            />
          </div>
          <div className="animate-slide-up stagger-5">
            <StatCard
              title="Reservas Pendentes"
              value={stats.reservasPendentes}
              icon={Bookmark}
              color="primary"
            />
          </div>
        </div>

        {/* Gráfico e Ações */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Gráfico Placeholder */}
          <Card className="lg:col-span-2" padding="lg">
            <div className="flex items-center justify-between mb-6">
              <div>
                <h2 className="text-base font-bold text-gray-900">
                  Empréstimos nos Últimos 30 Dias
                </h2>
                <p className="text-xs text-gray-400 mt-0.5">Evolução mensal</p>
              </div>
              <div className="p-2 rounded-lg bg-emerald-50">
                <TrendingUp className="w-4 h-4 text-emerald-600" />
              </div>
            </div>
            <div className="h-64 flex items-center justify-center bg-gray-50/80 rounded-xl border border-dashed border-gray-200">
              <div className="text-center">
                <BarChart3 className="w-8 h-8 text-gray-300 mx-auto mb-2" />
                <p className="text-sm text-gray-400 font-medium">Gráfico será implementado com Recharts</p>
              </div>
            </div>
          </Card>

          {/* Ações Necessárias */}
          <Card padding="lg">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-base font-bold text-gray-900">
                Ações Necessárias
              </h2>
              {acoesPendentes.length > 0 && (
                <span className="flex items-center justify-center w-6 h-6 rounded-full bg-red-50 text-red-600 text-xs font-bold">
                  {acoesPendentes.length}
                </span>
              )}
            </div>
            <div className="space-y-2.5">
              {acoesPendentes.length > 0 ? (
                acoesPendentes.map((acao) => (
                  <div
                    key={acao.id}
                    className="p-3.5 bg-gray-50/80 rounded-xl hover:bg-gray-100/80 cursor-pointer transition-all duration-200 group border border-transparent hover:border-gray-200"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-semibold text-gray-900">{acao.tipo}</p>
                        <p className="text-xs text-gray-500 mt-0.5 truncate">{acao.descricao}</p>
                      </div>
                      <span className="ml-3 px-2 py-1 rounded-md bg-amber-50 text-amber-700 text-[10px] font-bold">{acao.dias}d</span>
                    </div>
                  </div>
                ))
              ) : (
                <EmptyState
                  title="Tudo em dia!"
                  message="Nenhuma ação pendente no momento."
                />
              )}
            </div>
          </Card>
        </div>

        {/* Top 5 Livros */}
        <Card padding="lg">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-base font-bold text-gray-900">
                Top 5 Livros Mais Emprestados
              </h2>
              <p className="text-xs text-gray-400 mt-0.5">Ranking por número de empréstimos</p>
            </div>
          </div>
          
          {livrosMaisEmprestados.length > 0 ? (
            <div className="space-y-2.5">
              {livrosMaisEmprestados.map((livro, index) => (
                <div
                  key={livro.id}
                  className="flex items-center justify-between p-3.5 bg-gray-50/80 rounded-xl hover:bg-gray-100/80 cursor-pointer transition-all duration-200 group border border-transparent hover:border-gray-200"
                >
                  <div className="flex items-center gap-4">
                    <span className={`flex items-center justify-center w-9 h-9 rounded-xl text-sm font-bold ${
                      index === 0 ? 'bg-primary-800 text-white shadow-sm' :
                      index === 1 ? 'bg-primary-100 text-primary-800' :
                      'bg-gray-100 text-gray-600'
                    }`}>
                      {index + 1}
                    </span>
                    <span className="font-semibold text-sm text-gray-900 group-hover:text-primary-800 transition-colors">{livro.titulo}</span>
                  </div>
                  <span className="text-xs text-gray-500 font-semibold tabular-nums">{livro.emprestimos} empréstimos</span>
                </div>
              ))}
            </div>
          ) : (
            <EmptyState
              title="Nenhum empréstimo registrado"
              message="Os livros mais emprestados aparecerão aqui."
            />
          )}
        </Card>
      </div>
    </AdminLayout>
  );
};

export default Dashboard;
