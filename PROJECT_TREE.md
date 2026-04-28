# SmartSaúde - Estrutura do Projeto

## 📁 Visão Geral

```
Esc--Maya-Yoshiko-Yamamoto/
├── 📱 Frontend (Android)
│   └── front/Esc--Maya-Yoshiko-Yamamoto/testbackend/
│       ├── 📱 app/src/main/                 # Código principal Android
│       │   ├── java/com/example/testbackend/
│       │   │   ├── 🎯 MainActivity.java
│       │   │   ├── 👤 ProfileActivity.java
│       │   │   ├── 🏥 ProfessionalMainActivity.java
│       │   │   ├── 🔐 LoginActivity.java
│       │   │   ├── 📋 PatientHealthDetailsActivity.java
│       │   │   └── 📚 models/                # Models de dados
│       │   ├── 🎨 res/layout/               # Layouts XML
│       │   └── 🎨 res/values/               # Recursos Android
│       ├── 📦 gradle/                       # Configuração Gradle
│       └── 📦 build/                        # Build artifacts
│
└── 🖥️ Backend (Microserviços FastAPI)
    └── Backend/
        ├── 🔐 auth-service/                  # Serviço de Autenticação
        │   ├── 📱 app/
        │   │   ├── 🧠 core/                  # Configuração central
        │   │   ├── 🗄️ models/                # ORM Models
        │   │   ├── 🛣️ routers/               # Endpoints API
        │   │   ├── 🔧 services/              # Lógica de negócio
        │   │   └── 💾 storage/               # Database layer
        │   ├── 📊 migrations/                # Migrações PostgreSQL
        │   └── 📁 storage/profile_photos/    # Upload de fotos
        │
        ├── 🤖 ai-service/                    # Serviço de IA
        │   ├── 📱 app/
        │   │   ├── 🧠 core/                  # Configuração Ollama
        │   │   ├── 🗄️ models/                # Schemas de dados
        │   │   ├── 🛣️ routers/               # Chat, Pose, WebSocket
        │   │   ├── 🔧 services/              # LLM, Pose Detection
        │   │   ├── 🤖 agents/                # Agentes de IA
        │   │   ├── 💬 prompts/               # Prompts do sistema
        │   │   └── 💾 storage/               # Arquivos de mídia
        │   └── 📦 requirements.txt            # Dependências Python
        │
        ├── 🏥 health-service/                # Serviço de Saúde
        │   ├── 📱 app/
        │   │   ├── 🗄️ models/                # Health data models
        │   │   ├── 🛣️ routers/               # Health endpoints
        │   │   ├── 🔧 services/              # Health calculations
        │   │   └── 💾 storage/               # Health data storage
        │
        ├── 💪 exercise-service/              # Serviço de Exercícios
        │   ├── 📱 app/
        │   │   ├── 🤖 agents/                # Exercise agents
        │   │   ├── 🗄️ models/                # Exercise models
        │   │   ├── 🛣️ routers/               # Exercise endpoints
        │   │   ├── 🔧 services/              # Exercise logic
        │   │   └── 💾 storage/               # Exercise data
        │
        ├── 📊 analytics-service/             # Serviço de Analytics
        │   ├── 📱 app/
        │   │   ├── 🧠 core/                  # Analytics core
        │   │   ├── 🗄️ models/                # Analytics models
        │   │   ├── 🛣️ routers/               # Analytics endpoints
        │   │   ├── 🔧 services/              # Analytics logic
        │   │   └── 💾 storage/               # Analytics data
        │
        ├── 🏥 ehr-service/                   # Electronic Health Records
        │   ├── 📱 app/
        │   │   ├── 🧠 core/                  # EHR core
        │   │   ├── 🗄️ models/                # EHR models
        │   │   ├── 📁 repositories/          # Data repositories
        │   │   ├── 🛣️ routers/               # EHR endpoints
        │   │   ├── 🔧 services/              # EHR services
        │   │   ├── 💾 storage/               # EHR storage
        │   │   └── 🛠️ utils/                # Utilities
        │
        ├── 🔔 notification-service/          # Serviço de Notificações
        │   ├── 📱 app/
        │   │   ├── 🧠 core/                  # Notification core
        │   │   ├── 🗄️ models/                # Notification models
        │   │   ├── 🛣️ routers/               # Notification endpoints
        │   │   ├── 🔧 services/              # Notification logic
        │   │   └── 💾 storage/               # Notification storage
        │
        ├── 🎯 training-service/              # Serviço de Treinamento
        │   ├── 📱 app/
        │   │   ├── 🧠 core/                  # Training core
        │   │   ├── 🗄️ models/                # Training models
        │   │   ├── 📁 repositories/          # Training repos
        │   │   ├── 🛣️ routers/               # Training endpoints
        │   │   ├── 🔧 services/              # Training logic
        │   │   └── 💾 storage/               # Training data
        │
        ├── 🔐 shared/                        # Módulos Compartilhados
        │   ├── 🔒 security/                  # Segurança compartilhada
        │   └── 📚 __pycache__/               # Cache Python
        │
        ├── 🗄️ database/                      # Configuração Database
        │   └── 📊 migrations/                # Migrações gerais
        │
        ├── 🧪 tests/                         # Testes Backend
        │   └── 📚 __pycache__/               # Cache de testes
        │
        └── 📦 .venv/                         # Ambiente Virtual Python
```

## 🏗️ Arquitetura de Microserviços

