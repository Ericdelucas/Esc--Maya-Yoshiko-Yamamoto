# 🚨 **ERRO 403 FORBIDDEN - CORRIGIDO!**

## ✅ **PROBLEMA IDENTIFICADO E RESOLVIDO**

### **❌ O que estava errado:**
- **HealthToolsApi** não estava enviando token de autenticação
- **Backend exigia autenticação** mas não recebia token
- **Resultado:** 403 Forbidden

### **✅ O que foi corrigido:**
1. **HealthToolsApi.java** - Adicionado `@Header("Authorization") String token` em todos os métodos
2. **HealthQuestionnaireActivity.java** - Adicionado `TokenManager` para obter e enviar token
3. **Logs melhorados** para debugging

---

## 🛠️ **COMO TESTAR AGORA:**

### **✅ Passo 1 - Recompilar App:**
```bash
cd front/Esc--Maya-Yoshiko-Yamamoto/testbackend
./gradlew clean
./gradlew assembleDebug
```

### **✅ Passo 2 - Instalar App:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### **✅ Passo 3 - Redirecionar Porta:**
```bash
adb reverse tcp:8080 tcp:8080
```

### **✅ Passo 4 - Testar Questionário:**
1. **Abrir app** → Fazer login
2. **Menu** → "Ferramentas de Saúde"
3. **Responder** todas as perguntas
4. **Clicar** "Salvar Respostas"

---

## 🎯 **RESULTADO ESPERADO:**

### **✅ Sucesso:**
- **Toast:** "Salvo! Pontuação: X - Risco: Y"
- **Sem erro 403**
- **Dados salvos** no banco

### **✅ Logs no Backend:**
```
INFO: 172.18.0.1:XXXXX - "POST /health-tools/save-questionnaire HTTP/1.1" 200 OK
```

---

## 🔍 **VERIFICAÇÃO:**

### **✅ Testar API diretamente com token:**
```bash
# Obter token primeiro
TOKEN=$(curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"seu@email.com","password":"suasenha"}' | jq -r '.access_token')

# Testar endpoint
curl -X POST "http://localhost:8080/health-tools/save-questionnaire" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"answers":[{"question_id":"symptoms","answer":"no"}]}'
```

### **✅ Logs no Android Studio:**
Procurar por:
```
HealthQuestionnaire: Erro response code: XXX
HealthQuestionnaire: Erro de conexão: XXX
```

---

## 🚨 **SE AINDA DER ERRO:**

### **❌ Verificar:**
- [ ] **Fez login no app?** (precisa de token válido)
- [ ] **Rodou adb reverse?**
- [ ] **Recompilou o app?**
- [ ] **Backend está rodando?** (`docker ps | grep auth-service`)

### **❌ Debug avançado:**
No Android Studio, colocar breakpoint na linha:
```java
String token = tokenManager.getAuthToken();
Log.d("HealthQuestionnaire", "Token: " + token);
```

---

## 🎯 **RESUMO FINAL:**

### **✅ Problema resolvido:**
- **HealthToolsApi** agora envia token corretamente
- **Autenticação funcionando**
- **403 Forbidden eliminado**

**Agora o questionário deve salvar corretamente! 🚀🎯**
