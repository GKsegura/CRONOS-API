# Cronos API

API REST para gerenciamento de horas trabalhadas e tarefas diárias.

## 📋 Índice

- [Tecnologias](#tecnologias)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Como Executar](#como-executar)
- [Documentação da API](#documentação-da-api)
- [Banco de Dados](#banco-de-dados)
- [Exportação](#exportação)

## 🚀 Tecnologias

- Java 11+
- Spark Framework (servidor web)
- SQLite (banco de dados)
- Gson (serialização JSON)
- Apache POI (geração de Excel)

## 📁 Estrutura do Projeto

```
src/
├── api/                          # Controllers REST
│   ├── ApiServer.java           # Configuração do servidor
│   ├── DiaController.java       # Endpoints de dias
│   ├── TaskController.java      # Endpoints de tarefas
│   ├── CategoriaController.java # Endpoints de categorias
│   ├── ClienteController.java   # Endpoints de clientes
│   └── ExportController.java    # Endpoints de exportação
│
├── entities/                     # Entidades do domínio
│   ├── Dia.java                 # Dia de trabalho
│   ├── Task.java                # Tarefa
│   ├── Categoria.java           # Enum de categorias
│   └── Cliente.java             # Enum de clientes
│
├── repository/                   # Acesso aos dados
│   └── DiaRepository.java       # CRUD de dias e tarefas
│
├── service/                      # Lógica de negócio
│   ├── DiaService.java          # Serviços de dia
│   ├── ApontamentoExcelService.java # Geração de Excel
│   └── RelatorioService.java    # Geração de relatórios
│
├── database/                     # Configuração do BD
│   ├── SQLiteConnection.java    # Conexão SQLite
│   └── TableCreator.java        # Criação de tabelas
│
└── application/                  # Ponto de entrada
    └── Main.java                # Inicialização
```

## ▶️ Como Executar

1. **Clone o repositório**
```bash
git clone <repo-url>
cd cronos-api
```

2. **Compile o projeto**
```bash
javac -cp "lib/*:." src/**/*.java
```

3. **Execute a aplicação**
```bash
java -cp "lib/*:." application.Main
```

4. **Acesse a API**
```
http://localhost:8080
```

## 📚 Documentação da API

Consulte o arquivo [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) para detalhes completos sobre todos os endpoints.

### Resumo dos Endpoints

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| **Dias** |
| GET | `/api/dias` | Lista todos os dias |
| GET | `/api/dias/:id` | Busca dia por ID |
| GET | `/api/dias/data/:data` | Busca dia por data |
| POST | `/api/dias` | Cria novo dia |
| PUT | `/api/dias/:id` | Atualiza dia |
| DELETE | `/api/dias/:id` | Remove dia |
| **Tarefas** |
| GET | `/api/tarefas/:diaId` | Lista tarefas de um dia |
| POST | `/api/tarefas/:diaId` | Cria tarefa |
| PUT | `/api/tarefas/:id` | Atualiza tarefa |
| DELETE | `/api/tarefas/:id` | Remove tarefa |
| **Dados Auxiliares** |
| GET | `/api/categorias` | Lista categorias |
| GET | `/api/clientes` | Lista clientes |
| GET | `/api/clientes/nomes` | Lista nomes de clientes |
| **Exportação** |
| GET | `/api/export/excel/:mes/:ano` | Exporta Excel |

## 🗄️ Banco de Dados

A API utiliza SQLite com o arquivo `database.db` criado automaticamente na inicialização.

### Tabelas

**dias**
```sql
CREATE TABLE dias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    data TEXT NOT NULL,
    inicioTrabalho TEXT,
    fimTrabalho TEXT,
    inicioAlmoco TEXT,
    fimAlmoco TEXT
);
```

**tarefas**
```sql
CREATE TABLE tarefas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    descricao TEXT NOT NULL,
    categoria TEXT,
    cliente TEXT,
    duracaoMin INTEGER,
    obs TEXT,
    apontado INTEGER DEFAULT 0,
    dia_id INTEGER,
    FOREIGN KEY(dia_id) REFERENCES dias(id) ON DELETE CASCADE
);
```

## 📊 Exportação

### Excel

A exportação gera um arquivo `.xlsx` com as seguintes colunas:

| Coluna | Descrição |
|--------|-----------|
| Processo/Atividade | Descrição da tarefa |
| Comentário | Categoria da tarefa |
| DATA | Data no formato dd/MM/yy |
| TIPO | Tipo baseado na categoria |
| HORAS | Duração formatada (Hh:MMm) |
| COOPERATIVA | Cliente |
| TASKS STATUS | Status de aprovação |
| NPX | Status NPX |
| OBSERVAÇÕES | Campo de observações da tarefa |
| APONTADO | Se a tarefa foi apontada (SIM/NÃO) |

**Formatação:**
- Cabeçalho: fundo verde escuro, texto branco
- Linhas alternadas: branco e cinza claro
- Status:
    - **Pendente**: fundo vermelho, texto branco
    - **Aprovado**: fundo verde claro, texto verde escuro

### Tipos de Tarefa

A coluna TIPO é determinada automaticamente baseada na categoria:

| Categoria | Tipo |
|-----------|------|
| REUNIAO | REUNIÃO |
| SUPORTE* | SUPORTE AO CLIENTE |
| APOIO_DIVERSOS | APOIO AO TIME |
| DESPESA_GERAL | DESPESAS GERAIS |
| Outros | ERRO NEXUM (retrabalho) |

## 🔧 Configurações

### CORS

A API está configurada para aceitar requisições de qualquer origem (`*`).

Para produção, recomenda-se configurar origens específicas em `ApiServer.java`:

```java
response.header("Access-Control-Allow-Origin", "https://seu-frontend.com");
```

### Porta

A porta padrão é `8080`. Para alterar, modifique a constante em `ApiServer.java`:

```java
private static final int PORT = 8080;
```

## 📝 Notas de Desenvolvimento

### Removido da API (código CLI mantido para referência)

Os seguintes componentes foram mantidos no código mas não são utilizados pela API REST:

- `TaskService.java` - Operações interativas de console
- `RelatorioService.java` - Geração de relatórios em markdown (não utilizado pela API)
- `RelatorioController.java` - Controller não registrado

Estes componentes podem ser úteis para ferramentas CLI futuras.

### Boas Práticas Implementadas

✅ Validação de entrada em todos os endpoints  
✅ Tratamento de erros com códigos HTTP apropriados  
✅ Logging de operações importantes  
✅ Documentação inline nos controllers  
✅ Separação de responsabilidades (Controller → Repository → Database)  
✅ JSON como formato padrão de resposta  
✅ Formatação consistente de datas e horas

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob licença MIT.