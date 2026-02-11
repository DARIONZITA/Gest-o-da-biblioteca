import React, { useState, useEffect } from 'react';
import MemberLayout from '@/layouts/MemberLayout';
import Card from '@/components/Card';
import Button from '@/components/Button';
import Badge from '@/components/Badge';
import Input from '@/components/Input';
import EmptyState from '@/components/EmptyState';
import { Bookmark, Search, X, Clock } from 'lucide-react';
import { Reserva, StatusReserva } from '@/types/reserva';
import { reservasService } from '@/services/reservasService';
import { toast } from '@/utils/toast';
import { RESERVATION_EXPIRY_HOURS } from '@/constants';

type FilterStatus = 'TODOS' | 'ATIVA' | 'CONCLUIDA' | 'CANCELADA';

const MinhasReservas: React.FC = () => {
  const [reservas, setReservas] = useState<Reserva[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState<FilterStatus>('TODOS');

  const fetchReservas = async () => {
    try {
      setLoading(true);
      const data = await reservasService.getMinhasReservas();
      setReservas(data);
    } catch (error: any) {
      toast.error(error.message || 'Erro ao carregar reservas');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReservas();
  }, []);

  const handleCancelar = async (id: string) => {
    try {
      await reservasService.cancelar(id);
      setReservas(prev => prev.map(r => r.id === id ? { ...r, status: StatusReserva.CANCELADA } : r));
      toast.success('Reserva cancelada com sucesso!');
    } catch (error: any) {
      toast.error(error.message || 'Erro ao cancelar reserva');
    }
  };

  const filtered = reservas.filter(r => {
    const matchesSearch = r.tituloLivro.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = filterStatus === 'TODOS' || r.status === filterStatus;
    return matchesSearch && matchesStatus;
  });

  const counts = {
    total: reservas.length,
    ativas: reservas.filter(r => r.status === StatusReserva.ATIVA).length,
    concluidas: reservas.filter(r => r.status === StatusReserva.CONCLUIDA).length,
    canceladas: reservas.filter(r => r.status === StatusReserva.CANCELADA).length,
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'ATIVA': return <Badge variant="warning" dot>Ativa</Badge>;
      case 'CONCLUIDA': return <Badge variant="success" dot>Concluída</Badge>;
      case 'CANCELADA': return <Badge variant="neutral" dot>Cancelada</Badge>;
      default: return <Badge variant="neutral">{status}</Badge>;
    }
  };

  return (
    <MemberLayout>
      <div className="space-y-6">
        <div className="animate-slide-up">
          <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Minhas Reservas</h1>
          <p className="text-sm text-gray-400 mt-0.5 font-medium">Acompanhe as suas reservas de livros</p>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 animate-slide-up stagger-1">
          <button onClick={() => setFilterStatus('TODOS')} className={`p-4 rounded-xl border text-left transition-all duration-200 ${filterStatus === 'TODOS' ? 'border-primary-300 bg-primary-50' : 'border-gray-100 bg-white hover:border-gray-200'}`}>
            <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400">Total</p>
            <p className="text-2xl font-extrabold text-gray-900 mt-1">{counts.total}</p>
          </button>
          <button onClick={() => setFilterStatus('ATIVA')} className={`p-4 rounded-xl border text-left transition-all duration-200 ${filterStatus === 'ATIVA' ? 'border-amber-300 bg-amber-50' : 'border-gray-100 bg-white hover:border-gray-200'}`}>
            <p className="text-[10px] font-bold uppercase tracking-wider text-amber-500">Ativas</p>
            <p className="text-2xl font-extrabold text-amber-700 mt-1">{counts.ativas}</p>
          </button>
          <button onClick={() => setFilterStatus('CONCLUIDA')} className={`p-4 rounded-xl border text-left transition-all duration-200 ${filterStatus === 'CONCLUIDA' ? 'border-emerald-300 bg-emerald-50' : 'border-gray-100 bg-white hover:border-gray-200'}`}>
            <p className="text-[10px] font-bold uppercase tracking-wider text-emerald-500">Concluídas</p>
            <p className="text-2xl font-extrabold text-emerald-700 mt-1">{counts.concluidas}</p>
          </button>
          <button onClick={() => setFilterStatus('CANCELADA')} className={`p-4 rounded-xl border text-left transition-all duration-200 ${filterStatus === 'CANCELADA' ? 'border-gray-300 bg-gray-50' : 'border-gray-100 bg-white hover:border-gray-200'}`}>
            <p className="text-[10px] font-bold uppercase tracking-wider text-gray-500">Canceladas</p>
            <p className="text-2xl font-extrabold text-gray-700 mt-1">{counts.canceladas}</p>
          </button>
        </div>

        <Card padding="md" className="animate-slide-up stagger-2">
          <Input
            placeholder="Buscar por título do livro..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            icon={<Search className="w-4 h-4" />}
          />
        </Card>

        <div className="flex items-center gap-2 px-1 animate-slide-up stagger-2">
          <Clock className="w-4 h-4 text-gray-400" />
          <p className="text-xs text-gray-400 font-medium">
            Reservas expiram automaticamente após <span className="font-bold text-gray-600">{RESERVATION_EXPIRY_HOURS}h</span> quando disponíveis
          </p>
        </div>

        <div className="space-y-3 animate-slide-up stagger-3">
          {loading ? (
            <Card padding="lg">
              <div className="flex items-center justify-center h-48">
                <div className="w-8 h-8 border-[3px] border-gray-200 border-t-primary-600 rounded-full animate-spin" />
              </div>
            </Card>
          ) : filtered.length > 0 ? (
            filtered.map((res) => (
              <Card key={res.id} padding="lg" hover className="group">
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-3 mb-2 flex-wrap">
                      <h3 className="text-sm font-bold text-gray-900">{res.tituloLivro}</h3>
                      {getStatusBadge(res.status)}
                      <span className="text-[10px] font-bold text-gray-400 bg-gray-100 px-2 py-0.5 rounded-md">
                        #{res.posicaoFila} na fila
                      </span>
                    </div>
                    <div className="grid grid-cols-2 gap-3 text-xs text-gray-500">
                      <div>
                        <span className="block text-[10px] font-bold text-gray-400 uppercase tracking-wider">Data da Reserva</span>
                        <span>{new Date(res.dataReserva).toLocaleDateString('pt-BR')}</span>
                      </div>
                    </div>
                  </div>
                  {res.status === StatusReserva.ATIVA && (
                    <div className="flex flex-col gap-2 shrink-0">
                      <Button variant="danger" size="sm" onClick={() => handleCancelar(res.id)}>
                        <X className="w-4 h-4" />
                        Cancelar
                      </Button>
                    </div>
                  )}
                </div>
              </Card>
            ))
          ) : (
            <Card padding="lg">
              <EmptyState
                icon={<Bookmark className="w-12 h-12" />}
                title="Nenhuma reserva encontrada"
                message={searchTerm ? 'Tente buscar com outros termos' : 'Você ainda não tem reservas'}
              />
            </Card>
          )}
        </div>

        {filtered.length > 0 && (
          <div className="flex items-center justify-between animate-fade-in">
            <p className="text-xs text-gray-400 font-medium">
              Mostrando {filtered.length} de {reservas.length} reservas
            </p>
          </div>
        )}
      </div>
    </MemberLayout>
  );
};

export default MinhasReservas;
