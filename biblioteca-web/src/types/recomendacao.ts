export interface Recomendacao {
  id: string;
  titulo: string;
  isbn: string;
  nomeAutor: string;
  nomeCategoria: string;
  qtdPaginas: number;
  qtdDisponivel: number;
  localizacao: string;
  capaURL?: string;
  sinopse?: string;
  
  // Campos específicos de recomendação
  scoreGeral: number;
  scoreColaborativo: number;
  scoreConteudo: number;
  scorePopularidade: number;
  motivoRecomendacao: string;
  confianca: number; // 0-100
}

// Alias para compatibilidade com backend
export type RecomendacaoResponseDTO = Recomendacao;
