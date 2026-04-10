# 📚 Sistema de Gestão de Biblioteca - ISPTEC

Sistema completo de gestão de biblioteca institucional desenvolvido para o ISPTEC, composto por um **backend em Spring Boot** e um **frontend web administrativo em React**.

---

## 🗂️ Estrutura do Repositório

```
Gest-o-da-biblioteca/
├── GestaoBiblioteca/     # Backend - API REST (Spring Boot + Java 17)
├── biblioteca-web/        # Frontend - Painel Administrativo (React + TypeScript)
└── Documentação/          # Documentação do projeto (Elaboração, Construção, Transição)
```

---

## 🚀 Tecnologias Utilizadas

### Backend (`GestaoBiblioteca`)
- **Java 17**
- **Spring Boot 3.4** (Web, Data JPA, Security, Validation, Cache, WebFlux)
- **MySQL** — base de dados relacional
- **JWT (JJWT 0.12)** — autenticação stateless
- **Springdoc OpenAPI / Swagger UI** — documentação interativa da API
- **Tess4J (Tesseract OCR)** — extração de texto de imagens
- **Groq API (LLaMA)** — chatbot de inteligência artificial
- **Google Books API** — pesquisa de livros externos
- **Gemini API** — OCR adicional
- **Caffeine Cache** — sistema de cache para recomendações
- **Lombok** — redução de boilerplate

### Frontend (`biblioteca-web`)
- **React 19** + **TypeScript**
- **Vite** — build tool e servidor de desenvolvimento
- **Tailwind CSS** — framework CSS utilitário
- **React Router DOM** — roteamento SPA
- **React Hook Form** + **Zod** — formulários e validação
- **Axios** — cliente HTTP com interceptors JWT
- **Recharts** — gráficos e visualizações
- **TanStack Table** — tabelas avançadas
- **Lucide React** — ícones
- **jsPDF** — exportação de relatórios em PDF
- **React Hot Toast** — notificações toast

---

## ⚙️ Pré-requisitos

### Backend
- Java 17+
- Maven 3.9+
- MySQL 8+

### Frontend
- Node.js 18+
- npm

---

## 🔧 Configuração e Execução

### 1. Backend

```bash
cd GestaoBiblioteca
```

Configure as variáveis de ambiente (ou edite `src/main/resources/application.properties`):

| Variável | Descrição | Padrão |
|---|---|---|
| `JWT_SECRET` | Chave secreta JWT (≥ 32 bytes) | `dev-secret-change-me-please-use-env-32bytes` |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas (CORS) | `http://localhost:5173` |
| `GROQ_API_KEY` | Chave da API Groq (chatbot) | — |
| `GEMINI_API_KEY` | Chave da API Gemini (OCR) | — |
| `GOOGLE_BOOKS_API_KEY` | Chave da API Google Books | — |
| `SEED_ADMIN_ENABLED` | Ativar seed do admin | `true` |
| `SEED_ADMIN_MATRICULA` | Matrícula do admin inicial | `20121101` |
| `SEED_ADMIN_SENHA` | Senha do admin inicial | `admin123` |
| `SEED_DEMO_ENABLED` | Ativar dados de demonstração | `true` |

Configure a base de dados em `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lib_db?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=SUA_SENHA
```

Inicie o servidor:
```bash
./mvnw spring-boot:run
```

O backend ficará disponível em: **http://localhost:8080/api**  
Swagger UI: **http://localhost:8080/api/swagger-ui.html**

---

### 2. Frontend

```bash
cd biblioteca-web
```

Instale as dependências:
```bash
npm install
```

Configure as variáveis de ambiente:
```bash
cp .env.example .env.local
```

Edite `.env.local`:
```env
VITE_API_URL=http://localhost:8080/api
```

Inicie o servidor de desenvolvimento:
```bash
npm run dev
```

O frontend ficará disponível em: **http://localhost:5173**

---

## 🧪 Modo Mock (Frontend sem Backend)

