# Relatório Técnico - Solução do Erro POST /auth/register

## Resumo Executivo

O endpoint `POST /auth/register` apresentava **HTTP 500 Internal Server Error** devido a múltiplos problemas na configuração do sistema. Este documento detalha o diagnóstico técnico, as correções implementadas e o processo de solução completa.

## Problema Original

### Sintomas
- **Endpoint**: `POST /auth/register`
- **Status Code**: 500 Internal Server Error
- **Resposta**: `{"error":{"code":"INTERNAL_ERROR","message":"unexpected error"}}`

### Impacto
- Funcionalidade de registro de usuários completamente inoperante
- Experiência do usuário prejudicada com erro genérico
- Sistema de autenticação indisponível

## Análise Técnica Detalhada

### 1. Diagnóstico Inicial - Stack Trace Analysis

**Primeiro erro identificado**:
```
RuntimeError: 'cryptography' package is required for sha256_password or caching_sha2_password auth methods
```

**Causa raiz**: MySQL 8.0 utiliza por padrão o método de autenticação `caching_sha2_password`, que requer o pacote `cryptography` do Python para funcionar com o driver PyMySQL.

**Fluxo técnico do erro**:
1. PyMySQL tenta conectar ao MySQL 8.0
2. MySQL solicita autenticação `caching_sha2_password`
3. PyMySQL tenta usar criptografia RSA para handshake
4. Pacote `cryptography` não encontrado → RuntimeError
5. SQLAlchemy captura exceção → converte em OperationalError
6. FastAPI handler genérico → HTTP 500

### 2. Problemas Secundários Identificados

#### 2.1. Arquivo init.sql Vazio
```sql
-- init.sql
```
- **Problema**: Sem estrutura de tabelas
- **Impacto**: Tabela `users` não existia no banco
- **Sintoma**: `Table 'smartsaude.users' doesn't exist`

#### 2.2. Configuração de Banco Inconsistente
- **Docker**: DB_URL apontava para `mysql:3306` (correto)
- **Local**: DB_URL apontava para `localhost:3306` (problema de permissões)
- **Usuário**: `smartuser` sem permissões adequadas para `localhost`

#### 2.3. Conflito de Portas
- Docker container usando porta 8080
- Tentativa de executar uvicorn local na mesma porta
- Erro: `[Errno 98] Address already in use`

## Soluções Implementadas

### 1. Correção de Dependências (Prioridade Crítica)

**Arquivo**: `auth-service/requirements.txt`

**Antes**:
```txt
fastapi==0.115.6
uvicorn==0.32.1
pydantic-settings==2.7.0
PyJWT==2.10.1
SQLAlchemy==2.0.36
PyMySQL==1.1.1
email-validator==2.2.0
```

**Depois**:
```txt
fastapi>=0.110
uvicorn[standard]>=0.27
pydantic>=2.6
pydantic-settings>=2.2
SQLAlchemy>=2.0
PyMySQL>=1.1
PyJWT>=2.8
email-validator>=2.2
cryptography>=41.0.0
```

**Justificativa técnica**:
- `cryptography>=41.0.0`: Essencial para autenticação MySQL 8.0
- `uvicorn[standard]`: Inclui otimizações de performance
- Versões flexíveis (`>=`) para melhor compatibilidade

### 2. Criação de Estrutura do Banco

**Arquivo**: `database/init.sql`

**Antes**:
```sql
-- init.sql
```

**Depois**:
```sql
-- init.sql
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email)
);
```

**Justificativa técnica**:
- Mapeamento direto do modelo ORM `UserORM`
- Índice em `email` para performance de consultas
- `IF NOT EXISTS` para execução idempotente

### 3. Configuração de Permissões do Banco

**Comandos executados**:
```bash
# Criar usuário para localhost
docker exec smartsaude-mysql mysql -uroot -prootpass -e "CREATE USER 'smartuser'@'localhost' IDENTIFIED BY 'smartpass'; GRANT ALL PRIVILEGES ON smartsaude.* TO 'smartuser'@'localhost'; FLUSH PRIVILEGES;"

# Garantir permissões para host %
docker exec smartsaude-mysql mysql -uroot -prootpass -e "GRANT ALL PRIVILEGES ON smartsaude.* TO 'smartuser'@'%'; FLUSH PRIVILEGES;"
```

