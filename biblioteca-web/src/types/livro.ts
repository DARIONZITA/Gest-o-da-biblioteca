/** Corresponde ao LivroResponseDTO do backend */
export interface Livro {
  id: string;
  titulo: string;
  isbn: string;
  nomeAutor: string;
  nomeCategoria: string;
  nomeEditora?: string;
  anoPublicacao?: number;
  qtdPaginas: number;
  qtdTotal: number;
  qtdDisponivel: number;
  localizacao?: string;
  capaURL?: string;
  sinopse?: string;
}

/** Payload para POST /livros (LivroRequestDTO do backend) */
export interface LivroFormData {
  titulo: string;
  isbn: string;
  qtdPaginas: number;
  autorId?: string; // Trocado para optional
  nomeAutor?: string; // Novo campo
  categoriaId: string;
  nomeEditora?: string; // Novo campo
  anoPublicacao?: number; // Novo campo
  qtdTotal: number;
  localizacao?: string;
  capaURL?: string;
  sinopse?: string;
}

export interface GoogleBook {
  title: string;
  author?: string;
  publisher?: string;
  publishedYear?: number;
  pageCount?: number;
  description?: string;
  category?: string;
  isbn?: string;
  thumbnailUrl?: string;
}
