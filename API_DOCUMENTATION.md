# Cronos API - Documentação

API REST para gerenciamento de horas trabalhadas e tarefas diárias.

## Base URL
```
http://localhost:8080
```

## Endpoints

### 📅 Dias (Days)

#### Listar todos os dias
```http
GET /api/dias
```
**Resposta:** Array de objetos Dia com suas tarefas

---

#### Buscar dia por ID
```http
GET /api/dias/:id
```
**Parâmetros:**
- `id` (path) - ID do dia

**Resposta:** Objeto Dia

**Erros:**
- `404` - Dia não encontrado
- `400` - ID inválido

---

#### Buscar dia por data
```http
GET /api/dias/data/:data
```
**Parâmetros:**
- `data` (path) - Data no formato `dd/MM/yyyy`

**Exemplo:** `/api/dias/data/31/01/2026`

**Resposta:** Objeto Dia

**Erros:**
- `404` - Dia não encontrado

---

#### Criar novo dia
```http
POST /api/dias
```
**Body:**
```json
{
  "data": "31/01/2026",
  "inicioTrabalho": "09:00",
  "fimTrabalho": "18:00",
  "inicioAlmoco": "12:00",
  "fimAlmoco": "13:00"
}
```

**Notas:**
- Se o dia já existe, retorna o existente (não cria duplicado)
- Todos os campos são opcionais exceto `data`
- Formato de data aceita: `dd/MM/yyyy` ou `yyyy-MM-dd`
- Formato de hora aceita: `HH:mm` ou `HH:mm:ss`

**Resposta:** Objeto Dia criado (status 201)

---

#### Atualizar dia
```http
PUT /api/dias/:id
```
**Parâmetros:**
- `id` (path) - ID do dia

**Body:**
```json
{
  "inicioTrabalho": "08:30",
  "fimTrabalho": "17:30",
  "inicioAlmoco": "12:00",
  "fimAlmoco": "13:00"
}
```

**Notas:**
- Apenas os campos enviados serão atualizados
- Use `null` para remover um horário

**Resposta:** Objeto Dia atualizado

**Erros:**
- `404` - Dia não encontrado
- `400` - ID inválido

---

#### Deletar dia
```http
DELETE /api/dias/:id
```
**Parâmetros:**
- `id` (path) - ID do dia

**Resposta:**
```json
{
  "message": "Dia removido com sucesso"
}
```

**Erros:**
- `404` - Dia não encontrado
- `400` - ID inválido

---

### ✅ Tarefas (Tasks)

#### Listar tarefas de um dia
```http
GET /api/tarefas/:diaId
```
**Parâmetros:**
- `diaId` (path) - ID do dia

**Resposta:** Array de tarefas

**Erros:**
- `404` - Dia não encontrado
- `400` - ID inválido

---

#### Criar tarefa
```http
POST /api/tarefas/:diaId
```
**Parâmetros:**
- `diaId` (path) - ID do dia

**Body:**
```json
{
  "descricao": "CHAMADO 12345 - Corrigir bug no sistema",
  "categoria": "SUPORTE",
  "cliente": "CLIENTE",
  "duracaoMin": 120,
  "obs": "Bug crítico resolvido",
  "apontado": false
}
```

**Campos:**
- `descricao` (obrigatório) - Descrição da tarefa
- `categoria` (opcional) - Uma das categorias disponíveis
- `cliente` (opcional) - Nome do cliente
- `duracaoMin` (opcional) - Duração em minutos
- `obs` (opcional) - Observações adicionais
- `apontado` (opcional, padrão: false) - Se foi apontado no sistema

**Resposta:** Objeto Task criado (status 201)

**Erros:**
- `404` - Dia não encontrado
- `400` - Descrição obrigatória ou ID inválido

---

#### Atualizar tarefa
```http
PUT /api/tarefas/:id
```
**Parâmetros:**
- `id` (path) - ID da tarefa

**Body:** Mesmo formato do POST

