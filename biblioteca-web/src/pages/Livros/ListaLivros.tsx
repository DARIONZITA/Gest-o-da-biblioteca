import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, Search, Filter, Trash2, BookOpen } from 'lucide-react';
import AdminLayout from '@/layouts/AdminLayout';
import Button from '@/components/Button';
import Input from '@/components/Input';
import Card from '@/components/Card';
import Badge from '@/components/Badge';
import EmptyState from '@/components/EmptyState';
import { API_URL, ROUTES } from '@/constants';
import { Livro } from '@/types/livro';
import { toast } from '@/utils/toast';
import { livrosService } from '@/services/livrosService';

const ListaLivros: React.FC = () => {
  const navigate = useNavigate();
  const [livros, setLivros] = useState<Livro[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    const loadLivros = async () => {
      try {
        setLoading(true);
        const data = await livrosService.getLivros();
        setLivros(data);
      } catch (error: any) {
        console.error('Erro ao carregar livros:', error);
        toast.error('Erro ao carregar livros');
      } finally {
        setLoading(false);
      }
    };

    loadLivros();
  }, []);

  const handleDelete = async (isbn: string) => {
    if (window.confirm('Tem certeza que deseja excluir este livro?')) {
      try {
        await livrosService.deleteLivro(isbn);
        setLivros(livros.filter((l) => l.isbn !== isbn));
        toast.success('Livro excluído com sucesso!');
      } catch (error) {
        toast.error('Erro ao excluir livro');
      }
    }
  };

  const filteredLivros = livros.filter(l =>
    !searchTerm ||
    l.titulo.toLowerCase().includes(searchTerm.toLowerCase()) ||
    l.nomeAutor.toLowerCase().includes(searchTerm.toLowerCase()) ||
    l.isbn.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <AdminLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between animate-slide-up">
          <div>
            <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Livros</h1>
            <p className="text-sm text-gray-400 mt-0.5 font-medium">Gerencie o acervo da biblioteca</p>
          </div>
          <Button
            variant="primary"
            onClick={() => navigate(ROUTES.ADMIN_LIVROS_NOVO)}
          >
            <Plus className="w-4 h-4" />
            Novo Livro
          </Button>
        </div>

        {/* Search and Filters */}
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

        {/* Table */}
        <Card padding="none" className="animate-slide-up stagger-2 overflow-hidden">
          {loading ? (
            <div className="flex items-center justify-center h-64">
              <div className="w-8 h-8 border-[3px] border-gray-200 border-t-primary-600 rounded-full animate-spin" />
            </div>
          ) : filteredLivros.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-100">
                    <th className="px-6 py-3.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">
                      Livro
                    </th>
                    <th className="px-6 py-3.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">
                      ISBN
                    </th>
                    <th className="px-6 py-3.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">
                      Categoria
                    </th>
                    <th className="px-6 py-3.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">
                      Quantidade
                    </th>
                    <th className="px-6 py-3.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">
                      Disponível
                    </th>
                    <th className="px-6 py-3.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">
                      Ações
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                  {filteredLivros.map((livro) => (
                    <tr key={livro.id} className="hover:bg-gray-50/80 transition-colors duration-150 group">
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          {livro.capaURL ? (
                            <img 
                              src={new URL(livro.capaURL, API_URL).toString()} 
                              alt={`Capa de ${livro.titulo}`}
                              className="w-12 h-16 object-cover rounded border border-gray-200 shadow-sm"
                              onError={(e) => {
                                e.currentTarget.style.display = 'none';
                                e.currentTarget.nextElementSibling?.classList.remove('hidden');
                              }}
                            />
                          ) : null}
                          <div className={`flex items-center justify-center w-12 h-16 bg-gray-100 rounded border border-gray-200 ${livro.capaURL ? 'hidden' : ''}`}>
                            <BookOpen className="w-5 h-5 text-gray-400" />
                          </div>
                          <div>
                            <div className="text-sm font-semibold text-gray-900 group-hover:text-primary-800 transition-colors">{livro.titulo}</div>
                            <div className="text-xs text-gray-400 mt-0.5">{livro.nomeAutor}</div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-500 font-mono text-xs">
                        {livro.isbn}
                      </td>
                      <td className="px-6 py-4">
                        <Badge variant="neutral">{livro.nomeCategoria}</Badge>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600 font-semibold tabular-nums">
                        {livro.qtdTotal}
                      </td>
                      <td className="px-6 py-4">
                        <Badge variant={livro.qtdDisponivel > 0 ? 'success' : 'error'} dot>
                          {livro.qtdDisponivel > 0
                            ? `${livro.qtdDisponivel} disponível`
                            : 'Indisponível'}
                        </Badge>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                          <button
                            onClick={() => handleDelete(livro.isbn)}
                            className="p-2 rounded-lg text-gray-400 hover:text-red-600 hover:bg-red-50 transition-all duration-200"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="py-12">
              <EmptyState
                title="Nenhum livro encontrado"
                message={searchTerm ? 'Tente buscar com outros termos' : 'Cadastre o primeiro livro do acervo'}
                actionButton={
                  <Button variant="primary" onClick={() => navigate(ROUTES.ADMIN_LIVROS_NOVO)}>
                    <Plus className="w-4 h-4" />
                    Cadastrar Primeiro Livro
                  </Button>
                }
              />
            </div>
          )}
        </Card>

        {/* Pagination */}
        {filteredLivros.length > 0 && (
          <div className="flex items-center justify-between animate-fade-in">
            <p className="text-xs text-gray-400 font-medium">
              Mostrando {filteredLivros.length} de {livros.length} livros
            </p>
            <div className="flex gap-1">
              <Button variant="ghost" size="sm" disabled>Anterior</Button>
              <button className="px-3.5 py-1.5 rounded-lg text-sm font-semibold bg-primary-800 text-white">1</button>
              <Button variant="ghost" size="sm" disabled>Próximo</Button>
            </div>
          </div>
        )}
      </div>
    </AdminLayout>
  );
};

export default ListaLivros;
