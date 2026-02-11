import React, { useState, useEffect } from 'react';
import { Search, Filter, BookOpen, Bookmark, Eye } from 'lucide-react';
import MemberLayout from '@/layouts/MemberLayout';
import Input from '@/components/Input';
import Card from '@/components/Card';
import Badge from '@/components/Badge';
import Button from '@/components/Button';
import EmptyState from '@/components/EmptyState';
import LivroDetalhesModal from '@/components/LivroDetalhesModal';
import { Livro } from '@/types/livro';
import { livrosService } from '@/services/livrosService';
import { emprestimosService } from '@/services/emprestimosService';
import { reservasService } from '@/services/reservasService';
import { useAuth } from '@/hooks/useAuth';
import { toast } from '@/utils/toast';
import { API_URL } from '@/constants';

const Catalogo: React.FC = () => {
  const { user } = useAuth();
  const [livros, setLivros] = useState<Livro[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [reservando, setReservando] = useState<string | null>(null);
  const [solicitando, setSolicitando] = useState<string | null>(null);
  const [livroSelecionado, setLivroSelecionado] = useState<Livro | null>(null);
  const [modalAberto, setModalAberto] = useState(false);

  useEffect(() => {
    const loadLivros = async () => {
      try {
        setLoading(true);
        const data = await livrosService.getLivros();
        setLivros(data);
      } catch (error: any) {
        toast.error('Erro ao carregar catálogo');
      } finally {
        setLoading(false);
      }
    };
    loadLivros();
  }, []);

  const handleReservar = async (livro: Livro) => {
    if (!user) return;
    try {
      setReservando(livro.id);
      await reservasService.createReserva({
        usuarioId: user.id,
        livroId: livro.id,
      });
      toast.success(`Reserva criada para "${livro.titulo}"!`);
      setModalAberto(false);
    } catch (error: any) {
      toast.error(error.message || 'Erro ao criar reserva');
    } finally {
      setReservando(null);
    }
  };

  const handleSolicitarEmprestimo = async (livro: Livro) => {
    if (!user) return;
    try {
      setSolicitando(livro.id);
      await emprestimosService.createRascunho({
        livroId: livro.id,
      });
      toast.success(`Solicitação enviada para "${livro.titulo}"!`);
      setModalAberto(false);
    } catch (error: any) {
      toast.error(error.message || 'Erro ao solicitar empréstimo');
    } finally {
      setSolicitando(null);
    }
  };

  const abrirDetalhes = (livro: Livro) => {
    setLivroSelecionado(livro);
    setModalAberto(true);
  };

  const filteredLivros = livros.filter(l =>
    !searchTerm ||
    l.titulo.toLowerCase().includes(searchTerm.toLowerCase()) ||
    l.nomeAutor.toLowerCase().includes(searchTerm.toLowerCase()) ||
    l.isbn.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <MemberLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="animate-slide-up">
          <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Catálogo</h1>
          <p className="text-sm text-gray-400 mt-0.5 font-medium">Explore o acervo da biblioteca</p>
        </div>

        {/* Search */}
        <Card padding="md" className="animate-slide-up stagger-1">
          <div className="flex gap-3">
            <div className="flex-1">
              <Input
                placeholder="Buscar por título, autor ou ISBN..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                icon={<Search className="w-4 h-4" />}
              />
            </div>
            <Button variant="outline">
              <Filter className="w-4 h-4" />
              Filtros
            </Button>
          </div>
        </Card>

        {/* Grid de livros */}
        {loading ? (
          <div className="flex items-center justify-center h-64">
            <div className="w-8 h-8 border-[3px] border-gray-200 border-t-primary-600 rounded-full animate-spin" />
          </div>
        ) : filteredLivros.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 animate-slide-up stagger-2">
            {filteredLivros.map((livro) => (
              <Card key={livro.id} padding="lg" hover className="flex flex-col justify-between">
                <div>
                  {/* Capa do livro */}
                  {livro.capaURL ? (
                    <div className="mb-4 rounded-lg overflow-hidden border border-gray-200">
                      <img 
                        src={new URL(livro.capaURL, API_URL).toString()} 
                        alt={`Capa de ${livro.titulo}`}
                        className="w-full h-48 object-cover"
                        onError={(e) => {
                          e.currentTarget.style.display = 'none';
                          e.currentTarget.parentElement?.classList.add('hidden');
                        }}
                      />
                    </div>
                  ) : (
                    <div className="mb-4 flex items-center justify-center h-48 bg-gray-100 rounded-lg border border-gray-200">
                      <BookOpen className="w-12 h-12 text-gray-300" />
                    </div>
                  )}
                  
                  <div className="flex items-start justify-between gap-2 mb-3">
                    <div className="flex-1 min-w-0">
                      <h3 className="text-sm font-bold text-gray-900 truncate">{livro.titulo}</h3>
                      <p className="text-xs text-gray-400 mt-0.5">{livro.nomeAutor}</p>
                    </div>
                    <Badge variant={livro.qtdDisponivel > 0 ? 'success' : 'error'} dot>
                      {livro.qtdDisponivel > 0 ? `${livro.qtdDisponivel} disp.` : 'Indisponível'}
                    </Badge>
                  </div>
                  <div className="flex items-center gap-2 mb-3">
                    <Badge variant="neutral">{livro.nomeCategoria}</Badge>
                    <span className="text-[10px] text-gray-400 font-mono">{livro.isbn}</span>
                  </div>
                </div>
                <div className="space-y-2">
                  <Button
                    variant="outline"
                    size="sm"
                    className="w-full"
                    onClick={() => abrirDetalhes(livro)}
                  >
                    <Eye className="w-4 h-4" />
                    Ver Detalhes
                  </Button>
                  {livro.qtdDisponivel > 0 ? (
                    <Button
                      variant="primary"
                      size="sm"
                      className="w-full"
                      onClick={() => handleSolicitarEmprestimo(livro)}
                      disabled={solicitando === livro.id}
                    >
                      <BookOpen className="w-4 h-4" />
                      {solicitando === livro.id ? 'Solicitando...' : 'Solicitar empréstimo'}
                    </Button>
                  ) : (
                    <Button
                      variant="outline"
                      size="sm"
                      className="w-full"
                      onClick={() => handleReservar(livro)}
                      disabled={reservando === livro.id}
                    >
                      <Bookmark className="w-4 h-4" />
                      {reservando === livro.id ? 'Reservando...' : 'Reservar'}
                    </Button>
                  )}
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <Card padding="lg">
            <EmptyState
              icon={<BookOpen className="w-12 h-12" />}
              title="Nenhum livro encontrado"
              message={searchTerm ? 'Tente buscar com outros termos' : 'O acervo está vazio'}
            />
          </Card>
        )}

        {filteredLivros.length > 0 && (
          <div className="flex items-center justify-between animate-fade-in">
            <p className="text-xs text-gray-400 font-medium">
              Mostrando {filteredLivros.length} de {livros.length} livros
            </p>
          </div>
        )}
      </div>

      {/* Modal de Detalhes do Livro */}
      {livroSelecionado && (
        <LivroDetalhesModal
          livro={livroSelecionado}
          isOpen={modalAberto}
          onClose={() => setModalAberto(false)}
          onSolicitarEmprestimo={() => handleSolicitarEmprestimo(livroSelecionado)}
          onReservar={() => handleReservar(livroSelecionado)}
          loadingSolicitar={solicitando === livroSelecionado.id}
          loadingReservar={reservando === livroSelecionado.id}
        />
      )}
    </MemberLayout>
  );
};

export default Catalogo;
