import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { ArrowLeft, Save, Upload, Link as LinkIcon, Search, Download } from 'lucide-react';
import AdminLayout from '@/layouts/AdminLayout';
import Button from '@/components/Button';
import Input from '@/components/Input';
import Card from '@/components/Card';
import ImageUploadOcr from '@/components/ImageUploadOcr';
import { ROUTES } from '@/constants';
import { toast } from '@/utils/toast';
import { livrosService } from '@/services/livrosService';
import { autoresService } from '@/services/autoresService';
import { categoriasService } from '@/services/categoriasService';
import { Autor } from '@/types/autor';
import { Categoria } from '@/types/categoria';
import { GoogleBook } from '@/types/livro'; // Import GoogleBook
import { OcrResponse } from '@/types/ocr';

const livroSchema = z.object({
  titulo: z.string().min(1, 'Título é obrigatório'),
  isbn: z.string().min(10, 'ISBN deve ter pelo menos 10 caracteres'),
  autorId: z.string().optional(),
  nomeAutor: z.string().optional(),
  categoriaId: z.string().min(1, 'Categoria é obrigatória'),
  qtdPaginas: z.number().min(1).optional(),
  qtdTotal: z.number().min(1, 'Quantidade deve ser pelo menos 1'),
  localizacao: z.string().optional(),
  capaURL: z.string().optional(),
  nomeEditora: z.string().optional(),
  anoPublicacao: z.number().optional(),
  sinopse: z.string().optional(),
});
// .refine((data) => data.autorId || data.nomeAutor, {
//   message: "Selecione um autor existente ou digite o nome do novo autor",
//   path: ["autorId"], 
// });

type LivroFormSchema = z.infer<typeof livroSchema>;
type CapaMode = 'url' | 'upload';

