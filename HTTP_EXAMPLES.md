# Exemplos de Requisições - Cronos API

Este arquivo contém exemplos de requisições para testar a API usando curl ou ferramentas como Postman/Insomnia.

## 📅 DIAS

### Criar um novo dia
```bash
curl -X POST http://localhost:8080/api/dias \
  -H "Content-Type: application/json" \
  -d '{
    "data": "31/01/2026",
    "inicioTrabalho": "09:00",
    "fimTrabalho": "18:00",
    "inicioAlmoco": "12:00",
    "fimAlmoco": "13:00"
  }'
```

### Criar dia apenas com data (horários opcionais)
```bash
curl -X POST http://localhost:8080/api/dias \
  -H "Content-Type: application/json" \
  -d '{
    "data": "01/02/2026"
  }'
```

### Listar todos os dias
```bash
curl http://localhost:8080/api/dias
```

### Buscar dia por ID
```bash
curl http://localhost:8080/api/dias/1
```

### Buscar dia por data
```bash
curl http://localhost:8080/api/dias/data/31/01/2026
```

### Atualizar horários de um dia
```bash
curl -X PUT http://localhost:8080/api/dias/1 \
  -H "Content-Type: application/json" \
  -d '{
    "inicioTrabalho": "08:30",
    "fimTrabalho": "17:30"
  }'
```

### Remover horário de almoço (usando null)
```bash
curl -X PUT http://localhost:8080/api/dias/1 \
  -H "Content-Type: application/json" \
  -d '{
    "inicioAlmoco": null,
    "fimAlmoco": null
  }'
```

### Deletar um dia
```bash
curl -X DELETE http://localhost:8080/api/dias/1
```

## ✅ TAREFAS

### Criar tarefa completa
```bash
curl -X POST http://localhost:8080/api/tarefas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "CHAMADO 12345 - Corrigir bug no login",
    "categoria": "SUPORTE",
    "cliente": "SICOOB_CREDIMINAS",
    "duracaoMin": 120,
    "obs": "Bug crítico resolvido com sucesso",
    "apontado": false
  }'
```

### Criar tarefa simples
```bash
curl -X POST http://localhost:8080/api/tarefas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "REUNIÃO DIÁRIA DO TIME",
    "categoria": "REUNIAO",
    "duracaoMin": 30
  }'
```

### Criar tarefa de suporte já apontada
```bash
curl -X POST http://localhost:8080/api/tarefas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "CHAMADO 54321 - Atualização de sistema",
    "categoria": "SUPORTE_HORAS_PAGAS",
    "cliente": "COOCAFE",
    "duracaoMin": 240,
    "obs": "Deploy realizado com sucesso",
    "apontado": true
  }'
```

### Listar tarefas de um dia
```bash
curl http://localhost:8080/api/tarefas/1
```

### Atualizar tarefa
```bash
curl -X PUT http://localhost:8080/api/tarefas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "CHAMADO 12345 - Bug corrigido e testado",
    "categoria": "SUPORTE",
    "cliente": "SICOOB_CREDIMINAS",
    "duracaoMin": 150,
    "obs": "Incluído teste automatizado",
    "apontado": true
  }'
```

### Marcar tarefa como apontada
```bash
curl -X PUT http://localhost:8080/api/tarefas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "CHAMADO 12345",
    "categoria": "SUPORTE",
    "apontado": true
  }'
```

### Deletar tarefa
```bash
curl -X DELETE http://localhost:8080/api/tarefas/1
```

## 📂 CATEGORIAS

### Listar todas as categorias
```bash
curl http://localhost:8080/api/categorias
```

**Resposta esperada:**
```json
[
  "SUPORTE",
  "REUNIAO",
  "DESPESA_GERAL",
  "SUPORTE_HORAS_PAGAS",
  "APOIO_DIVERSOS"
]
```

## 👥 CLIENTES

### Listar clientes (completo)
```bash
curl http://localhost:8080/api/clientes
```

**Resposta esperada:**
```json
[
  {
    "valor": "ALONSO_VERDIANI",
    "nome": "Alonso e Verdiani"
  },
  {
    "valor": "COOCAFE",
    "nome": "Coocafé"
  },
  ...
]
```

