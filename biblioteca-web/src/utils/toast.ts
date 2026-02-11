import { toast as hotToast, ToastOptions } from 'react-hot-toast';

const defaultOptions: ToastOptions = {
  duration: 4000,
  position: 'top-right',
};

export const toast = {
  success: (message: string, options?: ToastOptions) => {
    hotToast.success(message, { ...defaultOptions, ...options });
  },
  
  error: (message: string, options?: ToastOptions) => {
    hotToast.error(message, { ...defaultOptions, ...options });
  },
  
  warning: (message: string, options?: ToastOptions) => {
    hotToast(message, {
      ...defaultOptions,
      ...options,
      style: {
        border: '1px solid #F59E0B',
        color: '#92400E',
      },
    });
  },
  
  info: (message: string, options?: ToastOptions) => {
    hotToast(message, {
      ...defaultOptions,
      ...options,
      style: {
        border: '1px solid #3B82F6',
        color: '#1E3A8A',
      },
    });
  },
  
  loading: (message: string) => {
    return hotToast.loading(message, defaultOptions);
  },
  
  dismiss: (toastId?: string) => {
    hotToast.dismiss(toastId);
  },
};
