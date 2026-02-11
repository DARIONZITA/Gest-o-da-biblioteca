import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { ROUTES } from '@/constants';

interface PrivateRouteProps {
  children: React.ReactElement;
}

export const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-800"></div>
      </div>
    );
  }

  return isAuthenticated ? children : <Navigate to={ROUTES.LOGIN} replace />;
};

export const AdminRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const { isAuthenticated, isAdmin, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-800"></div>
      </div>
    );
  }

  if (!isAuthenticated) return <Navigate to={ROUTES.LOGIN} replace />;
  if (!isAdmin) return <Navigate to={ROUTES.MEMBRO_CATALOGO} replace />;
  return children;
};

export const MemberRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const { isAuthenticated, isAdmin, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-800"></div>
      </div>
    );
  }

  if (!isAuthenticated) return <Navigate to={ROUTES.LOGIN} replace />;
  if (isAdmin) return <Navigate to={ROUTES.ADMIN_DASHBOARD} replace />;
  return children;
};
