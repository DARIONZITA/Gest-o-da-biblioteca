import React, { useEffect, useState } from 'react';
import AdminLayout from '@/layouts/AdminLayout';
import Card from '@/components/Card';
import api from '@/services/api';
import { toast } from '@/utils/toast';
import { CreditCard, Search } from 'lucide-react';

interface Emprestimo {
  id: string;
  nomeUsuario: string;
  tituloLivro: string;
  valorMulta: number;
  dataPrevista: string;
  status: string;
}

const Pagamentos: React.FC = () => {
  const [emprestimos, setEmprestimos] = useState<Emprestimo[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  const fetchMultas = async () => {
    try {
      setLoading(true);
      const response = await api.get<Emprestimo[]>('/emprestimos');
      // Filter only loans with fines > 0
      const comMulta = response.data.filter(e => e.valorMulta > 0);
      setEmprestimos(comMulta);
    } catch (error) {
      toast.error('Erro ao carregar multas');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMultas();
  }, []);

  const handlePagar = async (id: string) => {
    try {
      if (!confirm('Confirmar o pagamento da multa?')) return;

      await api.patch(`/emprestimos/${id}/pagar`);
      toast.success('Pagamento registado com sucesso!');
      fetchMultas(); // Refresh list
    } catch (error) {
      toast.error('Erro ao registar pagamento');
    }
  };

  const filtered = emprestimos.filter(e =>
    (e.nomeUsuario || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
    (e.tituloLivro || '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex justify-between items-end">
          <div className="animate-slide-up">
            <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Pagamentos</h1>
            <p className="text-sm text-gray-400 mt-0.5 font-medium">Gerir multas pendentes</p>
          </div>
        </div>

        <Card padding="lg" className="animate-slide-up stagger-1">
          <div className="mb-6 flex gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                placeholder="Pesquisar por aluno ou livro..."
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-200 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all"
              />
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left">
              <thead className="bg-gray-50 text-gray-500 font-medium">
                <tr>
                  <th className="px-4 py-3 rounded-l-lg">Aluno</th>
                  <th className="px-4 py-3">Livro</th>
                  <th className="px-4 py-3">Vencimento</th>
                  <th className="px-4 py-3">Valor da Multa</th>
                  <th className="px-4 py-3 rounded-r-lg text-right">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {loading ? (
                  <tr><td colSpan={5} className="px-4 py-8 text-center text-gray-500">Carregando...</td></tr>
                ) : filtered.length === 0 ? (
                  <tr><td colSpan={5} className="px-4 py-8 text-center text-gray-500">Nenhuma multa pendente encontrada.</td></tr>
                ) : (
                  filtered.map(emp => (
                    <tr key={emp.id} className="hover:bg-gray-50 transition-colors">
                      <td className="px-4 py-3 font-medium text-gray-900">{emp.nomeUsuario}</td>
                      <td className="px-4 py-3 text-gray-600">{emp.tituloLivro}</td>
                      <td className="px-4 py-3 text-gray-500">{new Date(emp.dataPrevista).toLocaleDateString()}</td>
                      <td className="px-4 py-3 font-bold text-red-600">
                        {emp.valorMulta.toLocaleString('pt-AO', { style: 'currency', currency: 'AOA' })}
                      </td>
                      <td className="px-4 py-3 text-right">
                        <button
                          onClick={() => handlePagar(emp.id)}
                          className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-green-50 text-green-700 hover:bg-green-100 rounded-md transition-colors text-xs font-medium border border-green-200"
                        >
                          <CreditCard className="w-3.5 h-3.5" />
                          Registar Pagamento
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>
      </div>
    </AdminLayout>
  );
};

export default Pagamentos;
