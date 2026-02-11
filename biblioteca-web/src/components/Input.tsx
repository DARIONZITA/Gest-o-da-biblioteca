import React, { InputHTMLAttributes, forwardRef } from 'react';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  icon?: React.ReactNode;
}

const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, icon, className = '', ...props }, ref) => {
    return (
      <div className="w-full group">
        {label && (
          <label className="block text-xs font-semibold uppercase tracking-wider text-gray-500 mb-1.5">
            {label}
          </label>
        )}
        
        <div className="relative">
          {icon && (
            <div className="absolute left-3.5 top-1/2 transform -translate-y-1/2 text-gray-400 group-focus-within:text-primary-600 transition-colors duration-200">
              {icon}
            </div>
          )}
          
          <input
            ref={ref}
            className={`
              w-full px-4 py-2.5 border rounded-lg text-sm
              ${icon ? 'pl-11' : ''}
              ${error 
                ? 'border-error-400 bg-error-500/5 focus:ring-error-500 focus:border-error-500' 
                : 'border-gray-200 bg-white focus:ring-primary-500 focus:border-primary-500 hover:border-gray-300'
              }
              focus:outline-none focus:ring-2 focus:ring-offset-0
              disabled:bg-gray-50 disabled:text-gray-400 disabled:cursor-not-allowed
              placeholder:text-gray-400
              transition-all duration-200
              ${className}
            `}
            {...props}
          />
        </div>
        
        {error && (
          <p className="mt-1.5 text-xs font-medium text-error-500 animate-slide-up">{error}</p>
        )}
      </div>
    );
  }
);

Input.displayName = 'Input';

export default Input;
