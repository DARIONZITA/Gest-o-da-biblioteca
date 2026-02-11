import React, { useState, useEffect } from 'react';
import { Sparkles, TrendingUp, Brain, BookHeart, Loader2, Eye } from 'lucide-react';
import MemberLayout from '@/layouts/MemberLayout';
import Card from '@/components/Card';
import Badge from '@/components/Badge';
import Button from '@/components/Button';
import EmptyState from '@/components/EmptyState';
import LivroDetalhesModal from '@/components/LivroDetalhesModal';
import { Recomendacao } from '@/types/recomendacao';
import { getRecomendacoes, getLivrosPopulares } from '@/services/recomendacoesService';
import { reservasService } from '@/services/reservasService';
import { emprestimosService } from '@/services/emprestimosService';
import { useAuth } from '@/hooks/useAuth';
import { toast } from '@/utils/toast';
import { API_URL } from '@/constants';

const Recomendacoes: React.FC = () => {
  const { user } = useAuth();
  const [recomendacoes, setRecomendacoes] = useState<Recomendacao[]>([]);
  const [populares, setPopulares] = useState<Recomendacao[]>([]);
  const [loading, setLoading] = useState(true);
  const [abaAtiva, setAbaAtiva] = useState<'personalizadas' | 'populares'>('personalizadas');
  const [reservando, setReservando] = useState<string | null>(null);
  const [solicitando, setSolicitando] = useState<string | null>(null);
  const [livroSelecionado, setLivroSelecionado] = useState<Recomendacao | null>(null);
  const [modalAberto, setModalAberto] = useState(false);

  useEffect(() => {
    loadRecomendacoes();
  }, []);

  const loadRecomendacoes = async () => {
    try {
      setLoading(true);
      const [recsData, popsData] = await Promise.all([
        getRecomendacoes(),
        getLivrosPopulares()
      ]);
      setRecomendacoes(recsData);
      setPopulares(popsData);
    } catch (error: any) {
      toast.error('Erro ao carregar recomendações');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleReservar = async (livro: Recomendacao) => {
    if (!user) return;
    try {
      setReservando(livro.id);
      await reservasService.createReserva({
        usuarioId: user.id,
        livroId: livro.id,
      });
      toast.success(`Reserva criada para "${livro.titulo}"!`);
    } catch (error: any) {
      toast.error(error.message || 'Erro ao criar reserva');
    } finally {
      setReservando(null);
    }
  };

  const handleSolicitarEmprestimo = async (livro: Recomendacao) => {
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

  const abrirDetalhes = (livro: Recomendacao) => {
    setLivroSelecionado(livro);
    setModalAberto(true);
  };

  const getConfiancaColor = (confianca: number) => {
    if (confianca >= 80) return 'bg-green-100 text-green-800';
    if (confianca >= 50) return 'bg-yellow-100 text-yellow-800';
    return 'bg-gray-100 text-gray-800';
  };

  const renderLivroCard = (livro: Recomendacao) => (
    <Card key={livro.id} className="overflow-hidden hover:shadow-lg transition-shadow">
      <div className="flex gap-4">
        {/* Capa do Livro */}
        <div className="flex-shrink-0 w-24 h-32 bg-gray-200 rounded-md overflow-hidden">
          {livro.capaURL ? (
            <img
              src={new URL(livro.capaURL, API_URL).toString()}
              alt={livro.titulo}
              className="w-full h-full object-cover"
              onError={(e) => {
                e.currentTarget.style.display = 'none';
                const parent = e.currentTarget.parentElement;
                if (parent) {
                  parent.innerHTML = '<div class="w-full h-full flex items-center justify-center bg-gray-100"><svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path></svg></div>';
                }
              }}
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <BookHeart className="w-8 h-8 text-gray-400" />
            </div>
          )}
        </div>

        {/* Informações do Livro */}
        <div className="flex-grow">
          <div className="flex items-start justify-between mb-2">
            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-1">
                {livro.titulo}
              </h3>
              <p className="text-sm text-gray-600">{livro.nomeAutor}</p>
              {livro.nomeCategoria && (
                <Badge variant="info" className="mt-1">
                  {livro.nomeCategoria}
                </Badge>
              )}
            </div>
            <Badge className={getConfiancaColor(livro.confianca)}>
              {livro.confianca}% match
            </Badge>
          </div>

          {/* Motivo da Recomendação */}
          <div className="mb-3">
            <p className="text-sm text-gray-700 italic flex items-center gap-1">
              <Brain className="w-4 h-4" />
              {livro.motivoRecomendacao}
            </p>
          </div>

          {/* Informações Adicionais */}
          <div className="flex items-center gap-4 text-sm text-gray-600 mb-3">
            {livro.qtdPaginas && <span>{livro.qtdPaginas} páginas</span>}
            {livro.localizacao && <span>📍 {livro.localizacao}</span>}
            <span className={livro.qtdDisponivel > 0 ? 'text-green-600 font-medium' : 'text-red-600'}>
              {livro.qtdDisponivel > 0 
                ? `${livro.qtdDisponivel} disponível(eis)` 
                : 'Indisponível'}
            </span>
          </div>

          {/* Ações */}
          <div className="flex gap-2">
            <Button
              size="sm"
              variant="outline"
              onClick={() => abrirDetalhes(livro)}
            >
              <Eye className="w-4 h-4 mr-1" />
              Ver Detalhes
            </Button>
            <Button
              size="sm"
              variant="primary"
              onClick={() => handleSolicitarEmprestimo(livro)}
              disabled={livro.qtdDisponivel === 0 || solicitando === livro.id}
            >
              {solicitando === livro.id ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin mr-1" />
                  Solicitando...
                </>
              ) : (
                'Solicitar'
              )}
            </Button>
            {livro.qtdDisponivel === 0 && (
              <Button
                size="sm"
                variant="secondary"
                onClick={() => handleReservar(livro)}
                disabled={reservando === livro.id}
              >
                {reservando === livro.id ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin mr-1" />
                    Reservando...
                  </>
                ) : (
                  'Reservar'
                )}
              </Button>
            )}
          </div>
        </div>
      </div>
    </Card>
  );

  const livrosExibidos = abaAtiva === 'personalizadas' ? recomendacoes : populares;

  return (
    <MemberLayout>
      <div className="space-y-6">
        {/* Cabeçalho */}
        <div>
          <div className="flex items-center gap-2 mb-2">
            <Sparkles className="w-6 h-6 text-yellow-500" />
            <h1 className="text-3xl font-bold text-gray-900">Para Você</h1>
          </div>
          <p className="text-gray-600">
            Recomendações personalizadas baseadas no seu histórico de leitura
          </p>
        </div>

        {/* Abas */}
        <div className="flex gap-2 border-b border-gray-200">
          <button
            onClick={() => setAbaAtiva('personalizadas')}
            className={`px-4 py-2 font-medium transition-colors ${
              abaAtiva === 'personalizadas'
                ? 'text-blue-600 border-b-2 border-blue-600'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            <div className="flex items-center gap-2">
              <Brain className="w-4 h-4" />
              Personalizadas ({recomendacoes.length})
            </div>
          </button>
          <button
            onClick={() => setAbaAtiva('populares')}
            className={`px-4 py-2 font-medium transition-colors ${
              abaAtiva === 'populares'
                ? 'text-blue-600 border-b-2 border-blue-600'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            <div className="flex items-center gap-2">
              <TrendingUp className="w-4 h-4" />
              Em Alta ({populares.length})
            </div>
          </button>
        </div>

        {/* Lista de Recomendações */}
        {loading ? (
          <div className="flex items-center justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
          </div>
        ) : livrosExibidos.length === 0 ? (
          <EmptyState
            icon={<Sparkles className="w-12 h-12" />}
            title="Nenhuma recomendação disponível"
            message={
              abaAtiva === 'personalizadas'
                ? 'Comece a emprestar livros para receber recomendações personalizadas!'
                : 'Nenhum livro popular no momento.'
            }
          />
        ) : (
          <div className="grid gap-4">
            {livrosExibidos.map(renderLivroCard)}
          </div>
        )}

        {/* Rodapé Informativo */}
        {!loading && livrosExibidos.length > 0 && (
          <Card className="bg-blue-50 border-blue-200">
            <div className="flex items-start gap-3">
              <Brain className="w-5 h-5 text-blue-600 mt-0.5" />
              <div>
                <h3 className="font-semibold text-blue-900 mb-1">
                  Como funcionam as recomendações?
                </h3>
                <p className="text-sm text-blue-800">
                  Nosso sistema de IA analisa seu histórico de leitura, 
                  compara com usuários similares e identifica padrões 
                  para sugerir livros que você provavelmente vai adorar. 
                  Quanto mais você emprestar, melhores serão as recomendações!
                </p>
              </div>
            </div>
          </Card>
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

export default Recomendacoes;