### **🔐 Auth Service** (Porta 8080)
- **Função:** Autenticação, usuários, JWT
- **Database:** PostgreSQL (Render/Supabase)
- **Endpoints:** `/auth/login`, `/auth/register`, `/auth/me`, `/auth/profile/photo`

### **🤖 AI Service** (Porta 8090)
- **Função:** Chat com IA, detecção de pose, TTS
- **Tech:** Ollama LLM, MediaPipe, OpenCV, gTTS
- **Endpoints:** `/ai/chat`, `/ai/translate`, WebSocket pose

### **🏥 Health Service** (Porta 8081)
- **Função:** Cálculos de saúde (IMC, pressão, etc.)
- **Tech:** FastAPI, cálculos médicos
- **Endpoints:** `/health/bmi`, `/health/pressure`

### **💪 Exercise Service** (Porta 8082)
- **Função:** Gestão de exercícios, progresso
- **Tech:** FastAPI, agentes de IA
- **Endpoints:** `/exercises`, `/progress`

### **📊 Analytics Service** (Porta 8083)
- **Função:** Análises de dados, dashboards
- **Tech:** FastAPI, analytics engine
- **Endpoints:** `/analytics/dashboard`

### **🏥 EHR Service** (Porta 8084)
- **Função:** Prontuários eletrônicos
- **Tech:** FastAPI, medical records
- **Endpoints:** `/ehr/records`, `/ehr/patients`

### **🔔 Notification Service** (Porta 8085)
- **Função:** Sistema de notificações
- **Tech:** FastAPI, push notifications
- **Endpoints:** `/notifications/send`, `/notifications/pending`

### **🎯 Training Service** (Porta 8086)
- **Função:** Planos de treinamento
- **Tech:** FastAPI, ML models
- **Endpoints:** `/training/plans`, `/training/progress`

## 📱 Android App Structure

### **🎯 Activities Principais**
- **MainActivity:** Dashboard paciente
- **ProfessionalMainActivity:** Dashboard profissional
- **LoginActivity:** Login/registro
- **ProfileActivity:** Perfil do usuário

### **🛣️ Network Layer**
- **ApiClient:** Configuração Retrofit
- **AuthApi:** Endpoints de autenticação
- **HealthApi:** Endpoints de saúde
- **NotificationApi:** Sistema de notificações

### **📦 Models Android**
- **UserProfileResponse:** Dados do perfil
- **HealthResponse:** Dados de saúde
- **NotificationModel:** Notificações

## 🗄️ Database Architecture

### **PostgreSQL Tables**
- **users:** Usuários do sistema
- **appointments:** Agendamentos
- **patient_evaluations:** Avaliações
- **patient_reports:** Relatórios
- **tasks:** Tarefas/exercícios
- **health_tools:** Ferramentas de saúde

### **ORM Models (SQLAlchemy)**
- **UserORM:** Modelo de usuário
- **AppointmentORM:** Modelo de agendamento
- **PatientEvaluationORM:** Avaliações
- **PatientReportORM:** Relatórios
- **TaskORM:** Tarefas
- **HealthToolsORM:** Ferramentas de saúde

## 🚀 Tecnologias Utilizadas

### **Backend**
- **FastAPI:** Framework web moderno
- **SQLAlchemy 2.0+:** ORM avançado
- **PostgreSQL:** Banco de dados principal
- **Pydantic:** Validação de dados
- **JWT:** Autenticação stateless
- **WebSocket:** Comunicação real-time

### **IA & Processamento**
- **Ollama:** LLM local (Llama 3, Qwen)
- **OpenCV:** Processamento de imagem
- **MediaPipe:** Análise de pose
- **gTTS:** Text-to-Speech
- **NumPy:** Cálculos matemáticos

### **Frontend Android**
- **Java/Kotlin:** Linguagens nativas
- **Retrofit:** Client HTTP
- **Picasso:** Carregamento de imagens
- **Material Design:** UI/UX
- **WebSocket:** Comunicação real-time

### **Infraestrutura**
- **Docker:** Containerização
- **Render.com:** Hospedagem produção
- **Supabase:** PostgreSQL as a Service
- **GitHub:** Versionamento
- **Gradle:** Build Android

## 📊 Estatísticas do Projeto

- **🏗️ 8 microserviços** independentes
- **📱 1 app Android** completo
- **🗄️ 6 tabelas PostgreSQL** principais
- **🔗 50+ endpoints** REST API
- **🤖 3 modelos de LLM** integrados
- **📦 300+ arquivos** no total
- **🌐 Deploy produção** no Render

## 📱 Download do Aplicativo

### **🔗 APK Debug para Teste**

Para instalar o aplicativo SmartSaúde no seu dispositivo Android:

1. **Baixe o APK:**
   ```
   📦 [testbackend-debug.apk](./testbackend-debug.apk)
   ```

2. **Instale no Android:**
   - Ative "Fontes desconhecidas" nas configurações
   - Clique no arquivo APK baixado
   - Siga as instruções de instalação

3. **Configure o Ambiente:**
   - URL Base: `https://esc-maya-yoshiko-yamamoto.onrender.com`
   - Crie uma conta ou faça login
   - Teste todas as funcionalidades

### **📋 Versão e Informações**
- **Versão:** Debug Build
- **Tamanho:** ~8.5 MB
- **Mínimo Android:** API 21+
- **Recursos:** Câmera, Storage, Internet

---

**SmartSaúde © 2026** - Sistema de saúde inteligente com IA integrada
