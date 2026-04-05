# 🚨 CORREÇÃO URGENTE - DIRECIONAMENTO DE TELAS

## 🎯 **PROBLEMA**
O app não está direcionando corretamente:
- **Pacientes** devem ir para `MainActivity`
- **Profissionais** devem ir para `ProfessionalMainActivity`
- **Atualmente todos vão para a mesma tela**

## 🔧 **SOLUÇÃO IMEDIATA**

### **Arquivo para Editar:**
`front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/LoginActivity.java`

### **Passo 1: Adicionar Logs para Debug**

**Adicionar import no topo do arquivo (se não existir):**
```java
import android.util.Log;
```

**Localizar o método `navigateToMain()` e substituir por:**
```java
private void navigateToMain() {
    String userRole = tokenManager.getUserRole();
    Class<?> targetActivity;
    
    // DEBUG: Mostrar qual role foi recebido
    Log.d("LOGIN_DEBUG", "Role recebido: '" + userRole + "'");
    
    // Lógica de direcionamento
    if (userRole != null && (userRole.equalsIgnoreCase("professional") || userRole.equalsIgnoreCase("doctor") || userRole.equalsIgnoreCase("admin"))) {
        targetActivity = ProfessionalMainActivity.class;
        Log.d("LOGIN_DEBUG", "PROFISSIONAL - Indo para ProfessionalMainActivity");
    } else {
        targetActivity = MainActivity.class;
        Log.d("LOGIN_DEBUG", "PACIENTE - Indo para MainActivity");
    }
    
    Log.d("LOGIN_DEBUG", "Activity alvo: " + targetActivity.getSimpleName());
    
    Intent intent = new Intent(this, targetActivity);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
}
```

### **Passo 2: Verificar TokenManager**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/utils/TokenManager.java`

**Adicionar import (se não existir):**
```java
import android.util.Log;
```

**Modificar método `saveSession()`:**
```java
public void saveSession(String token, String role, String email) {
    Log.d("TOKEN_DEBUG", "Salvando - Role: '" + role + "', Email: '" + email + "'");
    
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(TOKEN_KEY, token);
    editor.putString(USER_ROLE_KEY, role);
    editor.putString(USER_EMAIL_KEY, email);
    editor.apply();
}
```

**Modificar método `getUserRole()`:**
```java
public String getUserRole() {
    String role = prefs.getString(USER_ROLE_KEY, "Patient");
    Log.d("TOKEN_DEBUG", "Recuperado role: '" + role + "'");
    return role;
}
```

## 🧪 **COMO TESTAR**

1. **Recompile o app**
2. **Faça login** com usuário profissional:
   - Email: `dr.silva@smartsaude.com`
   - Senha: `prof123`
3. **Veja os logs** no Logcat
4. **Verifique para qual tela vai**

## 📊 **LOGS ESPERADOS PARA PROFISSIONAL**
```
TOKEN_DEBUG: Salvando - Role: 'professional', Email: 'dr.silva@smartsaude.com'
TOKEN_DEBUG: Recuperado role: 'professional'
LOGIN_DEBUG: Role recebido: 'professional'
LOGIN_DEBUG: PROFISSIONAL - Indo para ProfessionalMainActivity
LOGIN_DEBUG: Activity alvo: ProfessionalMainActivity
```

## 📊 **LOGS ESPERADOS PARA PACIENTE**
```
TOKEN_DEBUG: Salvando - Role: 'patient', Email: 'joao.paciente@smartsaude.com'
TOKEN_DEBUG: Recuperado role: 'patient'
LOGIN_DEBUG: Role recebido: 'patient'
LOGIN_DEBUG: PACIENTE - Indo para MainActivity
LOGIN_DEBUG: Activity alvo: MainActivity
```

## 🚨 **SE AINDA NÃO FUNCIONAR**

1. **Verifique se o backend está retornando o role correto**
2. **Verifique se o ProfessionalMainActivity existe**
3. **Verifique se há erros no Logcat**

## 📱 **USUÁRIOS PARA TESTE**

**Profissional:**
- Email: `dr.silva@smartsaude.com`
- Senha: `prof123`

**Paciente:**
- Email: `joao.paciente@smartsaude.com`
- Senha: `pac123`

---

**IMPORTANTE:** Após testar, remova os logs de debug se não forem mais necessários.

**Status:** 🔄 **AGUARDANDO IMPLEMENTAÇÃO DA CORREÇÃO**
