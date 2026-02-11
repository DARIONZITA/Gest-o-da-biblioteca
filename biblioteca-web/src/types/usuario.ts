export enum PerfilUsuario {
  ADMIN = 'ADMIN',
  MEMBER = 'MEMBER',
}

export enum StatusUsuario {
  ATIVO = 'ATIVO',
  BLOQUEADO = 'BLOQUEADO',
}

/** Corresponde ao UsuarioResponseDTO do backend */
export interface Usuario {
  id: string;
  matricula: number;
  nome: string;
  email: string;
  perfil: PerfilUsuario;
  status: StatusUsuario;
  dataCadastro: string;
}

/** Dados retornados pelo POST /auth/login */
export interface UsuarioAuth {
  id: string;
  nome: string;
  email: string;
  perfil: PerfilUsuario;
  token: string;
}

/** Payload para POST /usuarios */
export interface UsuarioRequestData {
  matricula: number;
  nome: string;
  email: string;
  senha: string;
  perfil: PerfilUsuario;
  status: StatusUsuario;
}
