import React from 'react';
import MemberSidebar from '@/components/MemberSidebar';
import Topbar from '@/components/Topbar';
import ChatWidget from '@/components/ChatWidget';

interface MemberLayoutProps {
  children: React.ReactNode;
}

const MemberLayout: React.FC<MemberLayoutProps> = ({ children }) => {
  return (
    <div className="min-h-screen bg-gray-50/80">
      <MemberSidebar />
      <Topbar />
      
      <main className="ml-64 mt-16 p-8">
        <div className="max-w-7xl mx-auto animate-fade-in">
          {children}
        </div>
      </main>

      {/* Chatbot flutuante */}
      <ChatWidget />
    </div>
  );
};

export default MemberLayout;
