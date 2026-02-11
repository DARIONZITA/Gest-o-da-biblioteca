/** Corresponde ao AutorResponseDTO do backend */
export interface Autor {
  id: string;
  nome: string;
  descricao?: string;
  dataCadastro?: string;
}

/** Payload para POST /autores */
export interface AutorFormData {
  nome: string;
  descricao: string;
}