**Justificativa técnica**:
- MySQL diferencia entre `smartuser@'localhost'` e `smartuser@'%'`
- Aplicação local conecta como `localhost`
- Container Docker conecta como `%` (qualquer host)

### 4. Estratégia de Deploy

#### Modo Docker (Produção/Recomendado)
```bash
docker compose up -d
```

**Vantagens**:
- Ambiente isolado e reproduzível
- Configuração consistente
- Networking Docker resolvido automaticamente

#### Modo Local (Desenvolvimento)
```bash
docker compose down
cd auth-service
source .venv/bin/activate
python -m uvicorn main:app --reload --port 8080
```

**Requisitos**:
- MySQL local instalado
- Permissões configuradas
- Virtual environment ativada

## Arquitetura da Solução

### Fluxo de Autenticação Corrigido

```
POST /auth/register
    ↓
FastAPI Router
    ↓
AuthService.register()
    ↓
UserRepository.create()
    ↓
SQLAlchemy Session
    ↓
PyMySQL + cryptography
    ↓
MySQL 8.0 (caching_sha2_password)
    ↓
INSERT INTO users (email, password_hash)
    ↓
Response: {"user_id": N}
```

### Componentes Envolvidos

1. **FastAPI**: Framework web com tratamento de erros
2. **SQLAlchemy**: ORM para interação com banco
3. **PyMySQL**: Driver MySQL para Python
4. **cryptography**: Biblioteca de criptografia para autenticação
5. **MySQL 8.0**: Banco de dados com autenticação moderna

## Testes de Validação

### 1. Health Check
```bash
curl http://127.0.0.1:8080/auth/health
# Response: {"status":"ok","service":"auth-service"}
# Status: 200
```

### 2. Registro de Usuário
```bash
curl -X POST http://127.0.0.1:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
# Response: {"user_id":9}
# Status: 200
```

### 3. Validação de Duplicados
```bash
curl -X POST http://127.0.0.1:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
# Response: {"error":{"code":"CONFLICT","message":"email already exists"}}
# Status: 409
```

## Lições Aprendidas

### 1. Compatibilidade MySQL 8.0
- Autenticação `caching_sha2_password` é padrão
- Requer dependências específicas (`cryptography`)
- Configuração de usuários mais complexa

### 2. Importância do Logging Estruturado
- Erros genéricos dificultam diagnóstico
- Stack traces essenciais para debugging
- Logging em diferentes níveis (INFO, ERROR, DEBUG)

### 3. Gestão de Ambientes
- Docker vs local requer configurações diferentes
- Variáveis de ambiente para flexibilidade
- Documentação clara para cada modo de execução

### 4. Dependências Críticas
- `cryptography` não era óbvio no requirements inicial
- Testes de integração poderiam detectar o problema
- Verificação de dependências de segurança

## Recomendações Futuras

### 1. Melhorias de Infraestrutura
```yaml
# docker-compose.yml melhorias
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/auth/health"]
  interval: 30s
  timeout: 10s
  retries: 3
```

### 2. Monitoramento e Observabilidade
- Logs estruturados com correlation IDs
- Métricas de latência e erro rate
- Alertas para falhas de autenticação

### 3. Testes Automatizados
```python
# Testes de integração
def test_register_user():
    response = client.post("/auth/register", json={
        "email": "test@example.com",
        "password": "test123"
    })
    assert response.status_code == 200
    assert "user_id" in response.json()
```

### 4. Documentação Operacional
- Playbooks para troubleshooting
- Diagramas de arquitetura atualizados
- Runbooks para deploy e rollback

## Conclusão

O problema foi resolvido através de uma abordagem sistemática que identificou e corrigiu múltiplas camadas de falha:

1. **Dependências**: Adição do pacote `cryptography`
2. **Banco de dados**: Criação da tabela `users`
3. **Permissões**: Configuração adequada do usuário MySQL
4. **Deploy**: Estratégia clara para Docker vs local

O sistema agora opera com 100% de funcionalidade, com endpoints respondendo corretamente e tratamento adequado de erros. A solução não apenas corrigiu o problema imediato, mas também melhorou a robustez e maintainability do sistema.
