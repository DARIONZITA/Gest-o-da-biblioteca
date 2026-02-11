import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { ArrowLeft, Save } from 'lucide-react';
import AdminLayout from '@/layouts/AdminLayout';
import Button from '@/components/Button';
import Input from '@/components/Input';
import Card from '@/components/Card';
import { ROUTES } from '@/constants';
import { toast } from '@/utils/toast';
import { usuariosService } from '@/services/usuariosService';
import { PerfilUsuario, StatusUsuario } from '@/types/usuario';

const membroSchema = z.object({
  nome: z.string().min(10, 'Nome deve ter pelo menos 10 caracteres'),
  matricula: z.number().positive('Matrícula deve ser positiva'),
  email: z.string().email('Email inválido'),
  senha: z.string().min(6, 'Senha deve ter pelo menos 6 caracteres'),
});

type MembroFormData = z.infer<typeof membroSchema>;

const CadastroMembro: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<MembroFormData>({
    resolver: zodResolver(membroSchema),
  });

  const onSubmit = async (data: MembroFormData) => {
    try {
      setLoading(true);
      await usuariosService.createUsuario({
        matricula: data.matricula,
        nome: data.nome,
        email: data.email,
        senha: data.senha,
        perfil: PerfilUsuario.MEMBER,
        status: StatusUsuario.ATIVO,
      });
      toast.success('Membro registado com sucesso!');
      navigate(ROUTES.ADMIN_MEMBROS);
    } catch (error: any) {
      toast.error(error.message || 'Erro ao registar membro');
    } finally {
      setLoading(false);
    }
  };

  return (
    <AdminLayout>
      <div className="space-y-6 max-w-3xl">
        <div className="flex items-center gap-4 animate-slide-up">
          <button
            onClick={() => navigate(ROUTES.ADMIN_MEMBROS)}
            className="p-2 rounded-lg text-gray-400 hover:text-gray-700 hover:bg-gray-100 transition-all duration-200"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div>
            <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Registar Membro</h1>
            <p className="text-sm text-gray-400 mt-0.5 font-medium">Adicione um novo membro à biblioteca</p>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <Card padding="lg" className="animate-slide-up stagger-1">
            <h2 className="text-sm font-bold text-gray-900 uppercase tracking-wider mb-5">Dados do Membro</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
              <div className="md:col-span-2">
                <Input label="Nome Completo" placeholder="Ex: Maria Santos da Silva" error={errors.nome?.message} {...register('nome')} />
              </div>
              <Input label="Matrícula" type="number" placeholder="Ex: 20260001" error={errors.matricula?.message} {...register('matricula', { valueAsNumber: true })} />
              <Input label="Email" type="email" placeholder="Ex: nome@isptec.co.ao" error={errors.email?.message} {...register('email')} />
              <Input label="Senha" type="password" placeholder="Mínimo 6 caracteres" error={errors.senha?.message} {...register('senha')} />
            </div>
          </Card>

          <div className="flex justify-end gap-3 animate-slide-up stagger-2 pb-4">
            <Button type="button" variant="outline" onClick={() => navigate(ROUTES.ADMIN_MEMBROS)}>Cancelar</Button>
            <Button type="submit" variant="primary" loading={loading}>
              <Save className="w-4 h-4" />
              Registar Membro
            </Button>
          </div>
        </form>
      </div>
    </AdminLayout>
  );
};

export default CadastroMembro;
