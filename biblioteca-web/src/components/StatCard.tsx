import React from 'react';
import { LucideIcon } from 'lucide-react';

interface StatCardProps {
  title: string;
  value: string | number;
  icon: LucideIcon;
  color?: 'primary' | 'secondary' | 'success' | 'warning' | 'error';
  trend?: {
    value: number;
    isPositive: boolean;
  };
}

const StatCard: React.FC<StatCardProps> = ({
  title,
  value,
  icon: Icon,
  color = 'primary',
  trend,
}) => {
  const iconBg = {
    primary: 'bg-primary-800/10 text-primary-700',
    secondary: 'bg-secondary-500/10 text-secondary-600',
    success: 'bg-emerald-500/10 text-emerald-600',
    warning: 'bg-amber-500/10 text-amber-600',
    error: 'bg-red-500/10 text-red-600',
  };

  const borderAccent = {
    primary: 'border-l-primary-700',
    secondary: 'border-l-secondary-500',
    success: 'border-l-emerald-500',
    warning: 'border-l-amber-500',
    error: 'border-l-red-500',
  };
  
  return (
    <div className={`bg-white rounded-xl border border-gray-100 shadow-sm p-6 border-l-4 ${borderAccent[color]} hover:shadow-md transition-all duration-300 group`}>
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <p className="text-xs font-semibold uppercase tracking-wider text-gray-400 mb-2">{title}</p>
          <p className="text-3xl font-extrabold text-gray-900 tabular-nums">{value.toLocaleString('pt-BR')}</p>
          
          {trend && (
            <div className={`flex items-center gap-1 mt-3 text-sm font-medium ${trend.isPositive ? 'text-emerald-600' : 'text-red-600'}`}>
              <span className={`inline-flex items-center justify-center w-5 h-5 rounded-full text-xs ${trend.isPositive ? 'bg-emerald-100' : 'bg-red-100'}`}>
                {trend.isPositive ? '↑' : '↓'}
              </span>
              {Math.abs(trend.value)}% vs mês anterior
            </div>
          )}
        </div>
        
        <div className={`p-3 rounded-xl ${iconBg[color]} group-hover:scale-110 transition-transform duration-300`}>
          <Icon size={22} strokeWidth={2} />
        </div>
      </div>
    </div>
  );
};

export default StatCard;
