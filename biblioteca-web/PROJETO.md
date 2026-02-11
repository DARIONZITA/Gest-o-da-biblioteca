# 📚 Sistema de Gestão de Biblioteca - ISPTEC

Sistema web administrativo para gestão de biblioteca institucional, desenvolvido com React, TypeScript e Tailwind CSS.

## ✨ Funcionalidades Implementadas

### ✅ Infraestrutura Base
- ✅ Configuração completa do projeto (Vite + React + TypeScript)
- ✅ Tailwind CSS com Design System customizado seguindo guia UI/UX
- ✅ Estrutura de pastas organizada e escalável
- ✅ Path aliases (`@/`) configurados
- ✅ Variáveis de ambiente

### ✅ Autenticação
- ✅ Sistema completo de autenticação com Context API
- ✅ Login de administradores
- ✅ Proteção de rotas privadas
- ✅ Persistência de sessão (localStorage)
- ✅ Interceptors para token JWT

### ✅ Design System
- ✅ Componentes reutilizáveis:
  - Button (4 variantes: primary, secondary, outline, danger)
  - Input (com label, erro, ícones)
  - Card (múltiplos tamanhos de padding e sombra)
  - Badge (5 variantes: success, warning, error, info, neutral)
  - EmptyState
  - StatCard (para dashboard)
- ✅ Sistema de notificações toast (React Hot Toast)
- ✅ Paleta de cores institucional ISPTEC

### ✅ Layout Administrativo
- ✅ Sidebar com navegação
- ✅ Topbar com informações do usuário
- ✅ Layout responsivo

### ✅ Páginas
- ✅ **Login**: Autenticação de administradores
- ✅ **Dashboard**: 
  - Cards de estatísticas (Total de Livros, Usuários, Empréstimos, Reservas)
  - Seção de ações pendentes
  - Top 5 livros mais emprestados
  - Placeholder para gráficos
- ✅ **Lista de Livros**:
  - Tabela completa com todos os livros
  - Busca em tempo real
  - Badges de disponibilidade
  - Ações de editar e excluir
- ✅ **Cadastro de Livro**:
  - Formulário completo com validação (React Hook Form + Zod)
  - Campos: título, autor, ISBN, categoria, editora, ano, páginas, quantidade, localização, sinopse

## 🚀 Como Executar

### Pré-requisitos
- Node.js 18+ instalado
- npm ou yarn

### Instalação

1. Entre na pasta do projeto
```bash
cd biblioteca-web
```

2. As dependências já estão instaladas. Se necessário:
```bash
npm install
```

3. Configure as variáveis de ambiente
O arquivo `.env.local` já está criado com:
```env
VITE_API_URL=http://localhost:3000/api
```

4. O servidor já está rodando em:
```
http://localhost:5173
```

## 📦 Scripts Disponíveis

```bash
npm run dev      # Inicia servidor de desenvolvimento
npm run build    # Build de produção
npm run preview  # Preview do build
npm run lint     # Verifica código com ESLint
```

## 🎨 Design System

### Cores Principais
- **Primária (Azul ISPTEC)**: `#1E3A8A`
- **Secundária (Laranja)**: `#F97316`
- **Sucesso**: `#10B981`
- **Aviso**: `#F59E0B`
- **Erro**: `#EF4444`

### Tipografia
- **Fonte**: Inter (Google Fonts)
- **Tamanhos**: 12px, 14px, 16px, 18px, 20px, 24px, 30px, 36px
- **Pesos**: 400 (regular), 500 (medium), 600 (semibold), 700 (bold)

### Espaçamento
Sistema de 8px: 4px, 8px, 12px, 16px, 20px, 24px, 32px, 40px, 48px, 64px

## 📁 Estrutura do Projeto

