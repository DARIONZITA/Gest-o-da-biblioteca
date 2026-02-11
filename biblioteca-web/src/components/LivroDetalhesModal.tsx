import React from 'react';
import { X, BookOpen, User, Tag, MapPin, FileText, Hash } from 'lucide-react';
import Badge from './Badge';
import Button from './Button';
import { API_URL } from '@/constants';

interface LivroDetalhesModalProps {
  livro: {
    id: string;
    titulo: string;
    nomeAutor: string;
    nomeCategoria: string;
    isbn: string;
    qtdPaginas?: number;
    qtdDisponivel: number;
    localizacao?: string;
    capaURL?: string;
    sinopse?: string;
  };
  isOpen: boolean;
  onClose: () => void;
  onSolicitarEmprestimo?: () => void;
  onReservar?: () => void;
  loadingSolicitar?: boolean;
  loadingReservar?: boolean;
}

const LivroDetalhesModal: React.FC<LivroDetalhesModalProps> = ({
  livro,
  isOpen,
  onClose,
  onSolicitarEmprestimo,
  onReservar,
  loadingSolicitar = false,
  loadingReservar = false,
}) => {
  if (!isOpen) return null;

  const imagemUrl = livro.capaURL 
    ? new URL(livro.capaURL, API_URL).toString()
    : null;

  return (
    <>
      {/* Overlay */}
      <div
        className="fixed inset-0 bg-black bg-opacity-50 z-40 animate-fade-in"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto animate-scale-in">
          {/* Header */}
          <div className="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
            <h2 className="text-xl font-bold text-gray-900">Detalhes do Livro</h2>
            <button
              onClick={onClose}
              className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <X className="w-5 h-5 text-gray-500" />
            </button>
          </div>

          {/* Content */}
          <div className="p-6">
            <div className="flex flex-col md:flex-row gap-6">
              {/* Capa do Livro */}
              <div className="flex-shrink-0">
                {imagemUrl ? (
                  <div className="w-48 h-64 rounded-lg overflow-hidden border border-gray-200 shadow-md">
                    <img
                      src={imagemUrl}
                      alt={livro.titulo}
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        e.currentTarget.style.display = 'none';
                        const parent = e.currentTarget.parentElement;
                        if (parent) {
                          parent.innerHTML = '<div class="w-full h-full flex items-center justify-center bg-gray-100"><svg class="w-16 h-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path></svg></div>';
                        }
                      }}
                    />
                  </div>
                ) : (
                  <div className="w-48 h-64 bg-gray-100 rounded-lg border border-gray-200 flex items-center justify-center">
                    <BookOpen className="w-16 h-16 text-gray-400" />
                  </div>
                )}
              </div>

              {/* Informações */}
              <div className="flex-grow space-y-4">
                {/* Título e Badge de Disponibilidade */}
                <div>
                  <h3 className="text-2xl font-bold text-gray-900 mb-2">
                    {livro.titulo}
                  </h3>
                  <Badge
                    variant={livro.qtdDisponivel > 0 ? 'success' : 'error'}
                    className="text-sm"
                  >
                    {livro.qtdDisponivel > 0
                      ? `${livro.qtdDisponivel} ${livro.qtdDisponivel === 1 ? 'unidade disponível' : 'unidades disponíveis'}`
                      : 'Indisponível'}
                  </Badge>
                </div>

                {/* Autor */}
                <div className="flex items-center gap-2 text-gray-700">
                  <User className="w-5 h-5 text-gray-400" />
                  <span className="font-medium">Autor:</span>
                  <span>{livro.nomeAutor}</span>
                </div>

                {/* Categoria */}
                <div className="flex items-center gap-2 text-gray-700">
                  <Tag className="w-5 h-5 text-gray-400" />
                  <span className="font-medium">Categoria:</span>
                  <Badge variant="info">{livro.nomeCategoria}</Badge>
                </div>

                {/* ISBN */}
                <div className="flex items-center gap-2 text-gray-700">
                  <Hash className="w-5 h-5 text-gray-400" />
                  <span className="font-medium">ISBN:</span>
                  <code className="px-2 py-1 bg-gray-100 rounded text-sm font-mono">
                    {livro.isbn}
                  </code>
                </div>

                {/* Páginas */}
                {livro.qtdPaginas && (
                  <div className="flex items-center gap-2 text-gray-700">
                    <FileText className="w-5 h-5 text-gray-400" />
                    <span className="font-medium">Páginas:</span>
                    <span>{livro.qtdPaginas}</span>
                  </div>
                )}

                {/* Localização */}
                {livro.localizacao && (
                  <div className="flex items-center gap-2 text-gray-700">
                    <MapPin className="w-5 h-5 text-gray-400" />
                    <span className="font-medium">Localização:</span>
                    <span>{livro.localizacao}</span>
                  </div>
                )}
              </div>
            </div>

            {/* Sinopse */}
            {livro.sinopse && (
              <div className="mt-6 pt-6 border-t border-gray-200">
                <h4 className="text-lg font-semibold text-gray-900 mb-3 flex items-center gap-2">
                  <BookOpen className="w-5 h-5" />
                  Sinopse
                </h4>
                <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                  {livro.sinopse}
                </p>
              </div>
            )}
          </div>

          {/* Footer com Botões de Ação */}
          <div className="sticky bottom-0 bg-gray-50 border-t border-gray-200 px-6 py-4 flex gap-3">
            {livro.qtdDisponivel > 0 ? (
              <Button
                variant="primary"
                className="flex-1"
                onClick={onSolicitarEmprestimo}
                disabled={loadingSolicitar}
              >
                {loadingSolicitar ? 'Solicitando...' : 'Solicitar Empréstimo'}
              </Button>
            ) : (
              <Button
                variant="secondary"
                className="flex-1"
                onClick={onReservar}
                disabled={loadingReservar}
              >
                {loadingReservar ? 'Reservando...' : 'Reservar Livro'}
              </Button>
            )}
            <Button variant="outline" onClick={onClose}>
              Fechar
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};

export default LivroDetalhesModal;