### Listar apenas nomes
```bash
curl http://localhost:8080/api/clientes/nomes
```

**Resposta esperada:**
```json
[
  "Alonso e Verdiani",
  "Coocafé",
  "CoopLuiza",
  ...
]
```

## 📊 EXPORTAÇÃO

### Exportar Excel do mês (Janeiro 2026)
```bash
curl http://localhost:8080/api/export/excel/1/2026 \
  --output apontamentos_01_2026.xlsx
```

### Exportar Excel do mês (Dezembro 2025)
```bash
curl http://localhost:8080/api/export/excel/12/2025 \
  --output apontamentos_12_2025.xlsx
```

## 🔄 FLUXO COMPLETO DE EXEMPLO

```bash
# 1. Criar um dia
DIA_RESPONSE=$(curl -s -X POST http://localhost:8080/api/dias \
  -H "Content-Type: application/json" \
  -d '{
    "data": "31/01/2026",
    "inicioTrabalho": "09:00",
    "fimTrabalho": "18:00",
    "inicioAlmoco": "12:00",
    "fimAlmoco": "13:00"
  }')

# Extrair ID (usando jq)
DIA_ID=$(echo $DIA_RESPONSE | jq -r '.id')
echo "Dia criado com ID: $DIA_ID"

# 2. Adicionar tarefas
curl -s -X POST http://localhost:8080/api/tarefas/$DIA_ID \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "CHAMADO 12345 - Bug no sistema",
    "categoria": "SUPORTE",
    "cliente": "SICOOB_CREDIMINAS",
    "duracaoMin": 120,
    "apontado": false
  }' | jq

curl -s -X POST http://localhost:8080/api/tarefas/$DIA_ID \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "REUNIÃO DIÁRIA",
    "categoria": "REUNIAO",
    "duracaoMin": 30,
    "apontado": false
  }' | jq

curl -s -X POST http://localhost:8080/api/tarefas/$DIA_ID \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "CHAMADO 67890 - Deploy produção",
    "categoria": "SUPORTE_HORAS_PAGAS",
    "cliente": "NEXUM",
    "duracaoMin": 180,
    "obs": "Deploy realizado às 14h",
    "apontado": true
  }' | jq

# 3. Listar tarefas do dia
echo "Tarefas do dia:"
curl -s http://localhost:8080/api/tarefas/$DIA_ID | jq

# 4. Buscar dia completo
echo "Dia completo:"
curl -s http://localhost:8080/api/dias/$DIA_ID | jq
```

## 🧪 TESTES DE VALIDAÇÃO

### Tentar criar tarefa sem descrição (deve retornar erro 400)
```bash
curl -X POST http://localhost:8080/api/tarefas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "categoria": "SUPORTE"
  }'
```

### Buscar dia inexistente (deve retornar erro 404)
```bash
curl http://localhost:8080/api/dias/999999
```

### ID inválido (deve retornar erro 400)
```bash
curl http://localhost:8080/api/dias/abc
```

### Exportar mês sem dados (deve retornar erro 404)
```bash
curl http://localhost:8080/api/export/excel/12/2030
```

### Mês inválido (deve retornar erro 400)
```bash
curl http://localhost:8080/api/export/excel/13/2026
```

## 📝 NOTAS

### Usando com Postman/Insomnia

Importe esta coleção ou use os exemplos acima ajustando:
- Method (GET, POST, PUT, DELETE)
- URL
- Headers: `Content-Type: application/json`
- Body: JSON

### Usando jq para formatar JSON

Se você tem `jq` instalado, pode formatar as respostas:

```bash
curl http://localhost:8080/api/dias | jq
```

### Variáveis de ambiente

Para facilitar, você pode definir:

```bash
export API_URL="http://localhost:8080"

# Usar assim:
curl $API_URL/api/dias
```

### Formatos de data aceitos

A API aceita dois formatos de data:
- `dd/MM/yyyy` - Formato brasileiro (31/01/2026)
- `yyyy-MM-dd` - Formato ISO (2026-01-31)

### Formatos de hora aceitos

A API aceita dois formatos de hora:
- `HH:mm` - Formato curto (09:00)
- `HH:mm:ss` - Formato ISO (09:00:00)