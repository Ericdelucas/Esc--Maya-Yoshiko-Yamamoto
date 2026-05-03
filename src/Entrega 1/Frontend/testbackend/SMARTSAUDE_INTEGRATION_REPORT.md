# 🔄 RELATÓRIO DE INTEGRAÇÃO ATUALIZADO (V2): SmartSaúde SUS

Este documento serve como a única fonte de verdade para a integração entre o Frontend Android e o Backend (FastAPI). Todos os endpoints e modelos de dados abaixo foram alinhados conforme o relatório de alinhamento técnico.

---

## 1. Configuração de Rede (Constants.java)

O Frontend centraliza a configuração de rede na classe `utils/Constants.java`.

- **BASE_URL:** `http://10.0.2.2` (IP padrão para acessar o localhost do host pelo emulador).
- **Tráfego HTTP:** Permitido via `network_security_config.xml`.

### Mapeamento de Portas e Serviços
| Serviço | Porta | Endpoint Base | Status |
| :--- | :--- | :--- | :--- |
| **Auth Service** | `8080` | `/auth/` | ✅ Alinhado |
| **Exercise Service** | `8081` | `/exercises` | ✅ Corrigido (8040 -> 8081) |
| **AI Service** | `8090` | `/ai/` | ✅ Alinhado |
| **Analytics Service** | `8050` | `/analytics/` | ✅ Novo |

---

## 2. Especificações de API e Modelos de Dados

### 🔐 AUTH-SERVICE (`8080`)
- **Login (`POST /auth/login`):**
  - **Envio:** `email`, `password`.
  - **Resposta:**
    ```json
    {
      "access_token": "token_string",
      "token_type": "bearer",
      "user_role": "patient" // Chave exata esperada: user_role
    }
    ```
- **Registro (`POST /auth/register`):**
  - Campos: `name`, `email`, `password`, `role`.

### 💪 EXERCISE-SERVICE (`8081`)
- **Listagem (`GET /exercises`):**
  - **Nota:** Removido prefixo `/api/` conforme alinhamento.
  - **Campos do Modelo:** `id`, `name`, `description`, `category`, `video_url`.

### 🤖 AI-SERVICE (`8090`)
- **Processamento (`POST /ai/process-frame`):**
  - **Tipo:** `multipart/form-data`.
  - **Campo:** `image` (JPEG, 480x640, 70% qualidade).
  - **Resposta:**
    ```json
    {
      "landmarks": [{"x": f, "y": f, "z": f, "visibility": f}],
      "validation_status": "✔ Movimento Correto",
      "audio_feedback_url": "http://10.0.2.2:8090/ai/audio/feedback_uuid.mp3"
    }
    ```
- **Arquivos Estáticos:** O backend deve montar o diretório de áudios em `/ai/audio/`.

### 📊 ANALYTICS-SERVICE (`8050`)
- **Progresso (`GET /analytics/progress`):**
  - **Resposta:**
    ```json
    {
      "weekly_completion": 0.75,
      "motivational_message": "Continue assim!",
      "weekly_history": [0.1, 0.5, 0.75, ...]
    }
    ```

---

## 3. Comportamento do Frontend (Android)

1.  **Segurança:** JWT armazenado em `SharedPreferences` e enviado via Header `Authorization: Bearer <token>`.
2.  **Câmera (IA):** O app processa e envia frames a cada **300ms** para evitar sobrecarga.
3.  **Feedback de Áudio:** O `MediaPlayer` é acionado automaticamente ao receber uma URL em `audio_feedback_url`.
4.  **UI/UX:**
    - **Home:** Cards de próxima sessão e progresso circular (Material Design 3).
    - **Login:** Inputs com ícones e link de recuperação de senha.
    - **Cadastro:** Inclui validação visual de CPF e Checkbox LGPD.

---

## 4. Checklist para o Desenvolvedor Backend

- [ ] **CORS:** Permitir `10.0.2.2` e `*` em todos os microserviços.
- [ ] **Portas:** `8081` para exercícios e `8050` para analytics no `docker-compose.yml`.
- [ ] **Paths:** Garantir que os endpoints não tenham o prefixo `/api` (ex: usar `@app.get("/exercises")`).
- [ ] **JSON Keys:** Validar se as chaves batem com `user_role`, `validation_status` e `audio_feedback_url`.
- [ ] **Static Serving:** Garantir que a pasta `audio` no `ai-service` seja acessível via HTTP.

---
**Gerado por:** Gemini Android Expert (Manus AI)
**Data:** 26/02/2026
**Status:** Integração Pronta para Testes