```
src/
├── components/       # Componentes reutilizáveis
│   ├── Badge.tsx
│   ├── Button.tsx
│   ├── Card.tsx
│   ├── EmptyState.tsx
│   ├── Input.tsx
│   ├── PrivateRoute.tsx
│   ├── Sidebar.tsx
│   ├── StatCard.tsx
│   └── Topbar.tsx
├── constants/        # Constantes da aplicação
│   └── index.ts
├── contexts/         # Context API
│   └── AuthContext.tsx
├── hooks/            # Custom hooks
│   └── useAuth.ts
├── layouts/          # Layouts da aplicação
│   └── AdminLayout.tsx
├── pages/            # Páginas da aplicação
│   ├── Dashboard.tsx
│   ├── Login.tsx
│   └── Livros/
│       ├── CadastroLivro.tsx
│       └── ListaLivros.tsx
├── router/           # Configuração de rotas
│   └── index.tsx
├── services/         # Serviços de API
│   ├── api.ts
│   ├── authService.ts
│   └── livrosService.ts
├── types/            # TypeScript interfaces
│   ├── api.ts
│   ├── emprestimo.ts
│   ├── livro.ts
│   ├── reserva.ts
│   └── usuario.ts
├── utils/            # Utilitários
│   └── toast.ts
├── App.tsx
├── main.tsx
└── index.css
```

## 🔒 Autenticação

O sistema usa JWT (JSON Web Tokens) para autenticação. O token é armazenado no localStorage e adicionado automaticamente em todas as requisições ao backend via interceptor do Axios.

### Fluxo de Autenticação
1. Usuário entra com matrícula e senha
2. Backend valida credenciais e retorna token JWT
3. Token é armazenado no localStorage
4. Token é enviado em todas as requisições subsequentes
5. Se token expirar, usuário é redirecionado para login

## 🔄 Próximos Passos (Roadmap)

### Funcionalidades Pendentes
- [ ] Página de Edição de Livros
- [ ] Gestão de Usuários (lista, detalhes, bloquear/desbloquear)
- [ ] Gestão de Empréstimos (realizar, devolver, histórico)
- [ ] Gestão de Reservas (fila de espera)
- [ ] Relatórios com gráficos (Recharts)
- [ ] Exportação de dados (CSV/PDF)
- [ ] Cadastro de Livro com OCR (futuro)
- [ ] Busca avançada com filtros
- [ ] Paginação real na lista de livros
- [ ] Modo escuro (dark mode)

## 🛠️ Tecnologias Utilizadas

- **React 19** - Biblioteca UI
- **TypeScript** - Tipagem estática
- **Vite** - Build tool e dev server
- **Tailwind CSS** - Framework CSS utilitário
- **React Router DOM** - Roteamento
- **React Hook Form** - Gerenciamento de formulários
- **Zod** - Validação de schemas
- **Axios** - Cliente HTTP
- **React Hot Toast** - Notificações toast
- **Lucide React** - Ícones
- **date-fns** - Manipulação de datas
- **TanStack Table** - Tabelas avançadas (futuro)
- **Recharts** - Gráficos (futuro)

## 📝 Observações

### Backend
Este projeto é apenas o frontend. É necessário ter um backend compatível rodando na URL configurada em `.env.local`. O backend deve fornecer os seguintes endpoints:

- `POST /auth/login` - Autenticação
- `GET /livros` - Lista de livros (com paginação)
- `GET /livros/:id` - Detalhes de um livro
- `POST /livros` - Criar livro
- `PUT /livros/:id` - Atualizar livro
- `DELETE /livros/:id` - Excluir livro

### Dados Mock
Atualmente, o sistema usa dados mockados (fake data) para demonstração. Quando o backend estiver disponível, basta descomentar as chamadas aos services.

### QR Code
**IMPORTANTE**: Conforme solicitado, a funcionalidade de QR Code para identificação na biblioteca foi **excluída** do projeto.

## 📄 Status da Implementação

✅ **Fase 1: Setup e Fundação** - COMPLETO
- Dependências instaladas
- Tailwind configurado com design system
- Estrutura de pastas criada
- Constants e types definidos

✅ **Fase 2: Autenticação e API** - COMPLETO
- API service configurado
- AuthContext implementado
- Sistema de rotas com proteção

✅ **Fase 3: Design System** - COMPLETO
- Todos os componentes base criados
- Layout administrativo completo

✅ **Fase 4: Páginas Principais** - COMPLETO (básico)
- Login funcional
- Dashboard com estatísticas
- Lista de livros com busca
- Cadastro de livro com validação

🔄 **Próximas Fases**:
- Fase 5: Gestão de Empréstimos
- Fase 6: Gestão de Usuários
- Fase 7: Relatórios e Gráficos
- Fase 8: Funcionalidades Avançadas

---

**Desenvolvido para o ISPTEC** 🎓
