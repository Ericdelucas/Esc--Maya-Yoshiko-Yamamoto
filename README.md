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

<h3>Entregas</h3>
<pre>

</pre>

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
├── auth-service/          # 🏛️ Serviço Central de Autenticação
├── training-service/      # 💪 Serviço de Treinamento e Progresso
├── ehr-service/          # 📋 Serviço de Prontuário Eletrônico
├── exercise-service/     # 🏃‍♂️ Serviço de Exercícios e Mídia
├── ai-service/          # 🤖 Serviço de IA e Análise
├── health-service/      # 🏥 Serviço de Ferramentas de Saúde
├── notification-service/ # 📬 Serviço de Notificações
├── analytics-service/   # 📊 Serviço de Análises
└── shared/             # 🔧 Componentes Compartilhados
</pre>

## 🛠Instalação

<h3><b>Android:</b></h3>
<h3><p>https://drive.google.com/file/d/1bZhjURfa8ue7h2TGKx2VhKunDvap8EEC/view?usp=sharing<p></h3>

## 💻 Como rodar o projeto?

<h3><p>✅ Ferramentas necessárias<p></h3>
<li>Android Studio</li>
<li>Visual Studio Code</li>
<li>Java 11+</li>
<li>python 3(backend)</li>
<li>MySQL</li>
<br>

## 💻 Configuração para Desenvolvimento

Este guia explica como preparar e rodar o projeto SmartSaúde AI em ambiente local.

---

## 🗂️ Caminho do projeto

```
git clone https://github.com/Ericdelucas/Esc--Maya-Yoshiko-Yamamoto.git
cd Esc--Maya-Yoshiko-Yamamoto
cd Backend
````

## 🚀 Inicie os serviços com Docker (Recomendado)

Na pasta `Backend`, execute:

```bash
docker-compose up --build
```

Para rodar em background (detached mode):
```bash
docker-compose up -d --build
```

Para parar os serviços:
```bash
docker-compose down
```
<br>

## 🌐 Acesse os serviços

| Serviço | URL | Health Check |
|---------|-----|--------------|
| Auth Service | http://localhost:8080 | http://localhost:8080/health |
| Exercise Service | http://localhost:8081 | http://localhost:8081/health |
| Training Service | http://localhost:8030 | http://localhost:8030/health |
| EHR Service | http://localhost:8060 | http://localhost:8060/health |
| AI Service | http://localhost:8090 | http://localhost:8090/health |
| Health Service | http://localhost:8070 | http://localhost:8070/health |
| Notification Service | http://localhost:8040 | http://localhost:8040/health |
| Analytics Service | http://localhost:8050 | http://localhost:8050/health |


---
<br>

## 🎥Demonstração do Projeto
<p>Assista ao vídeo abaixo para uma demonstração completa das funcionalidades do sistema, incluindo a criação de participantes, equipes e atividades.</p>

<h3>conta do paciente</h3>
https://drive.google.com/file/d/1MjeqjMs6TLmSSI7D7qzJa9heRKmzGFyZ/view?usp=sharing

<h3>conta do profissional</h3>
https://drive.google.com/file/d/1cFt2RKfjQtuLXf7qkEg2YvvB7zD0ppAB/view?usp=sharing
<br>

## 📱Configuração do Frontend (Android)

O frontend do projeto é um aplicativo Android.

### Pré-requisitos:
- Android Studio (https://developer.android.com/studio)
- Java JDK 17 ou superior
- Android SDK

### Abra o projeto no Android Studio:
1. Abra o Android Studio
2. Selecione "Open an existing project"
3. Navegue até: `Esc--Maya-Yoshiko-Yamamoto\front\Esc--Maya-Yoshiko-Yamamoto\testbackend`
4. Aguarde o Gradle sincronizar

### Configure a URL da API:
No código do app, configure o IP da máquina para conectar ao backend:

```java
// Exemplo em Constants.kt ou similar
const val BASE_URL = "http://SEU_IP:8080/"
```

> **Nota:** Use o IP da sua máquina na rede local, não `localhost`, pois o emulador/Android tem seu próprio localhost.

### Execute o app:
- Clique no botão "Run" (▶️) no Android Studio
- Selecione um emulador ou dispositivo físico


---
<br>
## Rotas da API 🚀

<h3>Arquitetura de Microserviços</h3>
<table>
  <thead>
    <tr>
      <th align="left">Serviço</th>
      <th align="left">Porta</th>
      <th align="left">Descrição</th>
    </tr>
  </thead>
  <tbody>
    <tr><td><code>auth-service</code></td><td>8080</td><td>Autenticação e gestão de usuários</td></tr>
    <tr><td><code>exercise-service</code></td><td>8081</td><td>Gestão de exercícios</td></tr>
    <tr><td><code>training-service</code></td><td>8030</td><td>Planos de treino e gamificação</td></tr>
    <tr><td><code>ehr-service</code></td><td>8060</td><td>Prontuário eletrônico (EHR)</td></tr>
    <tr><td><code>ai-service</code></td><td>8090</td><td>Inteligência artificial e pose detection</td></tr>
    <tr><td><code>health-service</code></td><td>8070</td><td>Métricas de saúde (IMC, gordura corporal)</td></tr>
  </tbody>
</table>
<br>

## 📋Licença/License
<h3><p><a href="https://github.com/Ericdelucas/Esc--Maya-Yoshiko-Yamamoto.git">ESC</a> © 2026 by <a href="https://www.linkedin.com/in/eric-de-lucas-silva/">Eric de Lucas Silva, Stephanie Macedo da Silva e En Hsiang Chien</a> is licensed under <a href="https://creativecommons.org/licenses/by/4.0/">CC BY 4.0</a><img src="https://mirrors.creativecommons.org/presskit/icons/cc.svg" alt="" style="max-width: 1em;max-height:1em;margin-left: .2em;"><img src="https://mirrors.creativecommons.org/presskit/icons/by.svg" alt="" style="max-width: 1em;max-height:1em;margin-left: .2em;"><p></h3>

## 🎓Referências

Aqui estão as referências usadas no projeto.

1. <https://mayayamamoto.com.br/>
2. <https://www.linkedin.com/in/maya-yoshiko-yamamoto-bb18a736/.org/>
3. <https://www.instagram.com/rpg.maya/>