const CadastroLivro: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [autores, setAutores] = useState<Autor[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [capaMode, setCapaMode] = useState<CapaMode>('url');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<string>('');
  const [ocrResult, setOcrResult] = useState<OcrResponse | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // New state for External Search
  const [activeTab, setActiveTab] = useState<'manual' | 'search'>('manual');
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<GoogleBook[]>([]);
  const [searching, setSearching] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    watch,
  } = useForm<LivroFormSchema>({
    resolver: zodResolver(livroSchema),
    defaultValues: {
      qtdTotal: 1,
    },
  });

  const capaURLValue = watch('capaURL');

  useEffect(() => {
    const loadData = async () => {
      try {
        const [autoresData, categoriasData] = await Promise.all([
          autoresService.getAutores(),
          categoriasService.getCategorias(),
        ]);
        setAutores(autoresData);
        setCategorias(categoriasData);
      } catch (error) {
        toast.error('Erro ao carregar autores/categorias');
      }
    };
    loadData();
  }, []);

  useEffect(() => {
    // Update preview when capaURL changes (from URL input)
    if (capaMode === 'url' && capaURLValue) {
      setPreview(capaURLValue);
    }
  }, [capaURLValue, capaMode]);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      // Validate file type
      if (!file.type.startsWith('image/')) {
        toast.error('Apenas imagens são permitidas');
        return;
      }

      // Validate file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        toast.error('Imagem deve ter no máximo 5MB');
        return;
      }

      setSelectedFile(file);

      // Create preview
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleCapaModeChange = (mode: CapaMode) => {
    setCapaMode(mode);
    setSelectedFile(null);
    setPreview('');
    setValue('capaURL', '');
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };
  const handleOcrComplete = (result: OcrResponse) => {
    if (!result.sucesso) return;

    setOcrResult(result); // Guarda resultado para mostrar badge

    // Helper para preencher com feedback visual
    const fill = (field: keyof LivroFormSchema, value: any, label: string) => {
      setValue(field, value);
      toast.success(`${label} detectado: ${value} (${result.confianca || 0}%)`);
    };

    if (result.titulo) fill('titulo', result.titulo, 'Título');
    if (result.isbn) fill('isbn', result.isbn, 'ISBN');
    if (result.ano) fill('anoPublicacao', result.ano, 'Ano');
    if (result.qtdPaginas) fill('qtdPaginas', result.qtdPaginas, 'Páginas');
    if (result.editora) fill('nomeEditora', result.editora, 'Editora');
    if (result.sinopse) setValue('sinopse', result.sinopse);

    // Tentar encontrar autor correspondente
    if (result.autor) {
      const autorEncontrado = autores.find(
        (a) => a.nome.toLowerCase().includes(result.autor!.toLowerCase())
      );
      if (autorEncontrado) {
        setValue('autorId', autorEncontrado.id);
        toast.success(`Autor existente: ${autorEncontrado.nome}`);
      } else {
        setValue('nomeAutor', result.autor);
        toast.info(`Novo autor detectado: ${result.autor}`);
      }
    }
  };

  const handleOcrError = (error: string) => {
    toast.error(error);
  };
  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchQuery.trim()) return;

    try {
      setSearching(true);
      const results = await livrosService.searchExternal(searchQuery);
      console.log('Search results:', results);
      setSearchResults(results);
    } catch (error) {
      toast.error('Erro ao pesquisar livros.');
    } finally {
      setSearching(false);
    }
  };

  const handleImportBook = (book: GoogleBook) => {
    setValue('titulo', book.title);
    if (book.isbn) setValue('isbn', book.isbn);
    else setValue('isbn', ''); // Clear if no ISBN, forcing user to input or generate

    if (book.pageCount) setValue('qtdPaginas', book.pageCount);
    if (book.description) setValue('sinopse', book.description);
    if (book.publisher) setValue('nomeEditora', book.publisher);
    if (book.publishedYear) setValue('anoPublicacao', book.publishedYear);

    if (book.thumbnailUrl) {
      setValue('capaURL', book.thumbnailUrl);
      setCapaMode('url');
    }

    // Handle Author
    if (book.author) {
      const autorEncontrado = autores.find(a => a.nome.toLowerCase() === book.author!.toLowerCase());
      if (autorEncontrado) {
        setValue('autorId', autorEncontrado.id);
      } else {
        setValue('nomeAutor', book.author);
      }
    }

    // Handle Category (Approximation)
    if (book.category) {
      const categoriaEncontrada = categorias.find(c => c.nome.toLowerCase() === book.category!.toLowerCase());
      if (categoriaEncontrada) {
        setValue('categoriaId', categoriaEncontrada.id);
      } else {
        toast.info(`Categoria "${book.category}" não encontrada. Selecione manualmente.`);
      }
    }

    setActiveTab('manual');
    toast.success('Livro importado! Verifique e complete os dados.');
  };

  const onSubmit = async (data: LivroFormSchema) => {
    try {
      setLoading(true);
      let capaURL = data.capaURL;
      if (capaMode === 'upload' && selectedFile) {
        // Implementar uploadCapa real se necessário, por agora assume null ou URL
        // capaURL = await livrosService.uploadCapa(selectedFile);
      }

      await livrosService.createLivro({
        titulo: data.titulo,
        isbn: data.isbn,
        autorId: data.autorId,
        nomeAutor: data.nomeAutor,
        categoriaId: data.categoriaId,
        qtdPaginas: data.qtdPaginas || 0,
        qtdTotal: data.qtdTotal,
        localizacao: data.localizacao || undefined,
        capaURL: capaURL || undefined,
        nomeEditora: data.nomeEditora,
        anoPublicacao: data.anoPublicacao,
        sinopse: data.sinopse
      });
      toast.success('Livro cadastrado com sucesso!');
      navigate(ROUTES.ADMIN_LIVROS);
    } catch (error: any) {
      console.error(error);
      toast.error(error.response?.data?.message || error.message || 'Erro ao salvar');
    } finally {
      setLoading(false);
    }
  };

  return (
    <AdminLayout>
      <div className="space-y-6 max-w-4xl">
        <div className="flex items-center gap-4 animate-slide-up">
          <button
            onClick={() => navigate(ROUTES.ADMIN_LIVROS)}
            className="p-2 rounded-lg text-gray-400 hover:text-gray-700 hover:bg-gray-100 transition-all duration-200"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div>
            <h1 className="text-2xl font-extrabold text-gray-900 tracking-tight">Cadastrar Livro</h1>
            <p className="text-sm text-gray-400 mt-0.5 font-medium">Adicione um novo livro ao acervo</p>
          </div>
        </div>

        {/* Tabs */}
        <div className="flex border-b border-gray-200 animate-slide-up">
          <button
            className={`py-2 px-4 text-sm font-medium border-b-2 transition-colors ${activeTab === 'manual'
              ? 'border-primary-600 text-primary-600'
              : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}
            onClick={() => setActiveTab('manual')}
          >
            Cadastro Manual / OCR
          </button>
          <button
            className={`py-2 px-4 text-sm font-medium border-b-2 transition-colors ${activeTab === 'search'
              ? 'border-primary-600 text-primary-600'
              : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}
            onClick={() => setActiveTab('search')}
          >
            Pesquisar Online
          </button>
        </div>

        {activeTab === 'search' ? (
          <div className="space-y-6 animate-slide-up">
            <Card padding="lg">
              <form onSubmit={handleSearch} className="flex gap-2">
                <Input
                  placeholder="Digite o título, autor ou ISBN..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="flex-1"
                />
                <Button type="submit" loading={searching}>
                  <Search className="w-4 h-4 mr-2" />
                  Pesquisar
                </Button>
              </form>
            </Card>

            <div className="grid grid-cols-1 gap-4">
              {searchResults.map((book, index) => (
                <Card key={index} padding="md" className="flex gap-4 items-start">
                  {book.thumbnailUrl && <img src={book.thumbnailUrl} alt={book.title} className="w-16 h-24 object-cover rounded shadow" />}
                  <div className="flex-1">
                    <h3 className="font-bold text-gray-900">{book.title}</h3>
                    <p className="text-sm text-gray-600">{book.author} - {book.publishedYear}</p>
                    {book.isbn && <p className="text-xs text-gray-500">ISBN: {book.isbn}</p>}
                  </div>
                  <Button size="sm" variant="outline" onClick={() => handleImportBook(book)}>
                    <Download className="w-4 h-4 mr-1" />
                    Importar
                  </Button>
                </Card>
              ))}
              {searchResults.length === 0 && !searching && searchQuery && (
                <p className="text-center text-gray-500">Nenhum livro encontrado.</p>
              )}
            </div>
          </div>
        ) : (
          <>
            {/* OCR Upload Component */}
            <div className="animate-slide-up">
              <ImageUploadOcr
                onOcrComplete={handleOcrComplete}
                onError={handleOcrError}
              />
              {/* Global Confidence Badge */}
              {ocrResult && (
                <div className="mt-2 flex items-center gap-2">
                  <span className="text-xs font-semibold text-gray-500">Confiança da Extração:</span>
                  <div className={`text-xs px-2 py-0.5 rounded-full font-bold ${ocrResult.confianca > 80 ? 'bg-green-100 text-green-700' :
                    ocrResult.confianca > 50 ? 'bg-yellow-100 text-yellow-700' : 'bg-red-100 text-red-700'
                    }`}>
                    {ocrResult.confianca}% ({ocrResult.mensagem})
                  </div>
                </div>
              )}
            </div>

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
              <Card padding="lg" className="animate-slide-up stagger-1">
                <h2 className="text-sm font-bold text-gray-900 uppercase tracking-wider mb-5">
                  Informações Básicas
                </h2>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                  <div className="md:col-span-2">
                    <Input
                      label="Título"
                      placeholder="Ex: Clean Code"
                      error={errors.titulo?.message}
                      {...register('titulo')}
                    />
                  </div>

                  <Input
                    label="ISBN"
                    placeholder="Ex: 978-0132350884"
                    error={errors.isbn?.message}
                    {...register('isbn')}
                  />

                  <Input
                    label="Páginas"
                    type="number"
                    placeholder="Ex: 464"
                    error={errors.qtdPaginas?.message}
                    {...register('qtdPaginas', { valueAsNumber: true })}
                  />

                  <div>
                    <label className="block text-xs font-semibold uppercase tracking-wider text-gray-500 mb-1.5">
                      Autor
                    </label>
                    <div className="space-y-3">
                      <select
                        className={`w-full px-4 py-2.5 border rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500 hover:border-gray-300 focus:outline-none transition-all duration-200 ${errors.autorId ? 'border-red-300' : 'border-gray-200'}`}
                        {...register('autorId')}
                      >
                        <option value="">Selecione um autor da lista</option>
                        {autores.map(a => (
                          <option key={a.id} value={a.id}>{a.nome}</option>
                        ))}
                      </select>

                      <div className="relative py-1">
                        <div className="absolute inset-0 flex items-center" aria-hidden="true">
                          <div className="w-full border-t border-gray-200" />
                        </div>
                        <div className="relative flex justify-center">
                          <span className="bg-white px-2 text-xs text-gray-400 font-medium tracking-wide">OU NOVO AUTOR</span>
                        </div>
                      </div>

                      <Input
                        placeholder="Nome do autor (caso não exista acima)"
                        error={errors.nomeAutor?.message}
                        {...register('nomeAutor')}
                      />
                    </div>
                    {errors.autorId && !watch('nomeAutor') && (
                      <p className="mt-1.5 text-xs font-medium text-error-500 animate-slide-up">{errors.autorId.message}</p>
                    )}
                  </div>

                  <div>
                    <label className="block text-xs font-semibold uppercase tracking-wider text-gray-500 mb-1.5">
                      Categoria
                    </label>
                    <select
                      className={`w-full px-4 py-2.5 border rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500 hover:border-gray-300 focus:outline-none transition-all duration-200 ${errors.categoriaId ? 'border-red-300' : 'border-gray-200'}`}
                      {...register('categoriaId')}
                    >
                      <option value="">Selecione uma categoria</option>
                      {categorias.map(c => (
                        <option key={c.id} value={c.id}>{c.nome}</option>
                      ))}
                    </select>
                    {errors.categoriaId && (
                      <p className="mt-1.5 text-xs font-medium text-error-500 animate-slide-up">{errors.categoriaId.message}</p>
                    )}
                  </div>

                  <Input
                    label="Editora"
                    placeholder="Ex: Prentice Hall"
                    error={errors.nomeEditora?.message}
                    {...register('nomeEditora')}
                  />

                  <Input
                    label="Ano de Publicação"
                    type="number"
                    placeholder="Ex: 2008"
                    error={errors.anoPublicacao?.message}
                    {...register('anoPublicacao', { valueAsNumber: true })}
                  />

                  <div className="md:col-span-2">
                    <label className="block text-xs font-semibold uppercase tracking-wider text-gray-500 mb-1.5">
                      Sinopse
                    </label>
                    <textarea
                      rows={4}
                      className="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500 hover:border-gray-300 focus:outline-none transition-all duration-200 resize-y"
                      placeholder="Resumo do livro..."
                      {...register('sinopse')}
                    />
                  </div>
                </div>
              </Card>

              <Card padding="lg" className="animate-slide-up stagger-2">
                <h2 className="text-sm font-bold text-gray-900 uppercase tracking-wider mb-5">
                  Disponibilidade
                </h2>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                  <Input
                    label="Quantidade"
                    type="number"
                    min="1"
                    placeholder="Ex: 5"
                    error={errors.qtdTotal?.message}
                    {...register('qtdTotal', { valueAsNumber: true })}
                  />

                  <Input
                    label="Localização"
                    placeholder="Ex: Corredor A, Prateleira 3"
                    error={errors.localizacao?.message}
                    {...register('localizacao')}
                  />
                </div>
              </Card>

              <Card padding="lg" className="animate-slide-up stagger-3">
                <h2 className="text-sm font-bold text-gray-900 uppercase tracking-wider mb-5">
                  Capa do Livro
                </h2>

                {/* Radio buttons for mode selection */}
                <div className="flex gap-4 mb-5">
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="radio"
                      name="capaMode"
                      checked={capaMode === 'url'}
                      onChange={() => handleCapaModeChange('url')}
                      className="w-4 h-4 text-primary-600 focus:ring-primary-500"
                    />
                    <LinkIcon className="w-4 h-4 text-gray-500" />
                    <span className="text-sm font-medium text-gray-700">URL</span>
                  </label>

                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="radio"
                      name="capaMode"
                      checked={capaMode === 'upload'}
                      onChange={() => handleCapaModeChange('upload')}
                      className="w-4 h-4 text-primary-600 focus:ring-primary-500"
                    />
                    <Upload className="w-4 h-4 text-gray-500" />
                    <span className="text-sm font-medium text-gray-700">Upload</span>
                  </label>
                </div>

                {/* Conditional input based on mode */}
                {capaMode === 'url' ? (
                  <Input
                    label="URL da Capa"
                    placeholder="https://exemplo.com/capa.jpg"
                    error={errors.capaURL?.message}
                    {...register('capaURL')}
                  />
                ) : (
                  <div>
                    <label className="block text-xs font-semibold uppercase tracking-wider text-gray-500 mb-1.5">
                      Arquivo da Capa
                    </label>
                    <input
                      ref={fileInputRef}
                      type="file"
                      accept="image/*"
                      onChange={handleFileChange}
                      className="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500 hover:border-gray-300 focus:outline-none transition-all duration-200 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-primary-50 file:text-primary-700 hover:file:bg-primary-100"
                    />
                    <p className="mt-1.5 text-xs text-gray-500">Formatos: JPG, PNG, WebP (máx. 5MB)</p>
                  </div>
                )}

                {/* Preview */}
                {preview && (
                  <div className="mt-5">
                    <label className="block text-xs font-semibold uppercase tracking-wider text-gray-500 mb-2">
                      Preview
                    </label>
                    <div className="relative w-full max-w-xs mx-auto">
                      <img
                        src={preview}
                        alt="Preview da capa"
                        className="w-full h-auto rounded-lg shadow-md border border-gray-200"
                        onError={() => {
                          setPreview('');
                          toast.error('Erro ao carregar preview da imagem');
                        }}
                      />
                    </div>
                  </div>
                )}
              </Card>

              <div className="flex justify-end gap-3 animate-slide-up stagger-4 pb-4">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => navigate(ROUTES.ADMIN_LIVROS)}
                >
                  Cancelar
                </Button>
                <Button
                  type="submit"
                  variant="primary"
                  loading={loading}
                >
                  <Save className="w-4 h-4" />
                  Cadastrar Livro
                </Button>
              </div>
            </form>
          </>
        )}
      </div>
    </AdminLayout >
  );
};

export default CadastroLivro;
