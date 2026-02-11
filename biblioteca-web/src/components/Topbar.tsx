import React from 'react';
import { useAuth } from '@/hooks/useAuth';
import { LogOut, User } from 'lucide-react';

const Topbar: React.FC = () => {
  const { user, logout } = useAuth();

  return (
    <header className="bg-white border-b border-gray-100 h-16 fixed top-0 right-0 left-64 z-10 flex items-center justify-between px-8 shadow-[0_2px_10px_-3px_rgba(6,81,237,0.1)]">
      <div className="flex items-center gap-4">
        {/* Placeholder para breadcrumbs ou título dinâmico se necessário */}
        <h1 className="text-xl font-bold text-gray-800 tracking-tight">
          Painel Administrativo
        </h1>
      </div>

      <div className="flex items-center gap-6">
        <div className="flex items-center gap-3 pl-6 border-l border-gray-100">
          <div className="text-right hidden sm:block">
            <p className="text-sm font-bold text-gray-900 leading-none mb-1">{user?.nome}</p>
            <p className="text-xs text-gray-500 font-medium">{user?.perfil === 'ADMIN' ? 'Administrador' : 'Membro'}</p>
          </div>
          <div className="relative group cursor-pointer">
            <div className="w-10 h-10 rounded-full bg-primary-50 border-2 border-white shadow-sm flex items-center justify-center overflow-hidden transition-transform group-hover:scale-105">
              <User className="w-5 h-5 text-primary-600" />
            </div>
            <div className="absolute bottom-0 right-0 w-3 h-3 bg-emerald-500 border-2 border-white rounded-full"></div>
          </div>
        </div>

        <button
          onClick={logout}
          className="p-2 rounded-lg text-gray-400 hover:text-red-600 hover:bg-red-50 transition-all duration-200"
          title="Sair"
        >
          <LogOut className="w-5 h-5" />
        </button>
      </div>
    </header>
  );
};

export default Topbar;
