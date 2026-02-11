import React, { useState, useEffect } from 'react';
import AdminLayout from '@/layouts/AdminLayout';
import Card from '@/components/Card';
import Button from '@/components/Button';
import Badge from '@/components/Badge';
import Input from '@/components/Input';
import EmptyState from '@/components/EmptyState';
import { BookMarked, Search, RefreshCw, CheckCircle, AlertTriangle } from 'lucide-react';
import { Emprestimo, StatusEmprestimo } from '@/types/emprestimo';
import { emprestimosService } from '@/services/emprestimosService';
import { toast } from '@/utils/toast';
import { FINE_PER_DAY, MAX_RENEWALS } from '@/constants';

type FilterStatus = 'TODOS' | 'PENDENTE' | 'ATIVO' | 'ATRASADO' | 'DEVOLVIDO';

const ListaEmprestimos: React.FC = () => {
  const [emprestimos, setEmprestimos] = useState<Emprestimo[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState<FilterStatus>('TODOS');

  const fetchEmprestimos = async () => {
    try {
      setLoading(true);
      const data = await emprestimosService.getEmprestimos();
      setEmprestimos(data);
    } catch (error: any) {
      toast.error(error.message || 'Erro ao carregar empréstimos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEmprestimos();
  }, []);

  const handleRenovar = async (id: string) => {
    try {
      const updated = await emprestimosService.renovar(id);
      setEmprestimos(prev => prev.map(e => e.id === id ? { ...e, ...updated } : e));
      toast.success('Empréstimo renovado com sucesso!');
    } catch (error: any) {
      toast.error(error.message || 'Erro ao renovar empréstimo');
    }
  };

  const handleDevolver = async (id: string) => {
    try {
      const updated = await emprestimosService.devolver(id);
      setEmprestimos(prev => prev.map(e => e.id === id ? { ...e, ...updated } : e));
      toast.success('Devolução registada com sucesso!');
    } catch (error: any) {
      toast.error(error.message || 'Erro ao registar devolução');
    }
  };

  const handleAprovar = async (id: string) => {
    try {
      const updated = await emprestimosService.aprovar(id);
      setEmprestimos(prev => prev.map(e => e.id === id ? { ...e, ...updated } : e));
      toast.success('Empréstimo aprovado com sucesso!');
    } catch (error: any) {
      toast.error(error.message || 'Erro ao aprovar empréstimo');
    }
  };

  const filtered = emprestimos.filter(e => {
    const matchesSearch =
      e.tituloLivro.toLowerCase().includes(searchTerm.toLowerCase()) ||
      e.nomeUsuario.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = filterStatus === 'TODOS' || e.status === filterStatus;
    return matchesSearch && matchesStatus;
  });

  const counts = {
    total: emprestimos.length,
    pendentes: emprestimos.filter(e => e.status === StatusEmprestimo.PENDENTE).length,
    ativos: emprestimos.filter(e => e.status === StatusEmprestimo.ATIVO).length,
    atrasados: emprestimos.filter(e => e.status === StatusEmprestimo.ATRASADO).length,
    devolvidos: emprestimos.filter(e => e.status === StatusEmprestimo.DEVOLVIDO).length,
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'PENDENTE': return <Badge variant="warning" dot>Pendente</Badge>;
      case 'ATIVO': return <Badge variant="info" dot>Activo</Badge>;
      case 'ATRASADO': return <Badge variant="error" dot>Atrasado</Badge>;
      case 'DEVOLVIDO': return <Badge variant="success" dot>Devolvido</Badge>;
      default: return <Badge variant="neutral">{status}</Badge>;
    }
  };

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="animate-slide-up">
          <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Empréstimos</h1>
          <p className="text-sm text-gray-400 mt-0.5 font-medium">Gerencie os empréstimos de livros</p>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-2 md:grid-cols-5 gap-4 animate-slide-up stagger-1">
          <button onClick={() => setFilterStatus('TODOS')} className={`p-4 rounded-xl border text-left transition-all duration-200 ${filterStatus === 'TODOS' ? 'border-primary-300 bg-primary-50' : 'border-gray-100 bg-white hover:border-gray-200'}`}>
            <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400">Total</p>
            <p className="text-2xl font-extrabold text-gray-900 mt-1">{counts.total}</p>
          </button>
          <button onClick={() => setFilterStatus('PENDENTE')} className={`p-4 rounded-xl border text-left transition-all duration-200 ${filterStatus === 'PENDENTE' ? 'border-amber-300 bg-amber-50' : 'border-gray-100 bg-white hover:border-gray-200'}`}>
            <p className="text-[10px] font-bold uppercase tracking-wider text-amber-500">Pendentes</p>
            <p className="text-2xl font-extrabold text-amber-700 mt-1">{counts.pendentes}</p>
          </button>
          <button onClick={() => setFilterStatus('ATIVO')} className={`p-4 rounded-xl border text-left transition-all duration-200 ${filterStatus === 'ATIVO' ? 'border-blue-300 bg-blue-50' : 'border-gray-100 bg-white hover:border-gray-200'}`}>
            <p className="text-[10px] font-bold uppercase tracking-wider text-blue-500">Activos</p>
            <p className="text-2xl font-extrabold text-blue-700 mt-1">{counts.ativos}</p>
          </button>
          <button onClick={() => setFilterStatus('ATRASADO')} className={`p-4 rounded-xl border text-left transition-all duration-200 ${filterStatus === 'ATRASADO' ? 'border-red-300 bg-red-50' : 'border-gray-100 bg-white hover:border-gray-200'}`}>
            <p className="text-[10px] font-bold uppercase tracking-wider text-red-500">Atrasados</p>
            <p className="text-2xl font-extrabold text-red-700 mt-1">{counts.atrasados}</p>
          </button>
          <button onClick={() => setFilterStatus('DEVOLVIDO')} className={`p-4 rounded-xl border text-left transition-all duration-200 ${filterStatus === 'DEVOLVIDO' ? 'border-emerald-300 bg-emerald-50' : 'border-gray-100 bg-white hover:border-gray-200'}`}>
            <p className="text-[10px] font-bold uppercase tracking-wider text-emerald-500">Devolvidos</p>
            <p className="text-2xl font-extrabold text-emerald-700 mt-1">{counts.devolvidos}</p>
          </button>
        </div>

        <Card padding="md" className="animate-slide-up stagger-2">
          <Input
            placeholder="Buscar por título do livro ou nome..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            icon={<Search className="w-4 h-4" />}
          />
        </Card>

        <div className="space-y-3 animate-slide-up stagger-3">
          {loading ? (
            <Card padding="lg">
              <div className="flex items-center justify-center h-48">
                <div className="w-8 h-8 border-[3px] border-gray-200 border-t-primary-600 rounded-full animate-spin" />
              </div>
            </Card>
          ) : filtered.length > 0 ? (
            filtered.map((emp) => (
              <Card key={emp.id} padding="lg" hover className="group">
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-3 mb-2 flex-wrap">
                      <h3 className="text-sm font-bold text-gray-900">{emp.tituloLivro}</h3>
                      {getStatusBadge(emp.status)}
                      {emp.qtdRenovacoes > 0 && (
                        <span className="text-[10px] font-bold text-gray-400 bg-gray-100 px-2 py-0.5 rounded-md">
                          {emp.qtdRenovacoes}/{MAX_RENEWALS} renovações
                        </span>
                      )}
                    </div>
                    <div className="grid grid-cols-2 md:grid-cols-3 gap-3 text-xs text-gray-500">
                      <div>
                        <span className="block text-[10px] font-bold text-gray-400 uppercase tracking-wider">Membro</span>
                        <span className="font-medium text-gray-700">{emp.nomeUsuario}</span>
                      </div>
                      <div>
                        <span className="block text-[10px] font-bold text-gray-400 uppercase tracking-wider">Empréstimo</span>
                        <span>{emp.dataEmprestimo ? new Date(emp.dataEmprestimo).toLocaleDateString('pt-BR') : '--'}</span>
                      </div>
                      <div>
                        <span className="block text-[10px] font-bold text-gray-400 uppercase tracking-wider">Devolução Prevista</span>
                        <span className={emp.status === 'ATRASADO' ? 'text-red-600 font-semibold' : ''}>
                          {new Date(emp.dataPrevista).toLocaleDateString('pt-BR')}
                        </span>
                      </div>
                    </div>
                    {emp.status === 'ATRASADO' && emp.valorMulta > 0 && (
                      <div className="mt-3 flex items-center gap-3 flex-wrap">
                        <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-red-50 text-red-700 text-xs font-bold">
                          <AlertTriangle className="w-3.5 h-3.5" />
                          Atrasado
                        </div>
                        <div className="inline-flex items-center gap-1 px-2.5 py-1 rounded-md bg-amber-50 text-amber-700 text-xs font-bold">
                          Multa: {emp.valorMulta} Kz
                        </div>
                      </div>
                    )}
                    {emp.status === 'DEVOLVIDO' && emp.dataDevolucaoReal && (
                      <div className="mt-3">
                        <span className="inline-flex items-center gap-1 text-xs text-emerald-600 font-medium">
                          <CheckCircle className="w-3.5 h-3.5" />
                          Devolvido em {new Date(emp.dataDevolucaoReal).toLocaleDateString('pt-BR')}
                        </span>
                      </div>
                    )}
                  </div>
                  {(emp.status === 'ATIVO' || emp.status === 'ATRASADO') && (
                    <div className="flex flex-col gap-2 shrink-0">
                      <Button variant="primary" size="sm" onClick={() => handleDevolver(emp.id)}>
                        <CheckCircle className="w-4 h-4" />
                        Devolver
                      </Button>
                      {emp.qtdRenovacoes < MAX_RENEWALS && emp.status === 'ATIVO' && (
                        <Button variant="outline" size="sm" onClick={() => handleRenovar(emp.id)}>
                          <RefreshCw className="w-4 h-4" />
                          Renovar
                        </Button>
                      )}
                    </div>
                  )}
                  {emp.status === 'PENDENTE' && (
                    <div className="flex flex-col gap-2 shrink-0">
                      <Button variant="primary" size="sm" onClick={() => handleAprovar(emp.id)}>
                        <CheckCircle className="w-4 h-4" />
                        Aprovar
                      </Button>
                    </div>
                  )}
                </div>
              </Card>
            ))
          ) : (
            <Card padding="lg">
              <EmptyState
                icon={<BookMarked className="w-12 h-12" />}
                title="Nenhum empréstimo encontrado"
                message={searchTerm ? 'Tente buscar com outros termos' : 'Não há empréstimos registados'}
              />
            </Card>
          )}
        </div>

        {filtered.length > 0 && (
          <div className="flex items-center justify-between animate-fade-in">
            <p className="text-xs text-gray-400 font-medium">
              Mostrando {filtered.length} de {emprestimos.length} empréstimos
            </p>
          </div>
        )}
      </div>
    </AdminLayout>
  );
};

export default ListaEmprestimos;
