export interface OcrResponse {
  textoCompleto: string;
  titulo: string | null;
  autor: string | null;
  isbn: string | null;
  editora: string | null;
  ano: number | null;
  confianca: number;
  qtdPaginas?: number;
  sinopse?: string;
  capaUrl?: string;
  mensagem: string;
  sucesso: boolean;
}

export interface OcrUploadState {
  isProcessing: boolean;
  progress: number;
  error: string | null;
  result: OcrResponse | null;
}
