import React, { useState, useEffect } from 'react';
import AdminLayout from '@/layouts/AdminLayout';
import Card from '@/components/Card';
import Button from '@/components/Button';
import Input from '@/components/Input';
import { FolderOpen, Plus, Trash2, Save, X } from 'lucide-react';
import { Categoria } from '@/types/categoria';
import { categoriasService } from '@/services/categoriasService';
import { toast } from '@/utils/toast';

const Categorias: React.FC = () => {
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [saving, setSaving] = useState(false);

  const fetchCategorias = async () => {
    try {
      setLoading(true);
      const data = await categoriasService.getCategorias();
      setCategorias(data);
    } catch (error: any) {
      toast.error(error.message || 'Erro ao carregar categorias');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategorias();
  }, []);

  const handleAdd = async () => {
    if (!nome.trim()) {
      toast.error('Nome da categoria é obrigatório');
      return;
    }
    try {
      setSaving(true);
      const nova = await categoriasService.createCategoria({ nome: nome.trim(), descricao: descricao.trim() || undefined });
      setCategorias(prev => [...prev, nova]);
      setNome('');
      setDescricao('');
      setShowForm(false);
      toast.success('Categoria cadastrada com sucesso!');
    } catch (error: any) {
      toast.error(error.message || 'Erro ao cadastrar categoria');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await categoriasService.deleteCategoria(id);
      setCategorias(prev => prev.filter(c => c.id !== id));
      toast.success('Categoria removida com sucesso!');
    } catch (error: any) {
      toast.error(error.message || 'Erro ao remover categoria');
    }
  };

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between animate-slide-up">
          <div>
            <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Categorias</h1>
            <p className="text-sm text-gray-400 mt-0.5 font-medium">Gerencie as categorias de livros</p>
          </div>
          <Button variant="primary" onClick={() => setShowForm(!showForm)}>
            {showForm ? <X className="w-4 h-4" /> : <Plus className="w-4 h-4" />}
            {showForm ? 'Cancelar' : 'Nova Categoria'}
          </Button>
        </div>

        {showForm && (
          <Card padding="lg" className="animate-scale-in border-2 border-primary-100">
            <h2 className="text-sm font-bold text-gray-900 uppercase tracking-wider mb-4">Nova Categoria</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input label="Nome" placeholder="Ex: Ciências" value={nome} onChange={(e) => setNome(e.target.value)} />
              <Input label="Descrição" placeholder="Breve descrição (opcional)" value={descricao} onChange={(e) => setDescricao(e.target.value)} />
            </div>
            <div className="flex justify-end mt-4">
              <Button variant="primary" onClick={handleAdd} loading={saving}>
                <Save className="w-4 h-4" />
                Cadastrar Categoria
              </Button>
            </div>
          </Card>
        )}

        {loading ? (
          <Card padding="lg">
            <div className="flex items-center justify-center h-32">
              <div className="w-8 h-8 border-[3px] border-gray-200 border-t-primary-600 rounded-full animate-spin" />
            </div>
          </Card>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 animate-slide-up stagger-1">
            {categorias.map((cat) => (
              <Card key={cat.id} padding="md" hover className="group">
                <div className="flex items-start justify-between">
                  <div className="flex items-start gap-3">
                    <div className="p-2.5 rounded-xl bg-primary-50 text-primary-700">
                      <FolderOpen className="w-5 h-5" />
                    </div>
                    <div>
                      <h3 className="text-sm font-bold text-gray-900">{cat.nome}</h3>
                      {cat.descricao && <p className="text-xs text-gray-400 mt-0.5">{cat.descricao}</p>}
                    </div>
                  </div>
                  <button
                    onClick={() => handleDelete(cat.id)}
                    className="p-1.5 rounded-lg text-gray-300 hover:text-red-600 hover:bg-red-50 transition-all duration-200 opacity-0 group-hover:opacity-100"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </Card>
            ))}
          </div>
        )}
      </div>
    </AdminLayout>
  );
};

export default Categorias;
