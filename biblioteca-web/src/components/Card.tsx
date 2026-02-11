import React, { HTMLAttributes } from 'react';

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  padding?: 'none' | 'sm' | 'md' | 'lg';
  shadow?: 'none' | 'sm' | 'md' | 'lg';
  hover?: boolean;
}

const Card: React.FC<CardProps> = ({
  children,
  padding = 'md',
  shadow = 'sm',
  hover = false,
  className = '',
  ...props
}) => {
  const paddings = {
    none: 'p-0',
    sm: 'p-4',
    md: 'p-5',
    lg: 'p-6',
  };
  
  const shadows = {
    none: '',
    sm: 'shadow-sm',
    md: 'shadow-md',
    lg: 'shadow-lg',
  };

  const hoverClass = hover ? 'hover:shadow-md hover:-translate-y-0.5 cursor-pointer' : '';
  
  return (
    <div
      className={`bg-white rounded-xl border border-gray-100 transition-all duration-300 ease-out ${paddings[padding]} ${shadows[shadow]} ${hoverClass} ${className}`}
      {...props}
    >
      {children}
    </div>
  );
};

export default Card;
