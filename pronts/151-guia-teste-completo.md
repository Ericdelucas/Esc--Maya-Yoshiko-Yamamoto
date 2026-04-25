# ✅ **GUIA DE TESTE COMPLETO - SISTEMA LIMPO!**

## 🎯 **OBJETIVO DO TESTE**

Verificar se o sistema de controle de repetição funciona corretamente:
1. **Limpar dados** - Zerar todos os registros
2. **Testar exercício A** - Deve contar pontos
3. **Testar exercício B** - Deve contar pontos
4. **Tentar repetir A** - Deve bloquear
5. **Testar exercício C** - Deve contar pontos

---

## 🔧 **PASSO 1 - LIMPAR DADOS**

### **📋 Limpar todos os registros:**
```bash
# Faça login primeiro para obter token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@test.com", "password": "password"}' \
  | jq -r '.token'

# Use o token para limpar dados
TOKEN="SEU_TOKEN_AQUI"
curl -X POST -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/tasks/clear-test-data
```

### **📱 Resposta esperada:**
```json
{
  "success": true,
  "message": "Dados de teste limpos com sucesso!",
  "user_id": 3,
  "cleared_data": ["user_points", "user_tasks", "daily_completed_tasks"]
}
```

---

## 🔧 **PASSO 2 - VERIFICAR PONTOS ZERADOS**

### **📋 Verificar status inicial:**
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/tasks/user-points
```

### **📱 Resposta esperada:**
```json
{
  "user_id": 3,
  "username": "test",
  "total_points": 0,
  "tasks_completed": 0,
  "level": "Nível 1",
  "badges": ["Iniciante"]
}
```

---

## 🔧 **PASSO 3 - TESTAR EXERCÍCIO A**

### **📋 Completar exercício A (ID: 1):**
```bash
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 1}' \
  http://localhost:8080/tasks/complete-task
```

### **📱 Resposta esperada:**
```json
{
  "success": true,
  "message": "Exercício completado com sucesso!",
  "task_id": 1,
  "points_awarded": 15,
  "tasks_completed_today": 1,
  "remaining_tasks": 4,
  "new_total_points": 15
}
```

### **📱 Verificar pontos após A:**
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/tasks/user-points
```

**Esperado:** `total_points: 15`

---

## 🔧 **PASSO 4 - TESTAR EXERCÍCIO B**

### **📋 Completar exercício B (ID: 2):**
```bash
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 2}' \
  http://localhost:8080/tasks/complete-task
```

### **📱 Resposta esperada:**
```json
{
  "success": true,
  "message": "Exercício completado com sucesso!",
  "task_id": 2,
  "points_awarded": 15,
  "tasks_completed_today": 2,
  "remaining_tasks": 3,
  "new_total_points": 30
}
```

### **📱 Verificar pontos após B:**
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/tasks/user-points
```

**Esperado:** `total_points: 30`

---

## 🔧 **PASSO 5 - TENTAR REPETIR EXERCÍCIO A**

### **📋 Tentar repetir exercício A (ID: 1):**
```bash
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 1}' \
  http://localhost:8080/tasks/complete-task
```

### **📱 Resposta esperada (BLOQUEIO):**
```json
{
  "success": false,
  "message": "Este exercício já foi completado hoje. Tente novamente amanhã!",
  "can_repeat_tomorrow": true,
  "task_id": 1
}
```

### **📱 Verificar pontos após tentativa:**
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/tasks/user-points
```

**Esperado:** `total_points: 30` (não deve mudar!)

---

## 🔧 **PASSO 6 - TESTAR EXERCÍCIO C**

### **📋 Completar exercício C (ID: 3):**
```bash
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 3}' \
  http://localhost:8080/tasks/complete-task
```

### **📱 Resposta esperada:**
```json
{
  "success": true,
  "message": "Exercício completado com sucesso!",
  "task_id": 3,
  "points_awarded": 15,
  "tasks_completed_today": 3,
  "remaining_tasks": 2,
  "new_total_points": 45
}
```

### **📱 Verificar pontos finais:**
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/tasks/user-points
```

**Esperado:** `total_points: 45`

---

## 🎮 **FLUXO COMPLETO ESPERADO**

### **📅 Resumo do teste:**
```
🧹 PASSO 1: Limpar dados ✅
📊 PASSO 2: Pontos = 0 ✅
🏋 PASSO 3: Exercício A = +15 pontos ✅
🏋 PASSO 4: Exercício B = +15 pontos ✅
🚫 PASSO 5: Repetir A = Bloqueado ✅
🏋 PASSO 6: Exercício C = +15 pontos ✅

