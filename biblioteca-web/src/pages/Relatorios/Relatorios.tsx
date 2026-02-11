import React, { useState, useEffect } from 'react';
import AdminLayout from '@/layouts/AdminLayout';
import Card from '@/components/Card';
import { BarChart3, BookOpen, Users, TrendingUp, TrendingDown, Calendar, Download, BookMarked } from 'lucide-react';
import Button from '@/components/Button';
import { toast } from '@/utils/toast';
import { relatoriosService, DashboardData } from '@/services/relatoriosService';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

const Relatorios: React.FC = () => {
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [periodo, setPeriodo] = useState<'mes' | 'trimestre' | 'ano'>('mes');

  useEffect(() => {
    const loadData = async () => {
      try {
        const dashboardData = await relatoriosService.getDashboardData();
        setData(dashboardData);
      } catch (error: any) {
        toast.error('Erro ao carregar dados dos relatórios');
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  const handleExport = () => {
    if (!data) return;

    const doc = new jsPDF();

    // Título
    doc.setFontSize(20);
    doc.text('Relatório de Biblioteca', 14, 22);
    doc.setFontSize(10);
    doc.text(`Gerado em: ${new Date().toLocaleDateString()} às ${new Date().toLocaleTimeString()}`, 14, 30);

    // Resumo Geral
    doc.setFontSize(14);
    doc.text('Resumo Geral', 14, 45);

    const summaryData = [
      ['Total de Livros', data.livrosTotal],
      ['Livros Disponíveis', data.livrosDisponiveis],
      ['Empréstimos Ativos', data.emprestimosAtivos],
      ['Empréstimos Atrasados', data.emprestimosAtrasados],
      ['Membros Ativos', data.membrosAtivos],
      ['Multas Pendentes', `${data.multasTotalPendentes} Kz`]
    ];

    autoTable(doc, {
      startY: 50,
      head: [['Métrica', 'Valor']],
      body: summaryData,
      theme: 'grid',
      headStyles: { fillColor: [63, 81, 181] }
    });

    // Categorias Populares
    let finalY = (doc as any).lastAutoTable.finalY + 15;
    doc.text('Categorias Mais Populares', 14, finalY);

    const categoriasData = data.categoriasMaisPopulares.map(c => [c.nome, c.total, `${c.pct}%`]);

    autoTable(doc, {
      startY: finalY + 5,
      head: [['Categoria', 'Total Livros', '% do Acervo']],
      body: categoriasData,
      theme: 'striped'
    });

    // Livros Mais Emprestados
    finalY = (doc as any).lastAutoTable.finalY + 15;
    doc.text('Livros Mais Emprestados (Top 5)', 14, finalY);

    const livrosData = data.livrosMaisEmprestados.map(l => [l.titulo, l.emprestimos]);

    autoTable(doc, {
      startY: finalY + 5,
      head: [['Livro', 'Qtd. Empréstimos']],
      body: livrosData,
      theme: 'striped'
    });

    doc.save('relatorio-biblioteca.pdf');
    toast.success('PDF gerado com sucesso!');
  };

  if (loading) {
    return (
      <AdminLayout>
        <div className="flex items-center justify-center h-96">
          <div className="text-center">
            <div className="w-12 h-12 border-[3px] border-gray-200 border-t-primary-600 rounded-full animate-spin mx-auto" />
            <p className="mt-4 text-sm text-gray-400 font-medium">Gerando relatórios...</p>
          </div>
        </div>
      </AdminLayout>
    );
  }

  if (!data) return null;

  // Calculo simples para grafico de barras (mockado no backend por enquanto)
  const maxEmprestimos = data.emprestimosPoMes.length > 0
    ? Math.max(...data.emprestimosPoMes.map(e => e.qtd))
    : 1;

  return (
    <AdminLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between animate-slide-up">
          <div>
            <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Relatórios</h1>
            <p className="text-sm text-gray-400 mt-0.5 font-medium">Estatísticas e análises do sistema</p>
          </div>
          <Button variant="outline" onClick={handleExport}>
            <Download className="w-4 h-4" />
            Exportar PDF
          </Button>
        </div>

        {/* Período */}
        <div className="flex items-center gap-2 animate-slide-up stagger-1">
          <Calendar className="w-4 h-4 text-gray-400" />
          <div className="flex bg-gray-100 rounded-lg p-0.5">
            {(['mes', 'trimestre', 'ano'] as const).map((p) => (
              <button
                key={p}
                onClick={() => setPeriodo(p)}
                className={`px-4 py-1.5 rounded-md text-xs font-semibold transition-all duration-200 ${periodo === p ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'
                  }`}
              >
                {p === 'mes' ? 'Este Mês' : p === 'trimestre' ? 'Trimestre' : 'Este Ano'}
              </button>
            ))}
          </div>
        </div>

        {/* Summary Stats */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 animate-slide-up stagger-2">
          <Card padding="lg">
            <div className="flex items-center justify-between mb-2">
              <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400">Acervo Total</p>
              <div className="p-2 rounded-lg bg-primary-50"><BookOpen className="w-4 h-4 text-primary-600" /></div>
            </div>
            <p className="text-2xl font-extrabold text-gray-900">{data.livrosTotal}</p>
            <p className="text-xs text-gray-400 mt-1">
              <span className="text-emerald-600 font-semibold">{data.livrosDisponiveis}</span> disponíveis
            </p>
          </Card>

          <Card padding="lg">
            <div className="flex items-center justify-between mb-2">
              <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400">Membros</p>
              <div className="p-2 rounded-lg bg-emerald-50"><Users className="w-4 h-4 text-emerald-600" /></div>
            </div>
            <p className="text-2xl font-extrabold text-gray-900">{data.membrosTotal}</p>
            <p className="text-xs text-gray-400 mt-1">
              <span className="text-red-500 font-semibold">{data.membrosBloqueados}</span> bloqueados
            </p>
          </Card>

          <Card padding="lg">
            <div className="flex items-center justify-between mb-2">
              <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400">Empréstimos Ativos</p>
              <div className="p-2 rounded-lg bg-blue-50"><BookMarked className="w-4 h-4 text-blue-600" /></div>
            </div>
            <p className="text-2xl font-extrabold text-gray-900">{data.emprestimosAtivos}</p>
            <p className="text-xs text-gray-400 mt-1">
              <span className="text-red-500 font-semibold">{data.emprestimosAtrasados}</span> atrasados
            </p>
          </Card>

          <Card padding="lg">
            <div className="flex items-center justify-between mb-2">
              <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400">Multas Pendentes</p>
              <div className="p-2 rounded-lg bg-amber-50"><TrendingDown className="w-4 h-4 text-amber-600" /></div>
            </div>
            <p className="text-2xl font-extrabold text-red-600">{data.multasTotalPendentes} Kz</p>
            <p className="text-xs text-gray-400 mt-1">
              <span className="text-emerald-600 font-semibold">{data.multasTotalPagas} Kz</span> recebidos
            </p>
          </Card>
        </div>

        {/* Charts Row */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Empréstimos por Mês - Bar Chart */}
          <Card padding="lg" className="animate-slide-up stagger-3">
            <div className="flex items-center justify-between mb-6">
              <div>
                <h2 className="text-base font-bold text-gray-900">Empréstimos por Mês</h2>
                <p className="text-xs text-gray-400 mt-0.5">Visão anual (Simulado)</p>
              </div>
              <div className="p-2 rounded-lg bg-emerald-50">
                <TrendingUp className="w-4 h-4 text-emerald-600" />
              </div>
            </div>
            {data.emprestimosPoMes.length > 0 ? (
              <div className="flex items-end gap-3 h-48">
                {data.emprestimosPoMes.map((item, i) => (
                  <div key={item.mes || i} className="flex-1 flex flex-col items-center gap-2">
                    <span className="text-xs font-bold text-gray-900">{item.qtd}</span>
                    <div
                      className="w-full rounded-lg transition-all duration-500 ease-out"
                      style={{
                        height: `${(item.qtd / maxEmprestimos) * 100}%`,
                        minHeight: '8px',
                        background: i === data.emprestimosPoMes.length - 1
                          ? 'linear-gradient(to top, #1E3A8A, #3B82F6)'
                          : 'linear-gradient(to top, #E5E7EB, #D1D5DB)',
                      }}
                    />
                    <span className="text-[10px] font-bold text-gray-400 uppercase">{item.mes}</span>
                  </div>
                ))}
              </div>
            ) : (
              <div className="flex items-center justify-center h-48 text-gray-400 text-sm">
                Sem dados históricos disponíveis
              </div>
            )}
          </Card>

          {/* Categorias Populares */}
          <Card padding="lg" className="animate-slide-up stagger-3">
            <div className="flex items-center justify-between mb-6">
              <div>
                <h2 className="text-base font-bold text-gray-900">Categorias Populares</h2>
                <p className="text-xs text-gray-400 mt-0.5">Distribuição do acervo</p>
              </div>
              <div className="p-2 rounded-lg bg-primary-50">
                <BarChart3 className="w-4 h-4 text-primary-600" />
              </div>
            </div>
            <div className="space-y-4">
              {data.categoriasMaisPopulares.map((cat, i) => {
                const colors = ['bg-primary-600', 'bg-blue-500', 'bg-emerald-500', 'bg-amber-500', 'bg-purple-500'];
                return (
                  <div key={cat.nome}>
                    <div className="flex items-center justify-between mb-1.5">
                      <span className="text-sm font-semibold text-gray-700">{cat.nome}</span>
                      <span className="text-xs font-bold text-gray-500">{cat.total} livros ({Math.round(cat.pct)}%)</span>
                    </div>
                    <div className="w-full h-2.5 bg-gray-100 rounded-full overflow-hidden">
                      <div
                        className={`h-full rounded-full ${colors[i % colors.length]} transition-all duration-700 ease-out`}
                        style={{ width: `${Math.max(cat.pct, 5)}%` }}
                      />
                    </div>
                  </div>
                );
              })}
            </div>
          </Card>
        </div>

        {/* Top Livros */}
        <Card padding="lg" className="animate-slide-up stagger-4">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-base font-bold text-gray-900">Top 5 Livros Mais Emprestados</h2>
              <p className="text-xs text-gray-400 mt-0.5">Ranking por número de empréstimos</p>
            </div>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-100">
                  <th className="px-4 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">Posição</th>
                  <th className="px-4 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">Livro</th>
                  <th className="px-4 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">Empréstimos</th>
                  <th className="px-4 py-3 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">Gráfico</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {data.livrosMaisEmprestados.map((livro, index) => {
                  const maxBar = data.livrosMaisEmprestados[0].emprestimos;
                  return (
                    <tr key={livro.titulo} className="hover:bg-gray-50/80 transition-colors">
                      <td className="px-4 py-3">
                        <span className={`flex items-center justify-center w-8 h-8 rounded-lg text-sm font-bold ${index === 0 ? 'bg-primary-800 text-white' :
                            index === 1 ? 'bg-primary-100 text-primary-800' :
                              'bg-gray-100 text-gray-600'
                          }`}>
                          {index + 1}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-sm font-semibold text-gray-900">{livro.titulo}</td>
                      <td className="px-4 py-3 text-sm font-bold text-gray-700 tabular-nums">{livro.emprestimos}</td>
                      <td className="px-4 py-3 w-64">
                        <div className="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
                          <div
                            className="h-full rounded-full bg-primary-600 transition-all duration-700"
                            style={{ width: `${(livro.emprestimos / maxBar) * 100}%` }}
                          />
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </Card>
      </div>
    </AdminLayout>
  );
};

export default Relatorios;
