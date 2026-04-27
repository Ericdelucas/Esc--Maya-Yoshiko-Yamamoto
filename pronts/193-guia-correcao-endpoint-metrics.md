# 🔧 **GUIA - CORREÇÃO ENDPOINT METRICS**

## ✅ **O QUE FOI CORRIGIDO:**

### **📱 1. HealthApi.java - Endpoints atualizados:**
- ❌ **Antes:** `POST metrics/imc`
- ✅ **Agora:** `POST health-tools/calculate-bmi`

- ❌ **Antes:** `GET metrics/history/{user_id}`
- ✅ **Agora:** `GET health-tools/history`

- ❌ **Antes:** `POST questionnaire`
- ✅ **Agora:** `POST health-tools/save-questionnaire`

---

## 🚨 **PROBLEMA ATUAL:**

### **❌ App ainda chamando endpoint antigo:**
```
POST /metrics/imc?user_id=3&weight=9.0&height=9.0 HTTP/1.1" 404 Not Found
```

### **🎯 Causa:**
- **App instalado** não tem as correções
- **Precisa reinstalar** o app atualizado

---

## 🧪 **SOLUÇÃO - REINSTALAR APP:**

### **📋 Passo 1 - Compilar app atualizado:**
```bash
cd /home/eric-de-lucas/Documentos/GitHub/PI3/back/Esc--Maya-Yoshiko-Yamamoto/front/Esc--Maya-Yoshiko-Yamamoto/testbackend
./gradlew clean assembleDebug
```

### **📋 Passo 2 - Desinstalar versão antiga:**
```bash
adb uninstall com.example.testbackend
```

### **📋 Passo 3 - Instalar versão corrigida:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### **📋 Passo 4 - Testar:**
1. **Abrir app**
2. **Login profissional**
3. **"Meus Pacientes"**
4. **Clicar em paciente**
5. **Tentar salvar IMC**
6. **Verificar logs do backend**

---

## 🎯 **RESULTADO ESPERADO:**

### **✅ Logs devem mostrar:**
```
POST /health-tools/calculate-bmi?user_id=3&weight=9.0&height=9.0 HTTP/1.1" 200 OK
```

### **❌ Se ainda mostrar erro:**
```
POST /metrics/imc?user_id=3&weight=9.0&height=9.0 HTTP/1.1" 404 Not Found
```
**Significa que o app não foi atualizado.**

---

## 🔍 **VERIFICAÇÃO:**

### **📱 Para confirmar que o app foi atualizado:**
1. **Verificar versão** do app instalado
2. **Limpar cache** do app se necessário
3. **Reiniciar** o celular

### **📱 Logs para monitorar:**
```bash
# Monitorar logs do backend
docker compose logs -f auth-service

# Procurar por:
# POST /health-tools/calculate-bmi (✅ correto)
# POST /metrics/imc (❌ errado)
```

---

## 🔄 **SE AINDA NÃO FUNCIONAR:**

### **📱 Verificar se há outras chamadas antigas:**
```bash
# Procurar no código
grep -r "metrics/" /home/eric-de-lucas/Documentos/GitHub/PI3/back/Esc--Maya-Yoshiko-Yamamoto/front/
```

### **📱 Verificar se o app está usando outro cliente:**
- **HealthToolsApi** vs **HealthApi**
- **Constants.HEALTH_BASE_URL** vs outras URLs

---

## 📋 **RESUMO:**

**✅ Corrigido:** HealthApi.java com endpoints corretos  
**❌ Problema:** App instalado não tem correções  
**🎯 Solução:** Reinstalar app atualizado  

**Execute os 3 passos acima para corrigir! 🚀📱✅**
