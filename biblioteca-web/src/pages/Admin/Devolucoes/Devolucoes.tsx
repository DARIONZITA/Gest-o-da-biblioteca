import React, { useState, useEffect } from 'react';
import AdminLayout from '@/layouts/AdminLayout';
import Card from '@/components/Card';
import Button from '@/components/Button';
import Badge from '@/components/Badge';
import Input from '@/components/Input';
import { RotateCcw, Search, CheckCircle } from 'lucide-react';
import { Emprestimo } from '@/types/emprestimo';
import { emprestimosService } from '@/services/emprestimosService';
import { toast } from '@/utils/toast';

const Devolucoes: React.FC = () => {
  const [emprestimos, setEmprestimos] = useState<Emprestimo[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  const fetchEmprestimos = async () => {
    try {
      setLoading(true);
      const data = await emprestimosService.getEmprestimos();
      setEmprestimos(data.filter(e => e.status === 'ATIVO' || e.status === 'ATRASADO'));
    } catch (error: any) {
      toast.error(error.message || 'Erro ao carregar empréstimos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEmprestimos();
  }, []);

  const handleDevolucao = async (id: string) => {
    try {
      await emprestimosService.devolver(id);
      setEmprestimos(prev => prev.filter(e => e.id !== id));
      toast.success('Devolução registada com sucesso!');
    } catch (error: any) {
      toast.error(error.message || 'Erro ao registar devolução');
    }
  };

  const filtered = emprestimos.filter(e =>
    e.tituloLivro.toLowerCase().includes(searchTerm.toLowerCase()) ||
    e.nomeUsuario.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="animate-slide-up">
          <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Devoluções</h1>
          <p className="text-sm text-gray-400 mt-0.5 font-medium">Registar devoluções de livros emprestados</p>
        </div>

        <Card padding="md" className="animate-slide-up stagger-1">
          <Input
            placeholder="Buscar por título do livro ou nome do membro..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            icon={<Search className="w-4 h-4" />}
          />
        </Card>

        <div className="space-y-3 animate-slide-up stagger-2">
          {loading ? (
            <Card padding="lg">
              <div className="flex items-center justify-center h-32">
                <div className="w-8 h-8 border-[3px] border-gray-200 border-t-primary-600 rounded-full animate-spin" />
              </div>
            </Card>
          ) : filtered.length > 0 ? (
            filtered.map((emp) => (
              <Card key={emp.id} padding="lg" hover className="group">
                <div className="flex items-center justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2">
                      <h3 className="text-sm font-bold text-gray-900">{emp.tituloLivro}</h3>
                      {emp.status === 'ATRASADO' && <Badge variant="error" dot>Atrasado</Badge>}
                      {emp.status === 'ATIVO' && <Badge variant="info" dot>Activo</Badge>}
                    </div>
                    <div className="grid grid-cols-2 md:grid-cols-3 gap-3 text-xs text-gray-500">
                      <div>
                        <span className="block text-[10px] font-bold text-gray-400 uppercase tracking-wider">Membro</span>
                        <span className="font-medium text-gray-700">{emp.nomeUsuario}</span>
                      </div>
                      <div>
                        <span className="block text-[10px] font-bold text-gray-400 uppercase tracking-wider">Data Empréstimo</span>
                        <span>{emp.dataEmprestimo ? new Date(emp.dataEmprestimo).toLocaleDateString('pt-BR') : '-'}</span>
                      </div>
                      <div>
                        <span className="block text-[10px] font-bold text-gray-400 uppercase tracking-wider">Previsão Devolução</span>
                        <span className={emp.status === 'ATRASADO' ? 'text-red-600 font-semibold' : ''}>
                          {new Date(emp.dataPrevista).toLocaleDateString('pt-BR')}
                        </span>
                      </div>
                    </div>
                    {emp.valorMulta > 0 && (
                      <div className="mt-2 inline-flex items-center gap-1 px-2 py-1 rounded-md bg-red-50 text-red-700 text-xs font-bold">
                        Multa: {emp.valorMulta} Kz
                      </div>
                    )}
                  </div>
                  <Button variant="primary" size="sm" onClick={() => handleDevolucao(emp.id)}>
                    <CheckCircle className="w-4 h-4" />
                    Registar Devolução
                  </Button>
                </div>
              </Card>
            ))
          ) : (
            <Card padding="lg">
              <div className="text-center py-12">
                <div className="w-16 h-16 rounded-2xl bg-gray-100 flex items-center justify-center mx-auto mb-4">
                  <RotateCcw className="w-7 h-7 text-gray-300" strokeWidth={1.5} />
                </div>
                <p className="text-sm font-bold text-gray-900 mb-1">Nenhum empréstimo pendente</p>
                <p className="text-xs text-gray-400">
                  {searchTerm ? 'Nenhum resultado encontrado para a busca.' : 'Todos os livros foram devolvidos.'}
                </p>
              </div>
            </Card>
          )}
        </div>
      </div>
    </AdminLayout>
  );
};

export default Devolucoes;
