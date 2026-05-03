# SmartSaúde Backend - Estrutura Completa do Projeto

## 📋 Visão Geral

O SmartSaúde é uma arquitetura de microserviços em FastAPI/Python para uma plataforma de saúde digital, com foco em autenticação, treinamento, avaliações de pacientes, e IA para análise de exercícios.

---

## 🏗️ Arquitetura de Microserviços

### **Serviços Principais**

```
Backend/
├── auth-service/          # 🏛️ Serviço Central de Autenticação
├── training-service/      # 💪 Serviço de Treinamento e Progresso
├── ehr-service/          # 📋 Serviço de Prontuário Eletrônico
├── exercise-service/     # 🏃‍♂️ Serviço de Exercícios e Mídia
├── ai-service/          # 🤖 Serviço de IA e Análise
├── health-service/      # 🏥 Serviço de Ferramentas de Saúde
├── notification-service/ # 📬 Serviço de Notificações
├── analytics-service/   # 📊 Serviço de Análises
└── shared/             # 🔧 Componentes Compartilhados
```

---

## 🏛️ auth-service - Serviço Central

**Porta:** 8080 | **Função:** Autenticação, gerenciamento de usuários, orquestração

### 📁 Estrutura Interna
```
auth-service/
├── app/
│   ├── core/
│   │   ├── config.py              # ⚙️ Configurações (PostgreSQL, JWT)
│   │   ├── dependencies.py        # 🔐 Dependências de autenticação
│   │   ├── error_handler.py       # 🚨 Tratamento global de erros
│   │   ├── jwt_service.py         # 🎫 Serviço de tokens JWT
│   │   ├── rbac.py               # 🛡️ Role-Based Access Control
│   │   ├── security.py           # 🔒 Hashing e criptografia
│   │   └── notification_client.py # 📬 Cliente de notificações
│   ├── models/
│   │   ├── domain/               # 📋 Modelos de domínio
│   │   │   └── user.py
│   │   ├── orm/                  # 🗄️ Models SQLAlchemy
│   │   │   ├── user_orm.py
│   │   │   ├── appointment_orm.py
│   │   │   ├── patient_evaluation_orm.py
│   │   │   ├── patient_report_orm.py
│   │   │   ├── task_orm.py
│   │   │   └── health_tools_orm.py
│   │   └── schemas/              # 📝 Pydantic schemas
│   │       ├── user_schema.py
│   │       ├── token_schema.py
│   │       ├── dashboard_stats.py
│   │       └── [outros...]
│   ├── routers/                  # 🛤️ Endpoints FastAPI
│   │   ├── auth_router.py        # 🔐 Login, registro
│   │   ├── me_router.py          # 👤 Perfil do usuário
│   │   ├── admin_router.py       # 👑 Administração
│   │   ├── professional_router.py # 👨‍⚕️ Profissionais
│   │   ├── appointment_router.py # 📅 Agendamentos
│   │   ├── patient_evaluation_router.py # 📋 Avaliações
│   │   ├── patient_report_router.py     # 📄 Relatórios
│   │   ├── task_router.py        # ✅ Tarefas
│   │   ├── notification_router.py # 📬 Notificações
│   │   ├── health_tools_router.py # 🏥 Ferramentas de saúde
│   │   ├── patient_health_router.py # 🏥 Saúde do paciente
│   │   ├── patient_router.py     # 👥 Pacientes
│   │   ├── ai_proxy_router.py    # 🤖 Proxy para IA
│   │   └── health_router.py      # ❤️ Health check
│   ├── services/                 # 🔄 Lógica de negócio
│   │   ├── auth_service.py       # 🔐 Autenticação
│   │   ├── file_upload_service.py # 📁 Upload de arquivos
│   │   ├── task_service.py       # ✅ Gestão de tarefas
│   │   ├── health_tools_service.py # 🏥 Ferramentas de saúde
│   │   └── appointment_notification_service.py # 📅 Notificações
│   ├── storage/
│   │   └── database/             # 🗄️ Camada de dados
│   │       ├── db.py             # 🔌 Conexão PostgreSQL
│   │       ├── base_repository.py # 📋 Repositório base
│   │       ├── user_repository.py # 👥 Usuários
│   │       ├── appointment_repository.py # 📅 Agendamentos
│   │       └── [outros...]
│   └── logs/                     # 📝 Logs da aplicação
├── migrations/                   # 🔄 Migrações de banco
├── Dockerfile                    # 🐳 Container Docker
├── requirements.txt              # 📦 Dependências Python
└── main.py                      # 🚀 Entry point FastAPI
```

