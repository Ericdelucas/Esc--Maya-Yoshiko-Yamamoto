# 🎯 INSTRUÇÕES PARA O GEMINI - BACKEND CONTROLA DIRECIONAMENTO

## 📋 **O QUE JÁ FOI FEITO NO BACKEND**

✅ **Backend já está modificado para controlar o direcionamento:**

1. **TokenOut schema** - Inclui campos de direcionamento:
   ```python
   target_activity: str = None
   is_professional: bool = False
   ```

2. **AuthService.login()** - Retorna dados completos:
   ```python
   return {
       "token": token,
       "user_role": user.role,
       "target_activity": "ProfessionalMainActivity",  # Backend decide
       "is_professional": True
   }
   ```

## 🔧 **O QUE VOCÊ PRECISA FAZER NO FRONTEND**

### **Arquivo 1: LoginResponse.java**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/models/LoginResponse.java`

**Adicionar estes campos e métodos:**

```java
// 🔥 ADICIONAR ESTES CAMPOS DEPOIS DO "role":
@SerializedName("full_name")
private String fullName;

@SerializedName("email")
private String email;

// 🔥 NOVOS CAMPOS - Backend controla o direcionamento
@SerializedName("target_activity")
private String targetActivity;

@SerializedName("is_professional")
private boolean isProfessional;

// 🔥 ADICIONAR ESTES MÉTODOS DEPOIS DO getUserRole():
public String getFullName() {
    return fullName;
}

public String getEmail() {
    return email;
}

// 🔥 NOVOS GETTERS
public String getTargetActivity() {
    return targetActivity;
}

public boolean isProfessional() {
    return isProfessional;
}
```

### **Arquivo 2: LoginActivity.java**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/LoginActivity.java`

**1. Adicionar campo para guardar resposta:**
```java
// 🔥 ADICIONAR ESTE CAMPO DEPOIS DO TokenManager:
private LoginResponse loginResponse;
```

**2. Modificar o performLogin() para guardar resposta:**
```java
// DENTRO DO onResponse(), substitua esta parte:
if (token != null && !token.isEmpty()) {
    // ✅ Salva a sessão
    tokenManager.saveSession(token, role, email);
    
    // 🔥 GUARDAR RESPOSTA DO BACKEND
    this.loginResponse = loginResponse;
    
    // 🔥 Navegar baseado no que o BACKEND mandou
    navigateToCorrectActivity();
} else {
    Toast.makeText(LoginActivity.this, "Erro: Token vazio", Toast.LENGTH_SHORT).show();
}
```

**3. Substituir o método navigateToCorrectActivity() por:**
```java
private void navigateToCorrectActivity() {
    // 🔥 SIMPLIFICADO: Usa o que o backend mandou
    String targetActivity = loginResponse.getTargetActivity();
    boolean isProfessional = loginResponse.isProfessional();
    
    Log.d(TAG, "Backend mandou ir para: " + targetActivity);
    Log.d(TAG, "É profissional? " + isProfessional);
    
    Class<?> activityClass;
    
    // Backend decide, frontend só executa
    if ("ProfessionalMainActivity".equals(targetActivity)) {
        activityClass = ProfessionalMainActivity.class;
        Log.d(TAG, "🏥 PROFISSIONAL -> ProfessionalMainActivity");
    } else {
        activityClass = MainActivity.class;
        Log.d(TAG, "👤 PACIENTE -> MainActivity");
    }
    
    Log.d(TAG, "ABRINDO ACTIVITY: " + activityClass.getSimpleName());
    
    Intent intent = new Intent(this, activityClass);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
}
```

## 🧪 **COMO TESTAR APÓS AS MUDANÇAS**

### **1. Reiniciar o backend:**
```bash
cd Backend
docker-compose restart auth-service
```

### **2. Testar no app:**
- **Profissional:** `profissional@smartsaude.com` / `prof123` → ProfessionalMainActivity
- **Paciente:** `joao.paciente@smartsaude.com` / `pac123` → MainActivity

### **3. Verificar logs:**
```
LOGIN_DEBUG: Backend mandou ir para: ProfessionalMainActivity
LOGIN_DEBUG: 🏥 PROFISSIONAL -> ProfessionalMainActivity
```

## 🎯 **VANTAGENS DESTA SOLUÇÃO**

✅ **Backend controla** - Lógica centralizada  
✅ **Frontend simplificado** - Só executa o que backend manda  
✅ **Sem comparações complexas** - Backend já decide  
✅ **Fácil de debugar** - Backend diz exatamente o que fazer  

## 🚨 **IMPORTANTE**

1. **Faça as alterações exatamente como mostrado**
2. **Não mude a lógica do backend** (já está pronta)
3. **Teste com os usuários fornecidos**
4. **Verifique os logs** para confirmar o direcionamento

---

**Status:** 🔄 **AGUARDANDO IMPLEMENTAÇÃO DAS ALTERAÇÕES NO FRONTEND**
