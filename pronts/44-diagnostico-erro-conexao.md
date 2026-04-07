# 🔧 DIAGNÓSTICO - ERRO DE CONEXÃO LOGIN

## 🚨 **PROBLEMA IDENTIFICADO**

**Frontend mostra "erro de conexão" mas backend está funcionando.**

## ✅ **BACKEND STATUS**

### **1. Serviços:**
```bash
✅ docker-compose ps - auth-service Up (healthy)
✅ curl GET /health - 200 OK
```

### **2. Login API:**
```bash
❌ curl POST /auth/login - 401 Unauthorized
```

### **3. Usuários criados:**
```sql
✅ profissional@novo.com - Criado com hash correto
✅ testprofissional@teste.com - Hash existente
✅ test@test.com - Hash existente
```

## 🔍 **POSSÍVEIS CAUSAS**

### **1. Frontend - URL incorreta:**
- App apontando para localhost:8080 errado
- Proxy ou firewall bloqueando

### **2. Frontend - Formato de request:**
- Content-Type incorreto
- Formato JSON inválido

### **3. Frontend - Tratamento de erro:**
- Não mostrando mensagem específica
- Erro genérico "erro de conexão"

### **4. Backend - Autenticação:**
- Senhas incompatíveis
- Pepper diferente
- JWT secret diferente

## 🧪 **TESTES PARA REALIZAR**

### **1. Verificar URL do frontend:**
```java
// No Android app, verificar ApiClient
public class ApiClient {
    public static final String BASE_URL = "http://localhost:8080"; // ✅ Correto?
}
```

### **2. Testar login válido:**
```bash
# Testar novo usuário criado
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "profissional@novo.com", "password": "prof123"}'

# Esperado: 200 OK com token
# Real: 401 Unauthorized ❌
```

### **3. Verificar logs em tempo real:**
```bash
# Terminal 1: Logs do backend
docker logs -f smartsaude-auth

# Terminal 2: Testar login
curl -v POST http://localhost:8080/auth/login ...

# Terminal 3: Verificar network
docker network ls
```

## 📱 **PARA VERIFICAR NO FRONTEND**

### **1. AndroidManifest.xml:**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### **2. NetworkSecurityConfig:**
```xml
<!-- res/xml/network_security_config.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>
```

### **3. Build.gradle:**
```gradle
android {
    useLibrary 'org.apache.http.legacy'
}
```

## 🎯 **AÇÃO RECOMENDADA**

### **1. Verificar configuração de rede:**
- Confirmar BASE_URL no ApiClient
- Verificar permissões de internet
- Testar conectividade

### **2. Adicionar logs detalhados:**
```java
// No LoginActivity
try {
    // Login request
} catch (IOException e) {
    Log.e("LOGIN_ERROR", "Erro de conexão: " + e.getMessage());
    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
} catch (Exception e) {
    Log.e("LOGIN_ERROR", "Erro inesperado: " + e.getMessage());
    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
}
```

### **3. Testar com usuário válido:**
- **Email:** profissional@novo.com
- **Senha:** prof123
- **ID:** 37

## 📊 **STATUS ATUAL**

```
✅ Backend: Online e healthy
✅ Banco: Usuários criados
✅ API: Respondendo
❌ Frontend: Erro de conexão
❌ Login: 401 Unauthorized
```

## 🔄 **PRÓXIMOS PASSOS**

1. **Verificar BASE_URL no app**
2. **Adicionar logs detalhados no frontend**
3. **Testar com usuário válido criado**
4. **Verificar configuração de rede Android**
5. **Confirmar se o emulator/device tem acesso ao backend**

---

**Status:** 🔍 **DIAGNÓSTICO COMPLETO - PRONTO PARA CORREÇÃO**