---

## 💪 training-service - Serviço de Treinamento

**Porta:** 8030 | **Função:** Planos de treino, progresso, desafios

### 📁 Estrutura Interna
```
training-service/
├── app/
│   ├── core/
│   │   ├── config.py             # ⚙️ Configurações
│   │   ├── rbac_client.py        # 🛡️ Cliente RBAC
│   │   ├── notification_client.py # 📬 Notificações
│   │   └── auth_dependencies.py   # 🔐 Validação de auth
│   ├── models/
│   │   ├── orm/                  # 🗄️ Models de treino
│   │   │   ├── training_orm.py
│   │   │   ├── training_plan_orm.py
│   │   │   ├── training_plan_item_orm.py
│   │   │   └── training_log_orm.py
│   │   ├── schemas/              # 📝 Schemas Pydantic
│   │   │   ├── training_schema.py
│   │   │   ├── goals_schema.py
│   │   │   ├── challenges_schema.py
│   │   │   ├── progress_schema.py
│   │   │   └── leaderboard_schema.py
│   │   └── ideal_angles_models.py # 📐 Modelos de ângulos
│   ├── repositories/             # 📋 Repositórios especializados
│   │   ├── training_plan_repository.py
│   │   ├── progress_repository.py
│   │   ├── challenges_repository.py
│   │   └── leaderboard_repository.py
│   ├── routers/                  # 🛤️ Endpoints
│   │   ├── training_router.py    # 💪 Treinos
│   │   ├── goals_router.py       # 🎯 Metas
│   │   ├── challenges_router.py  # 🏆 Desafios
│   │   ├── progress_router.py    # 📈 Progresso
│   │   ├── leaderboard_router.py # 🏅 Classificação
│   │   ├── exercises_router.py   # 🏃‍♂️ Exercícios
│   │   ├── me_router.py          # 👤 Perfil
│   │   └── health_router.py      # ❤️ Health check
│   ├── services/                 # 🔄 Lógica de negócio
│   │   ├── training_service.py   # 💪 Serviço de treinos
│   │   ├── goals_service.py      # 🎯 Metas
│   │   ├── challenges_service.py # 🏆 Desafios
│   │   ├── progress_service.py   # 📈 Progresso
│   │   ├── leaderboard_service.py # 🏅 Classificação
│   │   ├── ideal_angles_service.py # 📐 Ângulos ideais
│   │   └── reminder_scheduler_service.py # ⏰ Lembretes
│   └── storage/
│       ├── database/             # 🗄️ Camada de dados
│       │   ├── db.py             # 🔌 Conexão
│       │   ├── base_repository.py # 📋 Base
│       │   └── [repositórios específicos]
│       └── ideal_angles_repository.py # 📐 Ângulos
├── Dockerfile                    # 🐳 Container
├── requirements.txt              # 📦 Dependências
└── main.py                      # 🚀 Entry point
```

---

## 📋 ehr-service - Serviço de Prontuário Eletrônico

**Porta:** 8060 | **Função:** Prontuários médicos, documentos, consentimentos

### 📁 Estrutura Interna
```
ehr-service/
├── app/
│   ├── services/
│   │   ├── ehr_service.py        # 📋 Serviço principal
│   │   ├── consent_service.py    # 📝 Consentimentos
│   │   ├── patient_documents_service.py # 📄 Documentos
│   │   └── consent_guard_service.py   # 🛡️ Guardião de consentimentos
│   ├── storage/
│   │   └── database/             # 🗄️ Repositórios
│   │       ├── medical_record_repository.py # 📋 Prontuários
│   │       ├── consent_repository.py       # 📝 Consentimentos
│   │       └── base_repository.py         # 📋 Base
│   ├── models/
│   │   ├── orm/                  # 🗄️ Models
│   │   │   └── medical_record_orm.py
│   │   └── schemas/              # 📝 Schemas
│   │       ├── ehr_schema.py
│   │       ├── consent_schema.py
│   │       └── patient_document_schemas.py
├── Dockerfile                    # 🐳 Container
├── requirements.txt              # 📦 Dependências
└── main.py                      # 🚀 Entry point
```