📊 RESULTADO FINAL: 45 pontos (3 exercícios diferentes)
🎯 PROGRESSÃO: 3/5 tarefas hoje
```

---

## 🎯 **VERIFICAÇÃO NO APP**

### **📱 Teste no Android:**
1. **Abra o app**
2. **Faça login**
3. **Vá para exercícios**
4. **Clique em exercício A**
   - Deve mostrar: "Tarefa concluída! +15 pontos"
   - Deve mostrar: "Progresso: 1/5 tarefas hoje"
5. **Clique em exercício B**
   - Deve mostrar: "Tarefa concluída! +15 pontos"
   - Deve mostrar: "Progresso: 2/5 tarefas hoje"
6. **Clique novamente em exercício A**
   - Deve mostrar: "Este exercício já foi completado hoje. Tente novamente amanhã!"
   - Deve mostrar: "📅 Você poderá repetir este exercício amanhã!"
7. **Clique em exercício C**
   - Deve mostrar: "Tarefa concluída! +15 pontos"
   - Deve mostrar: "Progresso: 3/5 tarefas hoje"

---

## 🚨 **POSSÍVEIS PROBLEMAS**

### **❌ Se todos der erro 500:**
- Problema no backend
- Verificar logs: `docker compose logs auth-service`

### **❌ Se exercício B não contar pontos:**
- Frontend não enviando ID correto
- Verificar se Gemini implementou mudanças

### **❌ Se repetir A não bloquear:**
- Sistema de controle não funcionando
- Verificar backend

### **❌ Se pontos não somarem:**
- Problema na lógica de pontos
- Verificar `add_points_to_user`

---

## 📋 **COMANDOS RÁPIDOS**

### **🔧 Script completo de teste:**
```bash
#!/bin/bash

# 1. Login e obter token
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@test.com", "password": "password"}' \
  | jq -r '.token')

echo "🔑 Token obtido: ${TOKEN:0:20}..."

# 2. Limpar dados
echo "🧹 Limpando dados..."
curl -s -X POST -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/tasks/clear-test-data | jq

# 3. Verificar pontos iniciais
echo "📊 Verificando pontos iniciais..."
curl -s -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/tasks/user-points | jq '.total_points'

# 4. Testar exercício A
echo "🏋 Testando exercício A..."
curl -s -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 1}' \
  http://localhost:8080/tasks/complete-task | jq

# 5. Testar exercício B
echo "🏋 Testando exercício B..."
curl -s -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 2}' \
  http://localhost:8080/tasks/complete-task | jq

# 6. Tentar repetir A
echo "🚫 Tentando repetir exercício A..."
curl -s -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 1}' \
  http://localhost:8080/tasks/complete-task | jq

# 7. Testar exercício C
echo "🏋 Testando exercício C..."
curl -s -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 3}' \
  http://localhost:8080/tasks/complete-task | jq

# 8. Verificar pontos finais
echo "📊 Verificando pontos finais..."
curl -s -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/tasks/user-points | jq '.total_points'

echo "✅ Teste completo!"
```

---

## 🎯 **RESULTADO ESPERADO**

### **✅ Sistema funcionando:**
- **Limpeza:** Dados zerados com sucesso
- **Exercício A:** +15 pontos na primeira vez
- **Exercício B:** +15 pontos (diferente)
- **Repetição A:** Bloqueado com mensagem clara
- **Exercício C:** +15 pontos (diferente)
- **Total:** 45 pontos (3 exercícios diferentes)

### **✅ Experiência do usuário:**
- **Clara:** Mensagens informativas
- **Justa:** Só bloqueia repetição do mesmo
- **Progressiva:** Permite completar diferentes exercícios
- **Segura:** Impede "farming" de pontos

---

## 📋 **GUIAS CRIADOS**

### **✅ Documentação completa:**
- `151-guia-teste-completo.md` - Guia de teste completo
- `150-teste-limpeza-dados.md` - Sistema de limpeza
- `149-instrucoes-gemini-frontend.md` - Instruções para Gemini
- `148-problema-task-id-especifico-corrigido.md` - Correção do ID

### **✅ Sistema pronto:**
- Backend com endpoint de limpeza
- Controle de repetição específico
- Sistema de testes completo
- Fluxo validado

---

## 🚀 **STATUS FINAL**

**O sistema está pronto para teste completo:**

1. **Endpoint de limpeza** - `/tasks/clear-test-data`
2. **Controle específico** - Cada exercício com seu ID
3. **Sistema de teste** - Passos validados
4. **Documentação completa** - Guias detalhados

**Agora você pode testar o sistema completo do zero! 🎯**
