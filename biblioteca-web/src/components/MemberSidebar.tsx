import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  BookOpen, 
  BookMarked, 
  Bookmark,
  User,
  Sparkles,
  LucideIcon
} from 'lucide-react';
import { ROUTES } from '@/constants';

interface MenuItem {
  label: string;
  icon: LucideIcon;
  path: string;
}

const menuItems: MenuItem[] = [
  { label: 'Catálogo', icon: BookOpen, path: ROUTES.MEMBRO_CATALOGO },
  { label: 'Para Você', icon: Sparkles, path: ROUTES.MEMBRO_RECOMENDACOES },
  { label: 'Meus Empréstimos', icon: BookMarked, path: ROUTES.MEMBRO_EMPRESTIMOS },
  { label: 'Minhas Reservas', icon: Bookmark, path: ROUTES.MEMBRO_RESERVAS },
  { label: 'Meu Perfil', icon: User, path: ROUTES.MEMBRO_PERFIL },
];

const MemberSidebar: React.FC = () => {
  return (
    <aside className="w-64 min-h-screen fixed left-0 top-0 z-20" style={{ background: 'linear-gradient(135deg, #0C4A6E 0%, #0369A1 50%, #0EA5E9 100%)' }}>
      {/* Subtle pattern overlay */}
      <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiNmZmZmZmYiIGZpbGwtb3BhY2l0eT0iMC4wMyI+PHBhdGggZD0iTTM2IDM0djItSDI0di0yaDEyem0wLTRWMjhIMjR2Mmgxem0tMi0yVjI2SDI2djJoOHptLTYtNnYySDI2di0yaDJ6bTAtNHYySDE4di0yaDEwem0tNC00djJIMjB2LTJoNHoiLz48L2c+PC9nPjwvc3ZnPg==')] opacity-50" />

      <div className="relative p-6">
        {/* Logo */}
        <div className="flex items-center gap-3 mb-10 animate-fade-in">
          <div className="w-11 h-11 bg-white rounded-xl flex items-center justify-center shadow-lg shadow-black/10">
            <BookOpen className="w-6 h-6 text-sky-800" />
          </div>
          <div>
            <h2 className="text-white font-extrabold text-lg tracking-tight">ISPTEC</h2>
            <p className="text-sky-200/80 text-[11px] font-medium tracking-wider uppercase">Biblioteca</p>
          </div>
        </div>

        {/* Nav label */}
        <p className="text-[10px] font-bold uppercase tracking-[0.2em] text-sky-200/50 mb-3 px-4">Área do Membro</p>

        {/* Navigation */}
        <nav className="space-y-1">
          {menuItems.map((item, index) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-4 py-2.5 rounded-lg transition-all duration-200 group animate-slide-right ${
                  isActive
                    ? 'bg-white/15 text-white shadow-sm backdrop-blur-sm'
                    : 'text-sky-100/80 hover:bg-white/10 hover:text-white'
                }`
              }
              style={{ animationDelay: `${index * 60}ms` }}
            >
              {({ isActive }) => (
                <>
                  <div className={`p-1.5 rounded-md transition-all duration-200 ${
                    isActive ? 'bg-white/10' : 'group-hover:bg-white/5'
                  }`}>
                    <item.icon className="w-[18px] h-[18px]" strokeWidth={isActive ? 2.2 : 1.8} />
                  </div>
                  <span className={`text-sm ${
                    isActive ? 'font-semibold' : 'font-medium'
                  }`}>{item.label}</span>
                  {isActive && (
                    <div className="ml-auto w-1.5 h-1.5 rounded-full bg-amber-400 animate-scale-in" />
                  )}
                </>
              )}
            </NavLink>
          ))}
        </nav>
      </div>

      {/* Footer */}
      <div className="absolute bottom-0 left-0 right-0 p-6">
        <div className="border-t border-white/10 pt-4">
          <p className="text-[10px] text-sky-200/40 text-center font-medium tracking-wider">v1.0.0</p>
        </div>
      </div>
    </aside>
  );
};

export default MemberSidebar;
