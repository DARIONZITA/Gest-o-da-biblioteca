# 🔐 Credenciais de Acesso - Modo MOCK

## ✅ Sistema em Modo MOCK Ativo

O sistema está configurado para funcionar **SEM BACKEND** usando dados simulados (mock).

### 📝 Credenciais Disponíveis:

#### 👨‍💼 **ADMIN** (Administrador)
```
Matrícula: 20230001
Senha: admin123
```
**Permissões:** Acesso total ao sistema, pode gerenciar livros, usuários, empréstimos e reservas.

---

#### 👨‍🎓 **ESTUDANTE** (Membro)
```
Matrícula: 20230002
Senha: estudante123
```
**Perfil:** Maria Santos  
**Curso:** Engenharia Informática  
**Ano:** 2023

---

#### 👨‍🏫 **PROFESSOR** (Membro)
```
Matrícula: 20230003
Senha: professor123
```
**Perfil:** Dr. Carlos Mendes

---

## 🎯 Como Usar

1. **Acesse:** http://localhost:5174/ (ou 5173/5175 dependendo da porta)
2. **Faça Login:** Use uma das credenciais acima
3. **Explore:** 
   - ✅ Dashboard com estatísticas
   - ✅ Lista de livros (5 livros mock)
   - ✅ Cadastro de novos livros
   - ✅ Busca e filtros funcionais
   - ✅ Edição e exclusão de livros

---

## 🔧 Como Desativar o Mock (Quando o backend estiver pronto)

### Passo 1: Desativar MOCK_ENABLED

Edite o arquivo `src/config/mock.ts`:

```typescript
// Mude de true para false
export const MOCK_ENABLED = false;
```

### Passo 2: Configurar URL da API

Edite o arquivo `.env.local`:

```env
VITE_API_URL=http://localhost:3000/api
# ou
VITE_API_URL=https://api-biblioteca.isptec.co.ao/api
```

### Passo 3: Reiniciar o servidor

```bash
npm run dev
```

---

## 📊 Dados Mock Disponíveis

### Livros (5 exemplos):
- Clean Code (Robert C. Martin)
- Introduction to Algorithms (Thomas H. Cormen)
- Mayombe (Pepetela)
- Head First Design Patterns (Eric Freeman)
- Design Patterns (Gang of Four)

### Estatísticas:
- Total de Livros: 500
- Total de Usuários: 120
- Empréstimos Ativos: 15
- Reservas Pendentes: 12

### Ações Pendentes (Dashboard):
- Devolução Atrasada
- Reserva Disponível
- Multa Pendente

---

## 🚀 Recursos Funcionais (Modo Mock)

| Funcionalidade | Status |
|---|---|
| ✅ Login com autenticação | Funcional |
| ✅ Dashboard com estatísticas | Funcional |
| ✅ Listar livros | Funcional |
| ✅ Buscar livros | Funcional |
| ✅ Cadastrar livro | Funcional |
| ✅ Editar livro | Funcional |
| ✅ Excluir livro | Funcional |
| ✅ Filtros (categoria, disponibilidade) | Funcional |
| ✅ Paginação | Funcional |
| ⏳ Gestão de Usuários | A implementar |
| ⏳ Gestão de Empréstimos | A implementar |
| ⏳ Gestão de Reservas | A implementar |
| ⏳ Relatórios | A implementar |

---

## 💡 Dicas

- **Simulação de rede:** Os mocks têm delay de 400-800ms para simular chamadas reais
- **Persistência:** Dados ficam salvos no array em memória durante a sessão
- **Token:** JWT simulado salvo no localStorage
- **Logout:** Limpa dados e redireciona para login

---

## 📚 Documentação Completa

- **[PROJETO.md](../PROJETO.md)** - Visão geral do projeto
- **[IMPLEMENTACAO.md](../IMPLEMENTACAO.md)** - Detalhes técnicos
- **[INTEGRACAO.md](../INTEGRACAO.md)** - Guia de integração com backend

---

**✨ Sistema 100% funcional mesmo sem backend!** 🎉
