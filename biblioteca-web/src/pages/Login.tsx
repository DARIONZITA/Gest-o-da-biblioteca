import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Mail, Lock, BookOpen } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { ROUTES } from '@/constants';
import { toast } from '@/utils/toast';
import Button from '@/components/Button';
import Input from '@/components/Input';
import Card from '@/components/Card';

const loginSchema = z.object({
  email: z.string().min(1, 'Email é obrigatório').email('Email inválido'),
  senha: z.string().min(6, 'Senha deve ter no mínimo 6 caracteres'),
});

type LoginFormData = z.infer<typeof loginSchema>;

const Login: React.FC = () => {
  const navigate = useNavigate();
  const { login, isAuthenticated, isAdmin } = useAuth();
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  useEffect(() => {
    if (isAuthenticated) {
      navigate(isAdmin ? ROUTES.ADMIN_DASHBOARD : ROUTES.MEMBRO_CATALOGO);
    }
  }, [isAuthenticated, isAdmin, navigate]);

  const onSubmit = async (data: LoginFormData) => {
    try {
      setLoading(true);
      await login(data.email, data.senha);
      toast.success('Login realizado com sucesso!');
      // O redirect é tratado pelo useEffect que observa isAuthenticated + isAdmin
    } catch (error: any) {
      console.error('Erro no login:', error);
      toast.error(error.message || 'Erro ao fazer login. Verifique suas credenciais.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen relative flex items-center justify-center p-4 overflow-hidden">
      {/* Background */}
      <div className="absolute inset-0 gradient-primary" />
      <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiNmZmZmZmYiIGZpbGwtb3BhY2l0eT0iMC4wNCI+PHBhdGggZD0iTTM2IDM0djItSDI0di0yaDEyem0wLTRWMjhIMjR2Mmgxem0tMi0yVjI2SDI2djJoOHptLTYtNnYySDI2di0yaDJ6bTAtNHYySDE4di0yaDEwem0tNC00djJIMjB2LTJoNHoiLz48L2c+PC9nPjwvc3ZnPg==')] opacity-30" />
      
      {/* Floating Decorations */}
      <div className="absolute top-20 left-20 w-72 h-72 bg-white/5 rounded-full blur-3xl animate-float" />
      <div className="absolute bottom-20 right-20 w-96 h-96 bg-secondary-500/10 rounded-full blur-3xl animate-float" style={{ animationDelay: '2s' }} />

      {/* Login Card */}
      <div className="relative w-full max-w-md animate-scale-in">
        <Card className="backdrop-blur-xl bg-white/95 border-white/20 shadow-2xl" padding="lg">
          <div className="text-center mb-8">
            <div className="w-20 h-20 rounded-2xl gradient-primary flex items-center justify-center mx-auto mb-5 shadow-lg shadow-primary-800/30 animate-float">
              <BookOpen className="w-10 h-10 text-white" strokeWidth={1.5} />
            </div>
            <h1 className="text-2xl font-extrabold text-gray-900 mb-1 tracking-tight">
              Biblioteca ISPTEC
            </h1>
            <p className="text-sm text-gray-500 font-medium">
              Sistema de Gestão Administrativa
            </p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            <div className="animate-slide-up stagger-1">
              <Input
                label="Email"
                type="email"
                placeholder="Ex: admin@biblioteca.local"
                icon={<Mail className="w-4 h-4" />}
                error={errors.email?.message}
                {...register('email')}
              />
            </div>

            <div className="animate-slide-up stagger-2">
              <Input
                label="Senha"
                type="password"
                placeholder="Digite sua senha"
                icon={<Lock className="w-4 h-4" />}
                error={errors.senha?.message}
                {...register('senha')}
              />
            </div>

            <div className="animate-slide-up stagger-3 pt-2">
              <Button
                type="submit"
                variant="primary"
                size="lg"
                fullWidth
                loading={loading}
              >
                Entrar
              </Button>
            </div>
          </form>

          <div className="mt-8 pt-5 border-t border-gray-100 text-center">
            <p className="text-xs text-gray-400 font-medium tracking-wide">
              Acesso restrito a administradores da biblioteca
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default Login;