---

## 🏃‍♂️ exercise-service - Serviço de Exercícios

**Porta:** 8081 | **Função:** Catálogo de exercícios, upload de mídia

### 📁 Estrutura Interna
```
exercise-service/
├── app/
│   ├── agents/                   # 🤖 Agentes especializados
│   │   └── local_storage_agent.py # 📁 Armazenamento local
│   ├── core/
│   │   ├── config.py             # ⚙️ Configurações
│   │   ├── rbac_client.py        # 🛡️ RBAC
│   │   └── paths.py             # 🛤️ Caminhos do sistema
│   ├── models/
│   │   ├── orm/                  # 🗄️ Models
│   │   │   └── exercise_orm.py
│   │   └── schemas/              # 📝 Schemas
│   │       └── exercise_schema.py
│   ├── routers/                  # 🛤️ Endpoints
│   │   ├── exercise_router.py    # 🏃‍♂️ Exercícios
│   │   └── health_router.py      # ❤️ Health check
│   ├── services/                 # 🔄 Lógica
│   │   ├── exercise_service.py   # 🏃‍♂️ Serviço de exercícios
│   │   ├── file_storage_service.py # 📁 Armazenamento
│   │   └── media_service.py      # 🎬 Mídia
│   └── storage/
│       └── database/             # 🗄️ Repositórios
│           ├── exercise_repository.py # 🏃‍♂️ Exercícios
│           └── base.py           # 📋 Base
├── Dockerfile                    # 🐳 Container
├── requirements.txt              # 📦 Dependências
└── main.py                      # 🚀 Entry point
```

---

## 🤖 ai-service - Serviço de Inteligência Artificial

**Porta:** 8090 | **Função:** Análise de pose, chat, tradução, processamento

### 📁 Estrutura Interna
```
ai-service/
├── app/
│   ├── agents/                   # 🤖 Agentes de IA
│   │   └── local_storage_agent.py # 📁 Armazenamento
│   ├── core/
│   │   ├── config.py             # ⚙️ Configurações (Ollama)
│   │   ├── dependencies.py       # 🔐 Dependências
│   │   ├── security.py           # 🔒 Segurança
│   │   ├── exceptions.py         # 🚨 Exceções
│   │   └── utils/                # 🛠️ Utilitários
│   │       ├── angle_calculator.py # 📐 Cálculo de ângulos
│   │       └── helpers.py        # 🔧 Auxiliares
│   ├── models/
│   │   ├── domain/               # 📋 Modelos de domínio
│   │   │   ├── chat_message.py
│   │   │   └── __init__.py
│   │   ├── orm/                  # 🗄️ Models (mínimo)
│   │   └── schemas/              # 📝 Schemas
│   │       ├── chat_schema.py
│   │       ├── translate_schema.py
│   │       └── __init__.py
│   ├── prompts/                  # 💬 Prompts de IA
│   │   ├── ollama_prompts.py     # 🦙 Prompts Ollama
│   │   ├── gemini_prompts.py     # 💎 Prompts Gemini
│   │   ├── smartsaude_assistant_prompt.py # 🏥 Assistente saúde
│   │   └── __init__.py
│   ├── routers/                  # 🛤️ Endpoints
│   │   ├── pose_router.py        # 🧍‍♂️ Análise de pose
│   │   ├── pose_ws_router.py     # 🌐 WebSocket pose
│   │   ├── pose_ws_router_simple.py # 🌐 WebSocket simplificado
│   │   ├── chat_router.py        # 💬 Chat
│   │   ├── translate_router.py   # 🌐 Tradução
│   │   ├── ws_probe_router.py    # 🔍 Probe WebSocket
│   │   └── health_router.py      # ❤️ Health check
│   ├── utils/                    # 🛠️ Utilitários
│   │   ├── helpers.py           # 🔧 Auxiliares
│   │   └── text_processing.py   # 📝 Processamento de texto
├── Dockerfile                    # 🐳 Container
├── requirements.txt              # 📦 Dependências
└── main.py                      # 🚀 Entry point
```

