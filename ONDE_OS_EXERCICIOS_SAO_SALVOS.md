# Onde os Exercícios e Vídeos São Salvos

## Resposta Direta

**Sim, os exercícios estão sendo salvos em dois lugares:**

1. **Banco de Dados PostgreSQL** (persistência permanente)
2. **Memória `patient_exercises_db`** (usado para mostrar aos pacientes)

---

## 📍 **Onde Exatamente os Exercícios Ficam Salvos**

### 1. Banco de Dados (PostgreSQL)
- **Tabela**: `TaskORM`
- **Campos de vídeo**: `exercise_video_url`, `exercise_image_url`
- **Persistência**: Permanente (sobrevive a reinicialização do backend)

```sql
-- Exemplo do que é salvo no banco:
INSERT INTO tasks (
    professional_id, patient_id, title, description,
    exercise_video_url, exercise_image_url, is_active
) VALUES (
    1, 2, 'Rotação de ombro', 'Movimentos circulares...',
    'content://media/external/images/...',  -- URI do Android
    'content://media/external/videos/...',  -- URI do Android
    true
);
```

### 2. Memória Temporária (`patient_exercises_db`)
- **Tipo**: Dicionário Python em memória
- **Estrutura**: `{patient_id: [exercises]}`
- **Usado para**: Respostas rápidas ao frontend
- **Problema**: Perdido ao reiniciar o backend

```python
# Exemplo do que fica em memória:
patient_exercises_db = {
    2: [  # Paciente ID 2
        {
            "id": 123,
            "title": "Rotação de ombro",
            "exercise_video_url": "content://...",
            "exercise_image_url": "content://..."
        }
    ]
}
```

---

## 🎥 **Sobre os Vídeos Especificamente**

### Problema Principal
**Os vídeos NÃO são enviados para o backend!**

O que acontece:
1. Android seleciona vídeo → Gera URI `content://`
2. URI é enviada ao backend como texto
3. Backend salva URI como string no banco
4. **O arquivo de vídeo fica no dispositivo Android!**

### Por Que "Não Encontrado"?
- URI `content://` só funciona no dispositivo Android original
- Backend tenta acessar `content://` (não funciona)
- Paciente não consegue acessar vídeo do dispositivo do profissional

---

## 🔍 **Como Verificar Onde Está Salvo**

### Endpoint de Debug (NOVO)
```bash
# Acesse no navegador ou curl:
GET http://localhost:8080/tasks/debug/exercises
```

### O que o debug mostra:
```json
{
    "debug_info": {
        "patient_exercises_db_keys": [1, 2, 3],
        "exercises_by_patient": {
            "2": {
                "total_exercises": 3,
                "exercises": [
                    {
                        "id": 123,
                        "title": "Rotação de ombro",
                        "has_video": true,
                        "video_url": "content://media/external/videos/..."
                    }
                ]
            }
        }
    }
}
```

---

## 📋 **Como Testar Agora**

### 1. Verificar se está salvando:
```bash
# No terminal do backend:
curl -H "Authorization: Bearer SEU_TOKEN" \
     http://localhost:8080/tasks/debug/exercises
```

### 2. Verificar logs ao criar:
No backend deve aparecer:
```
🎥 Vídeo URL recebido: content://media/external/videos/...
🏋️ EXERCÍCIO CRIADO E SALVO:
   - Vídeo salvo no patient_exercises_db: content://...
```

### 3. Verificar como paciente:
No app do paciente, logs mostram:
```
EXERCISE_DEBUG: Exercício: Rotação de ombro | Vídeo: content://...
```

---

## 🚨 **Problemas Identificados**

### 1. URI Local vs URL Web
- **Problema**: `content://` só funciona no dispositivo original
- **Solução**: Fazer upload do vídeo para o backend

### 2. Vídeo Não Realmente Transferido
- **Problema**: Vídeo fica no Android, não no backend
- **Solução**: Implementar upload de arquivo

### 3. Acesso Cross-Dispositivo
- **Problema**: Paciente não acessa vídeo do dispositivo do profissional
- **Solução**: Servidor central de vídeos

---

## ✅ **Resposta Final**

**Sim, está salvando, mas:**

1. ✅ **Exercícios são salvos** no banco PostgreSQL + memória
2. ✅ **URLs dos vídeos são salvas** no banco
3. ❌ **Os arquivos de vídeo NÃO são transferidos**
4. ❌ **Paciente não consegue acessar `content://`**

**Onde está salvo:**
- **Dados**: Banco PostgreSQL (tabela `tasks`)
- **Arquivos**: Ainda no dispositivo Android (não transferidos)

**Para resolver:** Precisa implementar upload real dos vídeos para o backend.
