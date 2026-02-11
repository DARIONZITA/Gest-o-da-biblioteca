# 🔌 Guia de Integração com Backend

Este documento explica como conectar o frontend ao backend quando estiver disponível.

## 📋 Pré-requisitos do Backend

O backend deve implementar os seguintes endpoints:

### Autenticação
```
POST /api/auth/login
Body: { "matricula": "20230001", "senha": "senha123" }
Response: {
  "success": true,
  "data": {
    "id": "uuid",
    "nome": "João Silva",
    "matricula": "20230001",
    "email": "joao@isptec.co.ao",
    "tipo": "ADMIN",
    "status": "ATIVO",
    "token": "jwt_token_here",
    "dataCadastro": "2024-01-15T10:00:00Z",
    "multasPendentes": 0,
    "emprestimosAtivos": 2
  }
}
```

### Livros
```
GET /api/livros?page=1&limit=20&search=clean&categoria=Técnico&disponivel=true
Response: {
  "success": true,
  "data": {
    "data": [ ...array de livros... ],
    "total": 500,
    "page": 1,
    "limit": 20,
    "totalPages": 25
  }
}

GET /api/livros/:id
Response: {
  "success": true,
  "data": { ...livro completo... }
}

POST /api/livros
Headers: { "Authorization": "Bearer jwt_token" }
Body: { ...dados do livro... }
Response: {
  "success": true,
  "data": { ...livro criado... },
  "message": "Livro cadastrado com sucesso"
}

PUT /api/livros/:id
Headers: { "Authorization": "Bearer jwt_token" }
Body: { ...dados para atualizar... }
Response: {
  "success": true,
  "data": { ...livro atualizado... },
  "message": "Livro atualizado com sucesso"
}

DELETE /api/livros/:id
Headers: { "Authorization": "Bearer jwt_token" }
Response: {
  "success": true,
  "message": "Livro excluído com sucesso"
}
```

### Usuários (Futuro)
```
GET /api/usuarios?page=1&limit=20
POST /api/usuarios
GET /api/usuarios/:id
PUT /api/usuarios/:id
DELETE /api/usuarios/:id
PUT /api/usuarios/:id/bloquear
PUT /api/usuarios/:id/desbloquear
```

### Empréstimos (Futuro)
```
GET /api/emprestimos?status=ATIVO
POST /api/emprestimos (realizar empréstimo)
PUT /api/emprestimos/:id/devolver
PUT /api/emprestimos/:id/renovar
```

### Reservas (Futuro)
```
GET /api/reservas
POST /api/reservas
DELETE /api/reservas/:id
```

### Dashboard (Futuro)
```
GET /api/dashboard/estatisticas
GET /api/dashboard/emprestimos-por-mes
GET /api/dashboard/livros-populares
```

---

## 🔧 Passos para Conectar ao Backend

### 1. Configure a URL da API

Edite o arquivo `.env.local`:
```env
# Substitua pela URL real do seu backend
VITE_API_URL=http://localhost:3000/api
# ou
VITE_API_URL=https://api-biblioteca.isptec.co.ao/api
```

### 2. Reinicie o servidor de desenvolvimento

```bash
npm run dev
```

### 3. Remova os Dados Mock

#### Login (src/pages/Login.tsx)
O login JÁ está integrado. Apenas certifique-se que o backend retorna o formato esperado.

#### Dashboard (src/pages/Dashboard.tsx)

**Antes (Mock):**
```typescript
const stats = {
  totalLivros: 500,
  totalUsuarios: 120,
  emprestimosAtivos: 15,
  reservasPendentes: 12,
};
```

**Depois (API):**
```typescript
import { dashboardService } from '@/services/dashboardService';

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadStats = async () => {
      try {
        const data = await dashboardService.getEstatisticas();
        setStats(data);
      } catch (error) {
        toast.error('Erro ao carregar estatísticas');
      } finally {
        setLoading(false);
      }
    };
    loadStats();
  }, []);

  if (loading) return <div>Carregando...</div>;

  // ... resto do componente
};
```

#### Lista de Livros (src/pages/Livros/ListaLivros.tsx)

**Antes (Mock):**
```typescript
useEffect(() => {
  const mockLivros: Livro[] = [ ... ];
  setLivros(mockLivros);
}, []);
```

**Depois (API):**
```typescript
import { livrosService } from '@/services/livrosService';

useEffect(() => {
  const loadLivros = async () => {
    try {
      setLoading(true);
      const response = await livrosService.getLivros({
        page: 1,
        limit: 20,
        search: searchTerm,
      });
      setLivros(response.data);
      // Também pode armazenar: response.total, response.totalPages
    } catch (error) {
      toast.error('Erro ao carregar livros');
    } finally {
      setLoading(false);
    }
  };
  loadLivros();
}, [searchTerm]);
```

#### Cadastro de Livro (src/pages/Livros/CadastroLivro.tsx)

**Antes (Mock):**
```typescript
const onSubmit = async (data: LivroFormData) => {
  try {
    setLoading(true);
    // await livrosService.createLivro(data);
    console.log('Dados do livro:', data);
    toast.success('Livro cadastrado com sucesso!');
    navigate(ROUTES.LIVROS);
  } catch (error: any) {
    // ...
  }
};
```

**Depois (API):**
```typescript
const onSubmit = async (data: LivroFormData) => {
  try {
    setLoading(true);
    await livrosService.createLivro(data);  // <-- Descomente esta linha
    toast.success('Livro cadastrado com sucesso!');
    navigate(ROUTES.LIVROS);
  } catch (error: any) {
    console.error('Erro ao cadastrar livro:', error);
    toast.error(error.message || 'Erro ao cadastrar livro');
  } finally {
    setLoading(false);
  }
};
```

