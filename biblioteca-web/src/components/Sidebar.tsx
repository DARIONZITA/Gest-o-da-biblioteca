import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  Home, 
  BookOpen, 
  Users,
  BookCheck,
  Bookmark,
  RotateCcw,
  CreditCard,
  FolderOpen,
  PenTool,
  BarChart3,
  LucideIcon
} from 'lucide-react';
import { ROUTES } from '@/constants';

interface MenuItem {
  label: string;
  icon: LucideIcon;
  path: string;
}

const menuItems: MenuItem[] = [
  { label: 'Dashboard', icon: Home, path: ROUTES.ADMIN_DASHBOARD },
  { label: 'Livros', icon: BookOpen, path: ROUTES.ADMIN_LIVROS },
  { label: 'Membros', icon: Users, path: ROUTES.ADMIN_MEMBROS },
  { label: 'Empréstimos', icon: BookCheck, path: ROUTES.ADMIN_EMPRESTIMOS },
  { label: 'Reservas', icon: Bookmark, path: ROUTES.ADMIN_RESERVAS },
  { label: 'Devoluções', icon: RotateCcw, path: ROUTES.ADMIN_DEVOLUCOES },
  { label: 'Pagamentos', icon: CreditCard, path: ROUTES.ADMIN_PAGAMENTOS },
  { label: 'Categorias', icon: FolderOpen, path: ROUTES.ADMIN_CATEGORIAS },
  { label: 'Autores', icon: PenTool, path: ROUTES.ADMIN_AUTORES },
  { label: 'Relatórios', icon: BarChart3, path: ROUTES.ADMIN_RELATORIOS },
];

const Sidebar: React.FC = () => {
  return (
    <aside className="w-64 min-h-screen fixed left-0 top-0 z-20 bg-primary-900 border-r border-white/5 shadow-2xl flex flex-col">
      <div className="p-6">
        {/* Logo */}
        <div className="flex items-center gap-3 mb-8">
          <div className="bg-primary-600/20 p-2 rounded-xl ring-1 ring-primary-500/30">
            <BookOpen className="w-6 h-6 text-primary-400" />
          </div>
          <div>
            <h2 className="text-white font-bold text-lg leading-none">ISPTEC</h2>
            <p className="text-secondary-400 text-xs font-medium tracking-wide mt-0.5">Biblioteca</p>
          </div>
        </div>

        {/* Navigation */}
        <div className="mb-6">
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-4 px-3">
            Menu Principal
          </p>
          <nav className="space-y-1">
            {menuItems.map((item) => (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 group relative ${
                    isActive
                      ? 'bg-primary-800 text-white shadow-lg shadow-primary-900/50'
                      : 'text-slate-300 hover:bg-white/5 hover:text-white'
                  }`
                }
              >
                {({ isActive }) => (
                  <>
                    {isActive && (
                      <div className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-secondary-500 rounded-r-full" />
                    )}
                    <item.icon 
                      className={`w-5 h-5 transition-colors ${
                        isActive ? 'text-secondary-400' : 'text-slate-400 group-hover:text-white'
                      }`} 
                    />
                    <span className="text-sm font-medium">{item.label}</span>
                  </>
                )}
              </NavLink>
            ))}
          </nav>
        </div>
      </div>

      {/* Footer */}
      <div className="mt-auto p-6 border-t border-white/5 bg-primary-950/30">
        <div className="flex items-center justify-between">
          <p className="text-xs text-primary-400/40 font-medium">Versão 1.0.0</p>
          <div className="w-2 h-2 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.5)]"></div>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
