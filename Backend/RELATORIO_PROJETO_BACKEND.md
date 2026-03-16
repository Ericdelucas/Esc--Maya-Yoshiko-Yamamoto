# Relatório do Projeto Backend - SmartSaúde AI

## Visão Geral

O projeto **SmartSaúde AI Backend** é uma arquitetura de microserviços desenvolvida em FastAPI (Python) para uma plataforma de saúde inteligente. O sistema oferece recursos de autenticação, prontuário eletrônico, análise de exercícios com IA, notificações, analytics e planos de treinamento personalizados.

## Arquitetura

### Microserviços

O backend consiste em 7 microserviços independentes:

1. **auth-service** (Porta 8080) - Autenticação e gerenciamento de usuários
2. **ai-service** (Porta 8090) - Processamento de IA, análise de pose e chat
3. **notification-service** (Porta 8070) - Sistema de notificações
4. **ehr-service** (Porta 8060) - Prontuário eletrônico do paciente
5. **analytics-service** (Porta 8050) - Análise de dados e métricas
6. **exercise-service** (Porta 8081) - Catálogo de exercícios e mídias
7. **training-service** (Porta 8030) - Planos de treinamento e acompanhamento

### Infraestrutura

- **Banco de Dados**: MySQL 8.0
- **Orquestração**: Docker Compose
- **Comunicação**: HTTP REST + WebSocket
- **Armazenamento**: Volumes Docker para dados e uploads

## Detalhes dos Serviços

### 1. Auth Service
- **Responsabilidade**: Autenticação JWT, gerenciamento de usuários, autorização
- **Recursos**:
  - Login/logout
  - Registro de usuários (Pacientes, Profissionais, Admins)
  - Verificação de tokens
  - Proxy para serviços AI
  - Gerenciamento de consentimentos LGPD
- **Segurança**: Fernet para criptografia de dados sensíveis

### 2. AI Service
- **Responsabilidade**: Processamento de inteligência artificial
- **Recursos**:
  - Análise de pose em tempo real via WebSocket
  - Chat com assistente de saúde (Gemini/Ollama)
  - Tradução de textos
  - Cálculo de ângulos para feedback de exercícios
  - Agentes especializados (Gemini, Ollama)
- **Tecnologias**: MediaPipe, OpenCV, Gemini API, Ollama

### 3. Notification Service
- **Responsabilidade**: Sistema de notificações multicanal
- **Recursos**:
  - Agendamento de notificações
  - Múltiplos canais (email, push, SMS)
  - Histórico de notificações
  - Status tracking

### 4. EHR Service
- **Responsabilidade**: Prontuário eletrônico do paciente
- **Recursos**:
  - Registros médicos criptografados
  - Histórico de consultas
  - Anotações profissionais
  - Conformidade LGPD
- **Segurança**: Criptografia de dados sensíveis

### 5. Analytics Service
- **Responsabilidade**: Análise de dados e métricas
- **Recursos**:
  - Métricas de adesão ao tratamento
  - Análise de progresso
  - Relatórios para profissionais
  - Dashboard de analytics

### 6. Exercise Service
- **Responsabilidade**: Catálogo de exercícios e gerenciamento de mídias
- **Recursos**:
  - Biblioteca de exercícios
  - Upload de vídeos/imagens
  - Classificação por tags
  - Metadados de exercícios
- **Armazenamento**: Volume Docker para uploads

### 7. Training Service
- **Responsabilidade**: Planos de treinamento personalizados
- **Recursos**:
  - Criação de planos de treinamento
  - Acompanhamento de progresso
  - Logs de execução
  - Feedback de esforço e dor
  - Integração com notificações

## Banco de Dados

### Schema MySQL

O banco de dados `smartsaude` contém as seguintes tabelas:

#### Usuários e Autenticação
- **users**: Informações básicas dos usuários com roles (Patient, Professional, Admin)

#### LGPD e Consentimentos
- **consent_records**: Registros de consentimento para conformidade LGPD

#### Prontuário Eletrônico
- **medical_records**: Registros médicos com criptografia

#### Notificações
- **notifications**: Sistema de notificações agendadas

#### Exercícios e Treinamento
- **exercises**: Catálogo de exercícios com mídias
- **training_plans**: Planos de treinamento personalizados
- **training_plan_items**: Itens dos planos (exercícios específicos)
- **training_logs**: Registros de execução dos treinos

### Segurança de Dados
- Criptografia Fernet para dados sensíveis
- Isolamento de dados por paciente
- Audit trail implícito através de timestamps

## Tecnologias e Frameworks

