import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ROUTES } from '@/constants';
import { AdminRoute, MemberRoute } from '@/components/PrivateRoute';
import { useAuth } from '@/hooks/useAuth';

// Pages
import Login from '@/pages/Login';
import Dashboard from '@/pages/Dashboard';
import ListaLivros from '@/pages/Livros/ListaLivros';
import CadastroLivro from '@/pages/Livros/CadastroLivro';
import ListaMembros from '@/pages/Admin/Membros/ListaMembros';
import CadastroMembro from '@/pages/Admin/Membros/CadastroMembro';
import Devolucoes from '@/pages/Admin/Devolucoes/Devolucoes';
import Pagamentos from '@/pages/Admin/Pagamentos/Pagamentos';
import Categorias from '@/pages/Admin/Categorias/Categorias';
import Autores from '@/pages/Admin/Autores/Autores';
import ListaEmprestimos from '@/pages/Emprestimos/ListaEmprestimos';
import ListaReservas from '@/pages/Reservas/ListaReservas';
import Relatorios from '@/pages/Relatorios/Relatorios';
import Catalogo from '@/pages/Membro/Catalogo';
import Recomendacoes from '@/pages/Membro/Recomendacoes';
import MeusEmprestimos from '@/pages/Membro/MeusEmprestimos';
import MinhasReservas from '@/pages/Membro/MinhasReservas';
import Perfil from '@/pages/Membro/Perfil';

const RootRedirect: React.FC = () => {
  const { isAuthenticated, isAdmin, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-800"></div>
      </div>
    );
  }

  if (!isAuthenticated) return <Navigate to={ROUTES.LOGIN} replace />;
  return isAdmin
    ? <Navigate to={ROUTES.ADMIN_DASHBOARD} replace />
    : <Navigate to={ROUTES.MEMBRO_CATALOGO} replace />;
};

const AppRouter: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path={ROUTES.LOGIN} element={<Login />} />

        {/* Admin Routes */}
        <Route path={ROUTES.ADMIN_DASHBOARD} element={<AdminRoute><Dashboard /></AdminRoute>} />
        <Route path={ROUTES.ADMIN_LIVROS} element={<AdminRoute><ListaLivros /></AdminRoute>} />
        <Route path={ROUTES.ADMIN_LIVROS_NOVO} element={<AdminRoute><CadastroLivro /></AdminRoute>} />
        <Route path={ROUTES.ADMIN_MEMBROS} element={<AdminRoute><ListaMembros /></AdminRoute>} />
        <Route path={ROUTES.ADMIN_MEMBROS_NOVO} element={<AdminRoute><CadastroMembro /></AdminRoute>} />
        <Route path={ROUTES.ADMIN_DEVOLUCOES} element={<AdminRoute><Devolucoes /></AdminRoute>} />
        <Route path={ROUTES.ADMIN_PAGAMENTOS} element={<AdminRoute><Pagamentos /></AdminRoute>} />
        <Route path={ROUTES.ADMIN_CATEGORIAS} element={<AdminRoute><Categorias /></AdminRoute>} />
        <Route path={ROUTES.ADMIN_AUTORES} element={<AdminRoute><Autores /></AdminRoute>} />
        <Route path={ROUTES.ADMIN_EMPRESTIMOS} element={<AdminRoute><ListaEmprestimos /></AdminRoute>} />
        <Route path={ROUTES.ADMIN_RESERVAS} element={<AdminRoute><ListaReservas /></AdminRoute>} />
        <Route path={ROUTES.ADMIN_RELATORIOS} element={<AdminRoute><Relatorios /></AdminRoute>} />

        {/* Member Routes */}
        <Route path={ROUTES.MEMBRO_CATALOGO} element={<MemberRoute><Catalogo /></MemberRoute>} />
        <Route path={ROUTES.MEMBRO_RECOMENDACOES} element={<MemberRoute><Recomendacoes /></MemberRoute>} />
        <Route path={ROUTES.MEMBRO_EMPRESTIMOS} element={<MemberRoute><MeusEmprestimos /></MemberRoute>} />
        <Route path={ROUTES.MEMBRO_RESERVAS} element={<MemberRoute><MinhasReservas /></MemberRoute>} />
        <Route path={ROUTES.MEMBRO_PERFIL} element={<MemberRoute><Perfil /></MemberRoute>} />

        {/* Redirects */}
        <Route path="/" element={<RootRedirect />} />
        <Route path="*" element={<RootRedirect />} />
      </Routes>
    </BrowserRouter>
  );
};

export default AppRouter;
