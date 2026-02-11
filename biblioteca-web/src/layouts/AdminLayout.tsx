import React from 'react';
import Sidebar from '@/components/Sidebar';
import Topbar from '@/components/Topbar';

interface AdminLayoutProps {
  children: React.ReactNode;
}

const AdminLayout: React.FC<AdminLayoutProps> = ({ children }) => {
  return (
    <div className="min-h-screen bg-slate-50">
      <Sidebar />
      <Topbar />
      
      <main className="ml-64 pt-20 pb-8 px-8">
        <div className="max-w-7xl mx-auto">
          {children}
        </div>
      </main>
    </div>
  );
};

export default AdminLayout;