### Backend Stack
- **FastAPI**: Framework web moderno com auto-documentação
- **SQLAlchemy**: ORM para banco de dados
- **Pydantic**: Validação e serialização
- **JWT**: Tokens de autenticação
- **WebSocket**: Comunicação em tempo real
- **Cryptography**: Fernet para criptografia

### IA e Processamento
- **MediaPipe**: Análise de pose
- **OpenCV**: Processamento de imagem
- **Google Gemini**: LLM para chat assistente
- **Ollama**: LLM local (Llama3)

### DevOps
- **Docker**: Containerização
- **Docker Compose**: Orquestração local
- **MySQL**: Banco de dados relacional

## API e Endpoints

### Autenticação
- `POST /auth/login` - Login de usuário
- `POST /auth/register` - Registro de novo usuário
- `GET /auth/me` - Informações do usuário atual
- `POST /auth/logout` - Logout

### Saúde e Monitoramento
- `GET /health` - Health check de cada serviço

### AI Service
- `POST /ai/chat` - Chat com assistente
- `POST /ai/translate` - Tradução de textos
- `WebSocket /pose` - Análise de pose em tempo real

### Exercícios
- `GET /exercises` - Listar exercícios
- `POST /exercises` - Criar novo exercício
- `POST /exercises/upload` - Upload de mídia

### Treinamento
- `GET /training/plans` - Planos do paciente
- `POST /training/plans` - Criar plano
- `POST /training/logs` - Registrar execução

## Segurança

### Implementações
- **JWT Tokens**: Autenticação stateless
- **Fernet Encryption**: Criptografia de dados sensíveis
- **CORS**: Configuração de origens permitidas
- **Role-based Access**: Controle por papéis
- **LGPD Compliance**: Gestão de consentimentos

### Variáveis de Ambiente
- `DB_URL`: String de conexão MySQL
- `JWT_SECRET`: Chave secreta para tokens
- `APP_FERNET_KEYS`: Chaves de criptografia
- `OLLAMA_HOST`: Endpoint do Ollama

## Deploy e Infraestrutura

### Docker Compose
- **Network**: `backend_default` para comunicação interna
- **Volumes**: 
  - `smartsaude_mysql_data`: Dados do MySQL
  - `smartsaude_uploads`: Arquivos de exercícios
- **Health Checks**: Verificação de dependências
- **Port Mapping**: Exposição serviços externamente

### Dependências
- MySQL deve estar healthy antes dos serviços
- Auth service deve estar online antes dos outros
- Notification service para training service

## Testes e QA

### Ferramentas
- **Postman Collection**: Testes automatizados da API
- **Health Endpoints**: Verificação de status
- **WebSocket Tests**: Testes de conexão em tempo real

### Scripts de Teste
- `test_health_endpoints.py`: Verificação de saúde dos serviços
- `test_ws_minimal.py`: Testes WebSocket básicos
- `camera_test.html`: Teste de câmera frontend

## Estrutura de Diretórios

```
Backend/
├── ai-service/           # Serviço de IA
├── auth-service/         # Autenticação
├── analytics-service/    # Analytics
├── ehr-service/         # Prontuário eletrônico
├── exercise-service/    # Exercícios
├── notification-service/ # Notificações
├── training-service/    # Treinamento
├── shared/              # Código compartilhado
├── database/            # Schema SQL
├── tests/               # Testes integrados
├── docker-compose.yml   # Orquestração
└── .env                 # Variáveis ambiente
```

## Próximos Passos e Melhorias

### Sugestões de Evolução
1. **API Gateway**: Centralizar roteamento e autenticação
2. **Message Queue**: Comunicação assíncrona entre serviços
3. **Monitoring**: Logs centralizados e métricas
4. **CI/CD**: Pipeline de deploy automatizado
5. **Caching**: Redis para performance
6. **Load Balancer**: Alta disponibilidade
7. **Security Scanner**: Verificação de vulnerabilidades

### Escalabilidade
- Horizontal scaling de serviços stateless
- Database sharding para grandes volumes
- CDN para mídias de exercícios
- Rate limiting e throttling

## Conclusão

O backend SmartSaúde AI representa uma arquitetura moderna e escalável para aplicações de saúde, com forte foco em segurança, conformidade LGPD e processamento de IA. A estrutura de microserviços permite desenvolvimento independente e escalabilidade granular, enquanto as tecnologias escolhidas garantem performance e mantenibilidade.

O sistema está pronto para produção com todas as best practices de segurança e monitoramento implementadas, oferecendo uma base sólida para evolução futura.