**Resposta:** Objeto Task atualizado

**Erros:**
- `400` - ID inválido ou descrição obrigatória

---

#### Deletar tarefa
```http
DELETE /api/tarefas/:id
```
**Parâmetros:**
- `id` (path) - ID da tarefa

**Resposta:**
```json
{
  "message": "Tarefa removida com sucesso"
}
```

**Erros:**
- `400` - ID inválido

---

### 📂 Categorias

#### Listar categorias disponíveis
```http
GET /api/categorias
```

**Resposta:**
```json
[
  "SUPORTE",
  "REUNIAO",
  "DESPESA_GERAL",
  "SUPORTE_HORAS_PAGAS",
  "APOIO_DIVERSOS"
]
```

---

### 👥 Clientes

#### Listar clientes (completo)
```http
GET /api/clientes
```

**Resposta:**
```json
[
  {
    "valor": "CLIENTE",
    "nome": "cliente"
  },
  {
    "valor": "NEXUM",
    "nome": "Nexum"
  }
]
```

---

#### Listar apenas nomes de clientes
```http
GET /api/clientes/nomes
```

**Resposta:** Array de strings com nomes dos clientes (ordenados alfabeticamente)

---

### 📊 Exportação

#### Exportar Excel do mês
```http
GET /api/export/excel/:mes/:ano
```

**Parâmetros:**
- `mes` (path) - Número do mês (1-12)
- `ano` (path) - Ano (ex: 2026)

**Exemplo:** `/api/export/excel/1/2026`

**Resposta:** Arquivo Excel (.xlsx) para download

**Headers da resposta:**
- `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- `Content-Disposition: attachment; filename="Apontamentos_01_2026.xlsx"`

**Formato da planilha:**
- Processo/Atividade
- Comentário (Categoria)
- Data
- Tipo
- Horas
- Cooperativa (Cliente)
- Tasks Status
- NPX
- Observações
- Apontado

**Erros:**
- `404` - Nenhum apontamento encontrado
- `400` - Mês ou ano inválido

---

## Modelos de Dados

### Dia (Day)
```json
{
  "id": 1,
  "data": "31/01/2026",
  "inicioTrabalho": "09:00",
  "fimTrabalho": "18:00",
  "inicioAlmoco": "12:00",
  "fimAlmoco": "13:00",
  "tarefas": [...]
}
```

### Task (Tarefa)
```json
{
  "id": 1,
  "descricao": "CHAMADO 12345",
  "categoria": "SUPORTE",
  "cliente": "cliente",
  "duracaoMin": 120,
  "obs": "Observações adicionais",
  "apontado": false
}
```

---

## Códigos de Status HTTP

- `200` - Sucesso
- `201` - Criado com sucesso
- `400` - Requisição inválida
- `404` - Recurso não encontrado
- `500` - Erro interno do servidor

---

## CORS

A API permite requisições de qualquer origem (`*`) e suporta os métodos:
- GET
- POST
- PUT
- DELETE
- OPTIONS

---

## Exemplos de Uso

### Criar um dia e adicionar tarefas

```bash
# 1. Criar dia
curl -X POST http://localhost:8080/api/dias \
  -H "Content-Type: application/json" \
  -d '{
    "data": "31/01/2026",
    "inicioTrabalho": "09:00",
    "fimTrabalho": "18:00"
  }'

# Resposta: { "id": 1, "data": "31/01/2026", ... }

# 2. Adicionar tarefa
curl -X POST http://localhost:8080/api/tarefas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "CHAMADO 12345 - Bug corrigido",
    "categoria": "SUPORTE",
    "cliente": "CLIENTE",
    "duracaoMin": 120,
    "apontado": false
  }'
```

### Buscar dia por data

```bash
curl http://localhost:8080/api/dias/data/31/01/2026
```

### Exportar Excel

```bash
curl http://localhost:8080/api/export/excel/1/2026 --output apontamentos.xlsx
```