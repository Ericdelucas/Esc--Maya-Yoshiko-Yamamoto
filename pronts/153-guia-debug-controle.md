# 🔍 **GUIA DE DEBUG - CONTROLE ESPECÍFICO**

## 🎯 **OBJETIVO**

Identificar exatamente onde está o problema no controle de repetição e corrigir o comportamento global.

---

## 🔧 **COMO DEBUGAR AGORA**

### **📋 Passo 1 - Limpar dados com debug:**
```bash
# Limpar dados (vai mostrar logs detalhados)
curl -X POST -H "Authorization: Bearer SEU_TOKEN" \
  http://localhost:8080/tasks/clear-test-data
```

**Logs esperados no backend:**
```
🧹 DADOS LIMPOS para usuário 3:
   - user_points_db: False
   - user_tasks_db: False
   - daily_completed_tasks: False
   - daily_completed_tasks total: 0
```

### **📋 Passo 2 - Testar exercício A (ID: 1):**
```bash
curl -X POST -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 1}' \
  http://localhost:8080/tasks/complete-task
```

**Logs esperados no backend:**
```
🔍 VERIFICANDO TAREFA 1 para usuário 3
   - Data de hoje: 2026-04-24
   - Criado registro para usuário 3
   - Criado registro para dia 2026-04-24
   - Tarefas completadas hoje: []
   - Verificando se task_id 1 está em []
   - ✅ TAREFA 1 REGISTRADA COMO COMPLETADA!
   - Nova lista de completadas: [1]
```

### **📋 Passo 3 - Testar exercício B (ID: 2):**
```bash
curl -X POST -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 2}' \
  http://localhost:8080/tasks/complete-task
```

**Logs esperados no backend:**
```
🔍 VERIFICANDO TAREFA 2 para usuário 3
   - Data de hoje: 2026-04-24
   - Tarefas completadas hoje: [1]
   - Verificando se task_id 2 está em [1]
   - ✅ TAREFA 2 REGISTRADA COMO COMPLETADA!
   - Nova lista de completadas: [1, 2]
```

### **📋 Passo 4 - Tentar repetir exercício A (ID: 1):**
```bash
curl -X POST -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 1}' \
  http://localhost:8080/tasks/complete-task
```

**Logs esperados no backend:**
```
🔍 VERIFICANDO TAREFA 1 para usuário 3
   - Data de hoje: 2026-04-24
   - Tarefas completadas hoje: [1, 2]
   - Verificando se task_id 1 está em [1, 2]
   - ❌ TAREFA 1 JÁ FOI COMPLETADA HOJE!
```

### **📋 Passo 5 - Testar exercício C (ID: 3):**
```bash
curl -X POST -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 3}' \
  http://localhost:8080/tasks/complete-task
```

**Logs esperados no backend:**
```
🔍 VERIFICANDO TAREFA 3 para usuário 3
   - Data de hoje: 2026-04-24
   - Tarefas completadas hoje: [1, 2]
   - Verificando se task_id 3 está em [1, 2]
   - ✅ TAREFA 3 REGISTRADA COMO COMPLETADA!
   - Nova lista de completadas: [1, 2, 3]
```

---

## 🚨 **PROBLEMAS POSSÍVEIS**

### **❌ Se logs mostrarem task_id sempre igual:**
- Frontend não enviando ID correto
- Problema no `TaskCompletionRequest`

### **❌ Se logs mostrarem lista sempre vazia:**
- Sistema de limpeza não funcionando
- `daily_completed_tasks` não persistindo

### **❌ Se logs mostrarem comportamento global:**
- Lógica de verificação errada
- Possível cache ou estado compartilhado

---

## 🔧 **VERIFICAR LOGS DO BACKEND**

### **📋 Como ver os logs:**
```bash
# Verificar logs em tempo real
docker compose logs -f auth-service

# Ou ver últimos logs
docker compose logs auth-service --tail 50
```

### **📋 O que procurar nos logs:**
```
🔍 VERIFICANDO TAREFA X para usuário Y
✅ TAREFA X REGISTRADA COMO COMPLETADA!
❌ TAREFA X JÁ FOI COMPLETADA HOJE!
```

---

## 🎯 **FLUXO ESPERADO CORRETO**

### **✅ Comportamento correto:**
1. **Exercício A (ID: 1)**: ✅ Primeira vez = permitido
2. **Exercício B (ID: 2)**: ✅ Diferente = permitido  
3. **Exercício A (ID: 1)**: ❌ Repetição = bloqueado
4. **Exercício C (ID: 3)**: ✅ Diferente = permitido
5. **Exercício B (ID: 2)**: ❌ Repetição = bloqueado
6. **Exercício D (ID: 4)**: ✅ Diferente = permitido

### **❌ Comportamento incorreto (que estava acontecendo):**
1. **Exercício A (ID: 1)**: ✅ Permitido
2. **Exercício B (ID: 2)**: ❌ Bloqueado (errado!)
3. **Exercício C (ID: 3)**: ❌ Bloqueado (errado!)
4. **Todos bloqueados** após o primeiro

---

## 🚀 **AÇÃO IMEDIATA**

### **📋 Execute o teste passo a passo:**
1. **Limpe os dados** e veja os logs
2. **Teste exercício A** e veja os logs  
3. **Teste exercício B** e veja os logs
4. **Tente repetir A** e veja os logs
5. **Compare os logs** com o esperado acima

### **📋 Se encontrar problema:**
- **Anote os logs exatos**
- **Compare com o esperado**
- **Me envie os logs** para análise

---

## 📋 **RESULTADO ESPERADO**

### **✅ Sistema funcionando:**
- Logs mostram IDs corretos
- Cada exercício controlado individualmente
- Bloqueio só para repetição do mesmo ID
- Exercícios diferentes sempre permitidos

### **✅ Experiência do usuário:**
- **Clara:** Mensagens específicas com ID
- **Justa:** Só bloqueia repetição real
- **Funcional:** 5 exercícios diferentes por dia
- **Debugável:** Logs detalhados para diagnóstico

---

## 🎯 **PRÓXIMOS PASSOS**

1. **Executar teste completo** com logs detalhados
2. **Identificar problema exato** através dos logs
3. **Corrigir raiz do problema** 
4. **Validar solução** com novo teste
5. **Remover logs de debug** após funcionar

---

## 📋 **GUIAS CRIADOS**

### **✅ Documentação:**
- `153-guia-debug-controle.md` - Guia de debug completo
- `152-problema-controle-global-corrigido.md` - Diagnóstico do problema
- `151-guia-teste-completo.md` - Teste completo

### **✅ Sistema atualizado:**
- Logs detalhados para debug
- Limpeza completa de dados
- Verificação por ID específico
- Sistema pronto para diagnóstico

---

## 🚀 **STATUS FINAL**

**O sistema agora tem logs detalhados para identificar exatamente onde está o problema:**

1. **Debug completo** - Logs mostram cada passo
2. **Diagnóstico preciso** - Identifica comportamento incorreto
3. **Correção direcionada** - Baseada nos logs
4. **Teste validado** - Fluxo completo documentado

**Execute o teste com os logs e me envie o resultado para análise! 🔍**