O frontend pode funcionar de forma independente usando dados simulados (mock). Para ativar/desativar, edite `src/config/mock.ts`:

```typescript
export const MOCK_ENABLED = true; // true = mock | false = API real
```

### Credenciais de teste (modo mock)

| Perfil | Matrícula | Senha |
|---|---|---|
| Administrador | `20230001` | `admin123` |
| Estudante | `20230002` | `estudante123` |
| Professor | `20230003` | `professor123` |

---

## 📋 Funcionalidades

### ✅ Implementadas
- Autenticação com JWT (login, logout, proteção de rotas)
- Dashboard com estatísticas e gráficos
- Gestão de Livros (listar, cadastrar, editar, excluir, buscar)
- Gestão de Categorias e Autores
- Gestão de Usuários
- Gestão de Empréstimos (realizar, devolver, renovar, histórico)
- Gestão de Reservas
- Relatórios exportáveis em PDF
- Chatbot assistente (integração Groq/LLaMA)
- OCR para extração de dados de livros a partir de imagens (Tesseract + Gemini)
- Sistema de recomendações de livros (com cache)
- Busca de livros via Google Books API
- Swagger UI para documentação e teste dos endpoints

### ⏳ Pendentes (Frontend)
- Gráficos no Dashboard (Recharts)
- Paginação real com TanStack Table
- Modo escuro
- Testes automatizados (Jest + React Testing Library)

---

## 🔌 Endpoints Principais da API

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/auth/login` | Login (retorna token JWT) |
| `GET` | `/api/livros` | Listar livros (paginado, com filtros) |
| `POST` | `/api/livros` | Cadastrar livro |
| `PUT` | `/api/livros/{id}` | Atualizar livro |
| `DELETE` | `/api/livros/{id}` | Excluir livro |
| `GET` | `/api/usuarios` | Listar usuários |
| `GET` | `/api/emprestimos` | Listar empréstimos |
| `POST` | `/api/emprestimos` | Realizar empréstimo |
| `PUT` | `/api/emprestimos/{id}/devolver` | Devolver livro |
| `GET` | `/api/reservas` | Listar reservas |
| `POST` | `/api/reservas` | Criar reserva |
| `POST` | `/api/ocr/upload` | Extrair dados de livro via OCR |
| `POST` | `/api/chatbot/mensagem` | Enviar mensagem ao chatbot |
| `GET` | `/api/recomendacoes` | Obter recomendações de livros |
| `GET` | `/api/relatorios` | Gerar relatório |

> Consulte o Swagger UI para a documentação completa de todos os endpoints.

---

## 🎨 Design System

| Elemento | Valor |
|---|---|
| Cor primária (Azul ISPTEC) | `#1E3A8A` |
| Cor secundária (Laranja) | `#F97316` |
| Sucesso | `#10B981` |
| Aviso | `#F59E0B` |
| Erro | `#EF4444` |
| Fonte | Inter (Google Fonts) |

---

## 📦 Scripts Disponíveis

### Frontend
```bash
npm run dev       # Servidor de desenvolvimento
npm run build     # Build de produção
npm run preview   # Preview do build
npm run lint      # Lint com ESLint
```

### Backend
```bash
./mvnw spring-boot:run          # Iniciar em modo desenvolvimento
./mvnw clean package            # Gerar JAR de produção
./mvnw test                     # Executar testes
```

---

## 📄 Documentação Adicional

- [`biblioteca-web/PROJETO.md`](biblioteca-web/PROJETO.md) — Visão geral do frontend
- [`biblioteca-web/IMPLEMENTACAO.md`](biblioteca-web/IMPLEMENTACAO.md) — Detalhes técnicos do frontend
- [`biblioteca-web/INTEGRACAO.md`](biblioteca-web/INTEGRACAO.md) — Guia de integração frontend ↔ backend
- [`biblioteca-web/CREDENCIAIS.md`](biblioteca-web/CREDENCIAIS.md) — Credenciais de teste (modo mock)

---

**Desenvolvido para o ISPTEC** 🎓
