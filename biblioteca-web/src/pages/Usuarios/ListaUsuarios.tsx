import React from 'react';
import AdminLayout from '@/layouts/AdminLayout';
import Card from '@/components/Card';
import { Users } from 'lucide-react';

const ListaUsuarios: React.FC = () => {
  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="animate-slide-up">
          <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Usuários</h1>
          <p className="text-sm text-gray-400 mt-0.5 font-medium">Gerencie os usuários da biblioteca</p>
        </div>

        <Card padding="lg" className="animate-slide-up stagger-1">
          <div className="text-center py-16">
            <div className="w-20 h-20 rounded-2xl bg-gray-100 flex items-center justify-center mx-auto mb-5">
              <Users className="w-9 h-9 text-gray-300" strokeWidth={1.5} />
            </div>
            <h3 className="text-base font-bold text-gray-900 mb-1">
              Módulo em Desenvolvimento
            </h3>
            <p className="text-sm text-gray-400 mb-8 max-w-md mx-auto">
              A gestão de usuários estará disponível em breve.
            </p>
            <div className="inline-flex flex-col items-start text-left bg-gray-50 rounded-xl px-6 py-4 text-sm">
              <p className="text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-2">Funcionalidades planejadas</p>
              <ul className="space-y-1.5 text-gray-500 text-sm">
                <li className="flex items-center gap-2"><span className="w-1 h-1 rounded-full bg-primary-400"></span>Cadastro de estudantes, professores e funcionários</li>
                <li className="flex items-center gap-2"><span className="w-1 h-1 rounded-full bg-primary-400"></span>Visualização de histórico de empréstimos</li>
                <li className="flex items-center gap-2"><span className="w-1 h-1 rounded-full bg-primary-400"></span>Gerenciamento de multas</li>
                <li className="flex items-center gap-2"><span className="w-1 h-1 rounded-full bg-primary-400"></span>Bloqueio/desbloqueio de usuários</li>
              </ul>
            </div>
          </div>
        </Card>
      </div>
    </AdminLayout>
  );
};

export default ListaUsuarios;
