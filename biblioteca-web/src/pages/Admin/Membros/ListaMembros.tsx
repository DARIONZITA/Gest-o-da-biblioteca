import React, { useState, useEffect } from 'react';
import AdminLayout from '@/layouts/AdminLayout';
import Card from '@/components/Card';
import Button from '@/components/Button';
import Input from '@/components/Input';
import Badge from '@/components/Badge';
import { Users, Plus, Search, Trash2 } from 'lucide-react';
import { Usuario, PerfilUsuario, StatusUsuario } from '@/types/usuario';
import { usuariosService } from '@/services/usuariosService';
import { toast } from '@/utils/toast';
import { ROUTES } from '@/constants';
import { useNavigate } from 'react-router-dom';

const ListaMembros: React.FC = () => {
  const navigate = useNavigate();
  const [membros, setMembros] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  const fetchMembros = async () => {
    try {
      setLoading(true);
      const data = await usuariosService.getUsuarios();
      setMembros(data.filter(u => u.perfil !== PerfilUsuario.ADMIN));
    } catch (error: any) {
      toast.error(error.message || 'Erro ao carregar membros');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMembros();
  }, []);

  const filteredMembros = membros.filter(m =>
    m.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    String(m.matricula).includes(searchTerm) ||
    m.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleDelete = async (id: string) => {
    try {
      await usuariosService.deleteUsuario(id);
      toast.success('Membro removido com sucesso');
      setMembros(prev => prev.filter(m => m.id !== id));
    } catch (error: any) {
      toast.error(error.message || 'Erro ao remover membro');
    }
  };

  const getStatusBadge = (status: StatusUsuario) => {
    switch (status) {
      case StatusUsuario.ATIVO: return <Badge variant="success" dot>Activo</Badge>;
      case StatusUsuario.BLOQUEADO: return <Badge variant="error" dot>Bloqueado</Badge>;
    }
  };

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between animate-slide-up">
          <div>
            <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Membros</h1>
            <p className="text-sm text-gray-400 mt-0.5 font-medium">Gerencie os membros da biblioteca</p>
          </div>
          <Button variant="primary" onClick={() => navigate(ROUTES.ADMIN_MEMBROS_NOVO)}>
            <Plus className="w-4 h-4" />
            Registar Membro
          </Button>
        </div>

        <Card padding="md" className="animate-slide-up stagger-1">
          <div className="flex gap-3">
            <div className="flex-1">
              <Input
                placeholder="Buscar por nome, matrícula ou email..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                icon={<Search className="w-4 h-4" />}
              />
            </div>
          </div>
        </Card>

        <Card padding="none" className="animate-slide-up stagger-2 overflow-hidden">
          {loading ? (
            <div className="flex items-center justify-center h-64">
              <div className="w-8 h-8 border-[3px] border-gray-200 border-t-primary-600 rounded-full animate-spin" />
            </div>
          ) : filteredMembros.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-100">
                    <th className="px-6 py-3.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">Membro</th>
                    <th className="px-6 py-3.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">Matrícula</th>
                    <th className="px-6 py-3.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">Status</th>
                    <th className="px-6 py-3.5 text-left text-[10px] font-bold text-gray-400 uppercase tracking-[0.1em]">Acções</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                  {filteredMembros.map((membro) => (
                    <tr key={membro.id} className="hover:bg-gray-50/80 transition-colors duration-150 group">
                      <td className="px-6 py-4">
                        <div>
                          <div className="text-sm font-semibold text-gray-900">{membro.nome}</div>
                          <div className="text-xs text-gray-400 mt-0.5">{membro.email}</div>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-500 font-mono text-xs">{membro.matricula}</td>
                      <td className="px-6 py-4">{getStatusBadge(membro.status)}</td>
                      <td className="px-6 py-4">
                        <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                          <button
                            onClick={() => handleDelete(membro.id)}
                            className="p-2 rounded-lg text-gray-400 hover:text-red-600 hover:bg-red-50 transition-all duration-200"
                            title="Excluir"
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
            <div className="py-12 text-center">
              <Users className="w-8 h-8 text-gray-300 mx-auto mb-2" />
              <p className="text-sm text-gray-400 font-medium">Nenhum membro encontrado</p>
            </div>
          )}
        </Card>
      </div>
    </AdminLayout>
  );
};

export default ListaMembros;