---

## 🏥 health-service - Serviço de Ferramentas de Saúde

**Porta:** Variável | **Função:** Cálculos de saúde (IMC, etc.)

### 📁 Estrutura Interna
```
health-service/
├── app/
│   ├── models/
│   │   └── orm/                  # 🗄️ Models de saúde
│   │       ├── health_models.py  # 🏥 Modelos de métricas
│   │       └── __init__.py
│   ├── services/
│   │   ├── health_service.py     # 🏥 Serviço principal
│   │   └── __init__.py
│   └── storage/
│       ├── database.py           # 🗄️ Conexão
│       └── __init__.py
├── Dockerfile                    # 🐳 Container
├── main.py                      # 🚀 Entry point
├── main_simple.py              # 🚀 Entry point simplificado
└── requirements.txt              # 📦 Dependências
```

---

## 📬 notification-service - Serviço de Notificações

**Porta:** 8070 | **Função:** Envio de notificações, agendamento

### 📁 Estrutura Interna
```
notification-service/
├── app/
│   ├── core/
│   │   └── config.py             # ⚙️ Configurações
│   ├── models/
│   │   ├── orm/                  # 🗄️ Models
│   │   │   └── notification_orm.py
│   │   └── schemas/              # 📝 Schemas
│   │       └── notification_schema.py
│   ├── routers/                  # 🛤️ Endpoints
│   │   ├── notification_router.py # 📬 Notificações
│   │   ├── notification_query_router.py # 📋 Consultas
│   │   ├── dispatch_router.py    # 📤 Envio
│   │   └── health_router.py      # ❤️ Health check
│   ├── services/                 # 🔄 Lógica
│   │   ├── notification_service.py # 📬 Serviço principal
│   │   └── dispatcher_service.py # 📤 Dispatcher
│   └── storage/
│       └── database/             # 🗄️ Repositórios
│           ├── notification_repository.py # 📬 Notificações
│           ├── base.py           # 📋 Base
│           └── db.py             # 🔌 Conexão
├── Dockerfile                    # 🐳 Container
├── requirements.txt              # 📦 Dependências
└── main.py                      # 🚀 Entry point
```

---

## 📊 analytics-service - Serviço de Análises

**Porta:** Variável | **Função:** Análises de dados, métricas

### 📁 Estrutura Interna
```
analytics-service/
├── app/
│   ├── core/
│   │   ├── config.py             # ⚙️ Configurações
│   │   └── rbac_client.py        # 🛡️ RBAC
│   ├── models/
│   │   └── orm/                  # 🗄️ Models (mínimo)
│   ├── routers/                  # 🛤️ Endpoints
│   │   └── health_router.py      # ❤️ Health check
│   └── services/                 # 🔄 Lógica
│       └── analytics_service.py  # 📊 Serviço principal
├── Dockerfile                    # 🐳 Container
├── requirements.txt              # 📦 Dependências
└── main.py                      # 🚀 Entry point
```

---

## 🔧 shared - Componentes Compartilhados

### 📁 Estrutura Interna
```
shared/
├── security/                     # 🔒 Módulos de segurança
│   ├── audit.py                 # 📝 Auditoria
│   ├── crypto_service.py        # 🔐 Serviços criptográficos
│   ├── dependencies.py          # 🔐 Dependências compartilhadas
│   ├── generate_key.py          # 🔑 Geração de chaves
│   ├── key_rotation.py          # 🔄 Rotação de chaves
│   ├── permissions.py           # 🛡️ Permissões
│   ├── rbac.py                  # 🛡️ RBAC compartilhado
│   ├── utils.py                 # 🛠️ Utilitários
│   └── validator.py             # ✅ Validadores
├── health_router.py              # ❤️ Health check compartilhado
└── __init__.py                   # 📋 Init
```