### 4. Crie os Services Faltantes

Crie `src/services/dashboardService.ts`:
```typescript
import api from './api';
import { ApiResponse } from '@/types/api';

interface EstatisticasDashboard {
  totalLivros: number;
  totalUsuarios: number;
  emprestimosAtivos: number;
  reservasPendentes: number;
}

export const dashboardService = {
  async getEstatisticas(): Promise<EstatisticasDashboard> {
    const response = await api.get<ApiResponse<EstatisticasDashboard>>('/dashboard/estatisticas');
    return response.data.data!;
  },

  async getEmprestimosPorMes() {
    const response = await api.get('/dashboard/emprestimos-por-mes');
    return response.data.data;
  },

  async getLivrosPopulares() {
    const response = await api.get('/dashboard/livros-populares');
    return response.data.data;
  },
};
```

### 5. Teste a Integração

1. **Teste o Login:**
   - Abra http://localhost:5173/login
   - Entre com credenciais válidas do backend
   - Verifique se o token é salvo no localStorage
   - Confirme redirecionamento para dashboard

2. **Verifique o Token:**
   - Abra DevTools → Application → Local Storage
   - Verifique `@biblioteca:token` e `@biblioteca:user`

3. **Teste as Requisições:**
   - Abra DevTools → Network
   - Navegue pelas páginas
   - Veja as requisições sendo feitas
   - Confirme que o header `Authorization: Bearer token` está presente

4. **Teste Erros:**
   - Simule um erro 401 (token inválido): veja se redireciona para login
   - Simule um erro 500: veja se o toast de erro aparece

---

## 🔐 Segurança

### Headers Necessários no Backend

Para evitar problemas de CORS, configure o backend:

```javascript
// Node.js/Express exemplo
app.use(cors({
  origin: 'http://localhost:5173', // ou seu domínio de produção
  credentials: true
}));

// Headers de resposta
res.setHeader('Access-Control-Allow-Origin', 'http://localhost:5173');
res.setHeader('Access-Control-Allow-Credentials', 'true');
res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
```

### Validação JWT

O backend deve:
1. Validar o token em todas as rotas protegidas
2. Verificar expiração do token
3. Retornar 401 se token inválido/expirado
4. Incluir informações do usuário no token

Formato recomendado do JWT payload:
```json
{
  "id": "user_uuid",
  "matricula": "20230001",
  "tipo": "ADMIN",
  "iat": 1234567890,
  "exp": 1234654290
}
```

---

## 🚨 Tratamento de Erros

O frontend já está configurado para lidar com erros. Certifique-se que o backend retorna:

### Sucesso (2xx)
```json
{
  "success": true,
  "data": { ... },
  "message": "Operação realizada com sucesso" // opcional
}
```

### Erro (4xx, 5xx)
```json
{
  "success": false,
  "error": "Mensagem de erro clara",
  "message": "Mensagem alternativa",
  "code": "ERROR_CODE" // opcional
}
```

### Erros Específicos

**401 Unauthorized:**
```json
{
  "success": false,
  "error": "Token inválido ou expirado"
}
```
→ Frontend redireciona para `/login`

**403 Forbidden:**
```json
{
  "success": false,
  "error": "Você não tem permissão para esta ação"
}
```
→ Frontend mostra toast de erro

**404 Not Found:**
```json
{
  "success": false,
  "error": "Recurso não encontrado"
}
```
→ Frontend mostra toast de erro

**500 Internal Server Error:**
```json
{
  "success": false,
  "error": "Erro interno do servidor. Tente novamente mais tarde."
}
```
→ Frontend mostra toast de erro

---

## 📊 Paginação

O frontend espera este formato para listas paginadas:

```json
{
  "success": true,
  "data": {
    "data": [ ...itens... ],
    "total": 500,
    "page": 1,
    "limit": 20,
    "totalPages": 25
  }
}
```

Implemente no backend:
- Query params: `?page=1&limit=20`
- Skip/offset: `(page - 1) * limit`
- Total count sempre retornado

---

## 🛠️ Debugging

### Se as requisições não estão sendo enviadas:

1. Verifique a URL no `.env.local`
2. Reinicie o servidor: `npm run dev`
3. Limpe o cache: `Ctrl + Shift + R`

### Se o token não está sendo enviado:

1. Verifique se o token está no localStorage
2. Veja o console do navegador (erros de interceptor)
3. Confira que o header está sendo adicionado (Network tab)

### Se o login não funciona:

1. Verifique a resposta do backend (deve ter campo `token`)
2. Confira que `token` e `data` estão sendo salvos
3. Veja se há erro de CORS

---

## ✅ Checklist de Integração

- [ ] Backend rodando e acessível
- [ ] URL configurada em `.env.local`
- [ ] CORS configurado no backend
- [ ] Endpoint `/auth/login` implementado
- [ ] Endpoint `/livros` implementado (GET, POST, PUT, DELETE)
- [ ] Formato de resposta correto (`{ success, data }`)
- [ ] JWT implementado e validado
- [ ] Token retornado no login
- [ ] Headers de autorização aceitos
- [ ] Erros retornando status codes corretos
- [ ] Dados mock removidos do frontend
- [ ] Chamadas aos services descomentadas
- [ ] Testes de integração realizados

---

**Quando tudo estiver integrado, o sistema estará 100% funcional!** 🎉

