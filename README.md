<!--  
Utilize o site <https://www.toptal.com/developers/gitignore> para gerar seu arquivo gitignore e apague este campo.

Vide tutoriais do PI.
``` -->

# FECAP - Fundação de Comércio Álvares Penteado

<p align="center">
<a href= "https://www.fecap.br/"><img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhZPrRa89Kma0ZZogxm0pi-tCn_TLKeHGVxywp-LXAFGR3B1DPouAJYHgKZGV0XTEf4AE&usqp=CAU" alt="FECAP - Fundação de Comércio Álvares Penteado" border="0"></a>
</p>

# SmartSaude

## ESC

## Integrantes: <a href="https://www.linkedin.com/in/eric-de-lucas-silva/">Eric De Lucas Silva</a>, <a href="https://www.linkedin.com/in/stephanie-silva-1b6100340/">Stephanie Macedo da Silva</a>, <a href="https://www.linkedin.com/in/en-hsiang-chien-53b550381/">En Hsiang Chien</a>.

## Professores Orientadores: <a href="https://www.linkedin.com/in/marco-aur%C3%A9lio-zanote-a5636123/?skipRedirect=true">Marco Aurelio Zanote</a>, <a href="https://www.linkedin.com/in/katia-bossi/">Katia Milani Lara Bossi</a>, <a href="https://www.linkedin.com/in/rodrigo-da-rosa-phd/">Rodrigo da Rosa</a>, <a href="https://www.linkedin.com/in/victorbarq/">Victor Bruno Alexander Rosetti de Quiroz</a>.

## Descrição

<table style="width: 100%; text-align: center;">
  <tr>
    <td align="center" valign="top">
      <a href="https://ibb.co/bgxV7RGn"><img src="https://i.ibb.co/KjYgmz8H/337-Sem-T-tulo-20260403154319.png" alt="Tela de login" border="none"></a>   
      </a>
      <p><b>Tela de Login</b></p>
    </td>
    <td align="center" valign="top">
      <a href="https://ibb.co/1fmz96yv"><img src="https://i.ibb.co/nsLPDCdg/1000146910.png" alt="Tela de Home(paciente)" border="none"></a>
      </a>
      <p><b>Tela Principal (Paciente)</b></p>
      </td>
    <td align="center" valign="top">
      <a href="https://ibb.co/B2vjs9M8"><img src="https://i.ibb.co/HTRxCskV/1000146911.png" alt="Tela de Home (profissional)" border="none"></a>
      </a>
      <p><b>Tela Principal (Profissional)</b></p>
    </td>
  </tr>
</table>

<br>

<p> A SmartSaude é uma plataforma desenvolvida para auxiliar no acompanhamento de tratamentos fisioterapêuticos, unindo um aplicativo mobile a um sistema backend responsável por gerenciar dados, usuários e funcionalidades. A proposta do projeto é facilitar o acesso do paciente aos exercícios e permitir o registro contínuo do seu progresso, tornando o processo mais organizado e eficiente.

Como diferencial, o sistema utiliza inteligência artificial para analisar os movimentos realizados durante os exercícios, ajudando a identificar se estão sendo executados corretamente. Dessa forma, o projeto contribui tanto para o paciente, que recebe orientações durante o uso, quanto para o profissional de saúde, que pode acompanhar a evolução de forma mais precisa e prática. </p>
<br>

## 🛠 Estrutura de pastas

<h3>Frontend</h3>
<pre>
Esc--Maya-Yoshiko-Yamamoto/
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
</pre>

<h3>Backend</h3>
<pre>
Backend/
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
</pre>

## 🛠 Instalação

<b>Android:</b>

https://drive.google.com/file/d/1bZhjURfa8ue7h2TGKx2VhKunDvap8EEC/view?usp=sharing

## 💻 Configuração para Desenvolvimento

Descreva como instalar todas as dependências para desenvolvimento e como rodar um test-suite automatizado de algum tipo. Se necessário, faça isso para múltiplas plataformas.

Para abrir este projeto você necessita das seguintes ferramentas:

-<a href="https://godotengine.org/download">GODOT</a>

```sh
make install
npm test
Coloque código do prompt de comnando se for necessário
```

## 📋 Licença/License
Utilize o link <https://chooser-beta.creativecommons.org/> para fazer uma licença CC BY 4.0.

## 🎓 Referências

Aqui estão as referências usadas no projeto.

1. <https://mayayamamoto.com.br/>
2. <https://www.linkedin.com/in/maya-yoshiko-yamamoto-bb18a736/.org/>
3. <https://www.instagram.com/rpg.maya/>
4. <https://www.toptal.com/developers/gitignore>