---

## 🗄️ Banco de Dados

### **PostgreSQL (Migrado de MySQL)**
- **Host:** Supabase/Render
- **Driver:** `postgresql+psycopg2`
- **ORM:** SQLAlchemy 2.0+
- **Migrações:** Automáticas via `Base.metadata.create_all()`

### **Principais Tabelas**
- `users` - Usuários e autenticação
- `appointments` - Agendamentos
- `patient_evaluations` - Avaliações
- `patient_reports` - Relatórios
- `tasks` - Tarefas
- `health_tools` - Ferramentas de saúde

---

## 🐳 Docker & Deploy

### **Docker Compose**
```yaml
# docker-compose.minimal.yml
services:
  mysql -> PostgreSQL (migrado)
  auth-service (8080)
  exercise-service (8081)
  training-service (8030)
  ehr-service (8060)
  ai-service (8090)
  notification-service (8070)
```

### **Render.com**
- **Runtime:** Python 3
- **Build:** `pip install -r requirements.txt`
- **Start:** `python main.py`
- **Database:** PostgreSQL via Supabase

---

## 📱 Integração Mobile

### **Android App**
- **Base URL:** `https://esc-maya-yoshiko-yamamoto.onrender.com/`
- **Timeouts:** 90 segundos (Render free tier)
- **WebSocket:** `wss://.../chat/ws/`
- **Security:** Network config atualizada

---

## 🔧 Configurações & Variáveis de Ambiente

### **Essenciais**
```bash
DATABASE_URL=postgresql+psycopg2://user:pass@host:5432/db
JWT_SECRET=secret_key_32_chars_min
PEPPER_KEY=pepper_for_hashing
APP_FERNET_KEYS=encryption_key
```

### **Serviços**
```bash
AUTH_BASE_URL=https://esc-maya-yoshiko-yamamoto.onrender.com/
NOTIF_BASE_URL=http://notification-service:8070
OLLAMA_HOST=http://host.docker.internal:11434
```

---

## 📝 Scripts & Ferramentas

### **Scripts de Desenvolvimento**
- `create_test_users.py` - Criação de usuários teste
- `test_health_endpoints.py` - Testes de saúde
- `diagnose_stack.sh` - Diagnóstico da stack
- `monitor_*.sh` - Monitores de saúde

### **Ferramentas**
- `SmartSaude.postman_collection.json` - Collection Postman
- `camera_test.html` - Teste de câmera
- `simular-app-android.py` - Simulador Android

---

## 🔄 Fluxo de Autenticação

1. **Login** → `auth-service` → Token JWT
2. **Validação** → RBAC em cada serviço
3. **API Calls** → Token Bearer required
4. **Refresh** → Token rotation automático

---

## 🚀 Tecnologias Utilizadas

### **Backend**
- **FastAPI** - Framework web moderno
- **SQLAlchemy 2.0+** - ORM avançado
- **PostgreSQL** - Banco de dados
- **Pydantic** - Validação de dados
- **JWT** - Autenticação stateless
- **WebSocket** - Comunicação real-time

### **IA & Processamento**
- **Ollama** - LLM local
- **OpenCV** - Processamento de imagem
- **MediaPipe** - Análise de pose
- **NumPy** - Cálculos matemáticos

### **Infraestrutura**
- **Docker** - Containerização
- **Render.com** - Hospedagem
- **Supabase** - PostgreSQL as a Service
- **GitHub Actions** - CI/CD

---

## 📊 Estatísticas do Projeto

- **96 diretórios**
- **310+ arquivos**
- **8 microserviços**
- **50+ endpoints**
- **15+ models de dados**
- **Migração MySQL → PostgreSQL concluída**

---

## 🔄 Próximos Passos

1. **Deploy completo** no Render
2. **Testes integrados** automatizados
3. **Monitoramento** com Prometheus
4. **CI/CD** com GitHub Actions
5. **Documentação** Swagger/OpenAPI

---

*Última atualização: Abril 2026*  
*Status: ✅ PostgreSQL migrado, pronto para deploy*
