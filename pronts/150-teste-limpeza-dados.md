# 🧹 **LIMPEZA DE DADOS PARA TESTE**

## 🎯 **OBJETIVO**

Limpar todos os registros de exercícios completados para testar o sistema do zero.

---

## 🔧 **COMO LIMPAR OS DADOS**

### **📋 Backend - Limpar registros:**
```python
# Adicionar endpoint de limpeza
@router.post("/clear-test-data")
def clear_test_data(current_user: UserOut = Depends(get_current_user)):
    """Limpa dados de teste do usuário"""
    user_id = current_user.id
    
    # 🔥 **LIMPAR PONTOS**
    if user_id in user_points_db:
        del user_points_db[user_id]
    
    # 🔥 **LIMPAR TAREFAS COMPLETADAS**
    if user_id in user_tasks_db:
        del user_tasks_db[user_id]
    
    # 🔥 **LIMPAR CONTROLE DE REPETIÇÃO**
    if user_id in daily_completed_tasks:
        del daily_completed_tasks[user_id]
    
    return {
        "success": True,
        "message": "Dados de teste limpos com sucesso!",
        "user_id": user_id,
        "cleared_data": [
            "user_points",
            "user_tasks", 
            "daily_completed_tasks"
        ]
    }
```

### **📱 Como testar:**
```bash
# 1. Limpar dados
curl -X POST -H "Authorization: Bearer TOKEN" \
  http://localhost:8080/tasks/clear-test-data

# 2. Verificar pontos zerados
curl -H "Authorization: Bearer TOKEN" \
  http://localhost:8080/tasks/user-points

# 3. Testar exercício A
curl -X POST -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 1}' \
  http://localhost:8080/tasks/complete-task

# 4. Testar exercício B
curl -X POST -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 2}' \
  http://localhost:8080/tasks/complete-task

# 5. Tentar repetir exercício A
curl -X POST -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"task_id": 1}' \
  http://localhost:8080/tasks/complete-task
```

---

## 🎮 **FLUXO DE TESTE ESPERADO**

### **📅 Passo 1 - Limpar dados:**
```json
{
  "success": true,
  "message": "Dados de teste limpos com sucesso!",
  "user_id": 3,
  "cleared_data": ["user_points", "user_tasks", "daily_completed_tasks"]
}
```

### **📅 Passo 2 - Verificar pontos zerados:**
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

### **📅 Passo 3 - Completar exercício A (ID: 1):**
```json
{
  "success": true,
  "message": "Exercício completado com sucesso!",
  "task_id": 1,
  "points_awarded": 15,
  "tasks_completed_today": 1,
  "remaining_tasks": 4
}
```

### **📅 Passo 4 - Completar exercício B (ID: 2):**
```json
{
  "success": true,
  "message": "Exercício completado com sucesso!",
  "task_id": 2,
  "points_awarded": 15,
  "tasks_completed_today": 2,
  "remaining_tasks": 3
}
```

### **📅 Passo 5 - Tentar repetir exercício A (ID: 1):**
```json
{
  "success": false,
  "message": "Este exercício já foi completado hoje. Tente novamente amanhã!",
  "can_repeat_tomorrow": true,
  "task_id": 1
}
```

---

## 🚀 **IMPLEMENTAÇÃO IMEDIATA**

Vou implementar o endpoint de limpeza agora mesmo:
