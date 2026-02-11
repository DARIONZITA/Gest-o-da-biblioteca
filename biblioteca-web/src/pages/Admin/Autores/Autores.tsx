import React, { useState, useEffect } from 'react';
import AdminLayout from '@/layouts/AdminLayout';
import Card from '@/components/Card';
import Button from '@/components/Button';
import Input from '@/components/Input';
import { PenTool, Plus, Trash2, Save, X, Search } from 'lucide-react';
import { Autor } from '@/types/autor';
import { autoresService } from '@/services/autoresService';
import { toast } from '@/utils/toast';

const Autores: React.FC = () => {
  const [autores, setAutores] = useState<Autor[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [saving, setSaving] = useState(false);

  const fetchAutores = async () => {
    try {
      setLoading(true);
      const data = await autoresService.getAutores();
      setAutores(data);
    } catch (error: any) {
      toast.error(error.message || 'Erro ao carregar autores');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAutores();
  }, []);

  const resetForm = () => {
    setNome('');
    setDescricao('');
    setShowForm(false);
  };

  const handleAdd = async () => {
    if (!nome.trim()) {
      toast.error('Nome do autor é obrigatório');
      return;
    }

    try {
      setSaving(true);
      const novo = await autoresService.createAutor({
        nome: nome.trim(),
        descricao: descricao.trim() || nome.trim(),
      });
      setAutores(prev => [...prev, novo]);
      toast.success('Autor cadastrado com sucesso!');
      resetForm();
    } catch (error: any) {
      toast.error(error.message || 'Erro ao cadastrar autor');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await autoresService.deleteAutor(id);
      setAutores(prev => prev.filter(a => a.id !== id));
      toast.success('Autor removido com sucesso!');
    } catch (error: any) {
      toast.error(error.message || 'Erro ao remover autor');
    }
  };

  const filtered = autores.filter(a =>
    a.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (a.descricao || '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between animate-slide-up">
          <div>
            <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Autores</h1>
            <p className="text-sm text-gray-400 mt-0.5 font-medium">Gerencie os autores cadastrados</p>
          </div>
          <Button variant="primary" onClick={() => { resetForm(); setShowForm(!showForm); }}>
            {showForm ? <X className="w-4 h-4" /> : <Plus className="w-4 h-4" />}
            {showForm ? 'Cancelar' : 'Novo Autor'}
          </Button>
        </div>

        {showForm && (
          <Card padding="lg" className="animate-scale-in border-2 border-primary-100">
            <h2 className="text-sm font-bold text-gray-900 uppercase tracking-wider mb-4">
              Novo Autor
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input label="Nome" placeholder="Ex: Pepetela" value={nome} onChange={(e) => setNome(e.target.value)} />
              <Input label="Descrição" placeholder="Breve descrição do autor" value={descricao} onChange={(e) => setDescricao(e.target.value)} />
            </div>
            <div className="flex justify-end mt-4 gap-2">
              <Button variant="outline" onClick={resetForm}>Cancelar</Button>
              <Button variant="primary" onClick={handleAdd} loading={saving}>
                <Save className="w-4 h-4" />
                Cadastrar Autor
              </Button>
            </div>
          </Card>
        )}

        <Card padding="md" className="animate-slide-up stagger-1">
          <Input
            placeholder="Buscar por nome ou nacionalidade..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            icon={<Search className="w-4 h-4" />}
          />
        </Card>

        {loading ? (
          <Card padding="lg">
            <div className="flex items-center justify-center h-32">
              <div className="w-8 h-8 border-[3px] border-gray-200 border-t-primary-600 rounded-full animate-spin" />
            </div>
          </Card>
        ) : filtered.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 animate-slide-up stagger-2">
            {filtered.map((autor) => (
              <Card key={autor.id} padding="lg" hover className="group">
                <div className="flex items-start justify-between">
                  <div className="flex items-start gap-3 flex-1 min-w-0">
                    <div className="p-2.5 rounded-xl bg-primary-50 text-primary-700 shrink-0">
                      <PenTool className="w-5 h-5" />
                    </div>
                    <div className="min-w-0">
                      <h3 className="text-sm font-bold text-gray-900">{autor.nome}</h3>
                      {autor.descricao && (
                        <p className="text-xs text-gray-500 mt-1 line-clamp-2">{autor.descricao}</p>
                      )}
                      {autor.dataCadastro && (
                        <div className="flex items-center gap-4 mt-3 text-xs text-gray-400">
                          <span>Desde {new Date(autor.dataCadastro).toLocaleDateString('pt-BR')}</span>
                        </div>
                      )}
                    </div>
                  </div>
                  <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity duration-200 shrink-0 ml-2">
                    <button
                      onClick={() => handleDelete(autor.id)}
                      className="p-2 rounded-lg text-gray-400 hover:text-red-600 hover:bg-red-50 transition-all duration-200"
                      title="Excluir"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <Card padding="lg">
            <div className="text-center py-12">
              <PenTool className="w-8 h-8 text-gray-300 mx-auto mb-2" />
              <p className="text-sm text-gray-400 font-medium">
                {searchTerm ? 'Nenhum autor encontrado' : 'Nenhum autor cadastrado'}
              </p>
            </div>
          </Card>
        )}

        {/* Footer */}
        {filtered.length > 0 && (
          <div className="animate-fade-in">
            <p className="text-xs text-gray-400 font-medium">
              Mostrando {filtered.length} de {autores.length} autores
            </p>
          </div>
        )}
      </div>
    </AdminLayout>
  );
};

export default Autores;
