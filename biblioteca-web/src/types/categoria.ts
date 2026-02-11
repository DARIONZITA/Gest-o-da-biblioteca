/** Corresponde ao CategoriaResponseDTO do backend */
export interface Categoria {
  id: string;
  nome: string;
  descricao?: string;
}

/** Payload para POST /categorias */
export interface CategoriaFormData {
  nome: string;
  descricao?: string;
}
