# Debug - Problema com Vídeos não Encontrados

## Sintoma
- App mostra "não encontrado" para vídeos
- Usuário pergunta se os vídeos estão sendo salvos

## Possíveis Causas

### 1. URI Local vs URL Web
**Problema**: Android pode estar enviando URI local (`content://`) em vez de URL web.

**Como verificar**: 
- Backend agora mostra logs com `🎥 Vídeo URL recebido:`
- Se mostrar `content://`, esse é o problema

### 2. Vídeo Não Sendo Adicionado ao patient_exercises_db
**Problema**: Vídeo é salvo no banco mas não aparece na lista do paciente.

**Como verificar**:
- Backend mostra logs quando cria exercício
- Verifique se aparece `Vídeo salvo no patient_exercises_db:`

## Como Testar Agora

### 1. Criar Exercício com Vídeo
1. Login como profissional
2. Vá para "Exercícios" → "+" 
3. Selecione um vídeo
4. Preencha dados e salve

### 2. Verificar Logs do Backend
**No terminal do backend, deve aparecer:**
```
=== DEBUG TASK CREATE ===
🎥 Vídeo URL recebido: content://...
📷 Imagem URL recebida: content://...

🏋️ EXERCÍCIO CRIADO E SALVO NO BANCO:
   - Vídeo URL: content://...
   - Vídeo salvo no patient_exercises_db: content://...
```

### 3. Verificar como Paciente
1. Login como paciente (mesmo ID usado na criação)
2. Vá para "Meus Exercícios"
3. Verifique logs:
```
🔍 BUSCANDO EXERCÍCIOS PARA PACIENTE X
EXERCISE_DEBUG: Exercício: Título | Vídeo: content://...
```

## Se o Vídeo for `content://`

### Problema
Android está enviando URI local que não funciona no backend.

### Solução
Precisa fazer upload do vídeo para o backend primeiro.

### Passos para Corrigir:
1. Fazer upload do arquivo para o backend
2. Backend retorna URL web
3. Usar URL web na criação do exercício

## Se o Vídeo for URL Web Mas Mesmo Assim "Não Encontrado"

### Possíveis Causas:
1. URL inválida/expirada
2. Vídeo muito grande
3. Formato não suportado
4. Problema de permissão

### Teste Rápido:
1. Copie a URL do log
2. Cole no navegador
3. Se não abrir, a URL está inválida

## Logs Adicionados

### Frontend (CreateTaskActivity.java):
```java
Log.d("TASK_DEBUG", "Vídeo URL: " + selectedVideoUri.toString());
```

### Backend (task_router.py):
```python
print(f"🎥 Vídeo URL recebido: {task_data.exercise_video_url}")
print(f"🎥 Vídeo salvo no patient_exercises_db: {new_exercise['exercise_video_url']}")
```

## Próximos Passos

1. **Teste criar um exercício com vídeo**
2. **Verifique os logs no backend**
3. **Meça o tipo de URL (content:// vs http://)**
4. **Se for content://, precisamos implementar upload**

---

**Status**: 🔍 **AGUARDANDO TESTE PARA IDENTIFICAR PROBLEMA**
