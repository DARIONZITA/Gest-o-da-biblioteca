import React, { useEffect, useState } from 'react';
import MemberLayout from '@/layouts/MemberLayout';
import Card from '@/components/Card';
import EmptyState from '@/components/EmptyState';
import { User } from 'lucide-react';
import { Usuario } from '@/types/usuario';
import { usuariosService } from '@/services/usuariosService';
import { toast } from '@/utils/toast';

const Perfil: React.FC = () => {
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadPerfil = async () => {
      try {
        setLoading(true);
        const data = await usuariosService.getMe();
        setUsuario(data);
      } catch (error: any) {
        toast.error(error.message || 'Erro ao carregar perfil');
      } finally {
        setLoading(false);
      }
    };

    loadPerfil();
  }, []);

  return (
    <MemberLayout>
      <div className="space-y-6">
        <div className="animate-slide-up">
          <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Meu Perfil</h1>
          <p className="text-sm text-gray-400 mt-0.5 font-medium">Dados da sua conta</p>
        </div>

        {loading ? (
          <Card padding="lg">
            <div className="flex items-center justify-center h-48">
              <div className="w-8 h-8 border-[3px] border-gray-200 border-t-primary-600 rounded-full animate-spin" />
            </div>
          </Card>
        ) : usuario ? (
          <Card padding="lg">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400">Nome</p>
                <p className="text-sm font-semibold text-gray-900 mt-1">{usuario.nome}</p>
              </div>
              <div>
                <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400">Email</p>
                <p className="text-sm font-semibold text-gray-900 mt-1">{usuario.email}</p>
              </div>
              <div>
                <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400">Matrícula</p>
                <p className="text-sm font-semibold text-gray-900 mt-1">{usuario.matricula}</p>
              </div>
              <div>
                <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400">Perfil</p>
                <p className="text-sm font-semibold text-gray-900 mt-1">{usuario.perfil}</p>
              </div>
              <div>
                <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400">Status</p>
                <p className="text-sm font-semibold text-gray-900 mt-1">{usuario.status}</p>
              </div>
              <div>
                <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400">Cadastro</p>
                <p className="text-sm font-semibold text-gray-900 mt-1">
                  {new Date(usuario.dataCadastro).toLocaleDateString('pt-BR')}
                </p>
              </div>
            </div>
          </Card>
        ) : (
          <Card padding="lg">
            <EmptyState
              icon={<User className="w-12 h-12" />}
              title="Perfil indisponível"
              message="Não foi possível carregar os dados do perfil"
            />
          </Card>
        )}
      </div>
    </MemberLayout>
  );
};

export default Perfil;
