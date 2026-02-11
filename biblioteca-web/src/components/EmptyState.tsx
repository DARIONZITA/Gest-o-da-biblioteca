import React from 'react';

interface EmptyStateProps {
  icon?: React.ReactNode;
  title: string;
  message?: string;
  actionButton?: React.ReactNode;
}

const EmptyState: React.FC<EmptyStateProps> = ({ icon, title, message, actionButton }) => {
  return (
    <div className="flex flex-col items-center justify-center py-12 px-4 text-center animate-fade-in">
      {icon && (
        <div className="text-6xl mb-4 text-gray-200">
          {icon}
        </div>
      )}
      
      <h3 className="text-base font-bold text-gray-900 mb-1">
        {title}
      </h3>
      
      {message && (
        <p className="text-sm text-gray-400 mb-6 max-w-sm">
          {message}
        </p>
      )}
      
      {actionButton && (
        <div>
          {actionButton}
        </div>
      )}
    </div>
  );
};

export default EmptyState;
