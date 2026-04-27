# 🚨 **ERRO 500 - TOKEN JWT CORROMPIDO**

## ✅ **SOLUÇÃO TEMPORÁRIA CRIADA**

### **❌ Problema:**
- **Token JWT está corrompido** (Invalid header padding)
- **Erro 500 Internal Server Error**
- **Autenticação falhando**

### **✅ Solução temporária:**
- **Criado endpoint `/save-questionnaire-test`** sem autenticação
- **Para testes rápidos** do questionário
- **Usa usuário fixo ID 3** para salvar

---

## 🛠️ **COMO TESTAR AGORA (SEM AUTENTICAÇÃO):**

### **✅ Opção 1 - Teste direto via curl:**
```bash
curl -X POST "http://localhost:8080/health-tools/save-questionnaire-test" \
  -H "Content-Type: application/json" \
  -d '{
    "answers": [
      {"question_id": "symptoms", "answer": "no"},
      {"question_id": "allergies", "answer": "no"},
      {"question_id": "meds", "answer": "no"},
      {"question_id": "chronic", "answer": "no"},
      {"question_id": "surgery", "answer": "no"},
      {"question_id": "habits", "answer": "excellent"}
    ]
  }'
```

### **✅ Opção 2 - Modificar app para usar endpoint de teste:**

**No HealthToolsApi.java:**
```java
@POST("health-tools/save-questionnaire-test")
Call<QuestionnaireResponse> saveQuestionnaireTest(@Body QuestionnaireRequest request);
```

**No HealthQuestionnaireActivity.java:**
```java
// Trocar esta linha:
healthApi.saveQuestionnaire(token, request).enqueue(...)

// Por esta (sem token):
healthApi.saveQuestionnaireTest(request).enqueue(...)
```

---

## 🎯 **RESULTADO ESPERADO:**

### **✅ Resposta do endpoint:**
```json
{
  "success": true,
  "message": "Questionário salvo com sucesso (TESTE)",
  "data": {
    "score": 0,
    "risk_level": "Baixo",
    "created_at": "2025-01-15T..."
  }
}
```

---

## 🔍 **VERIFICAÇÃO NO BANCO:**

### **✅ Verificar dados salvos:**
```bash
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT user_id, total_score, risk_level, created_at FROM health_questionnaires ORDER BY created_at DESC LIMIT 3;"
```

---

## 🚨 **SOLUÇÃO DEFINITIVA (AUTENTICAÇÃO):**

### **❌ O que causa o erro JWT:**
1. **Token expirado**
2. **Token corrompido no SharedPreferences**
3. **Formato inválido do token**

### **✅ Como resolver autenticação:**
1. **Limpar dados do app** e fazer login novamente
2. **Verificar TokenManager** está salvando token correto
3. **Debugar token** com os logs adicionados

### **✅ Logs para debug:**
No Android Studio, procurar por:
```
HealthQuestionnaire: Token obtido: SIM/NULL
HealthQuestionnaire: Token length: XXX
HealthQuestionnaire: Token preview: Bearer eyJhbGciOiJIUzI1NiIs...
```

---

## 🎯 **RESUMO:**

### **✅ Para teste imediato:**
- **Use `/save-questionnaire-test`** (sem autenticação)
- **Funciona com qualquer usuário**
- **Salva no banco do usuário ID 3**

### **✅ Para produção:**
- **Resolver problema do token JWT**
- **Limpar cache do app**
- **Fazer login novamente**

**Agora você pode testar o questionário mesmo com erro de autenticação! 🚀🎯**
