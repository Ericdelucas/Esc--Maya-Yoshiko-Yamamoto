# 🔍 DEBUG - SISTEMA DE DIRECIONAMENTO NÃO FUNCIONA

## 📋 **Problema Identificado**

O sistema de controle de acesso não está direcionando corretamente:
- **Usuários profissionais** estão indo para `MainActivity` (pacientes)
- **Deveriam ir para** `ProfessionalMainActivity`

## 🎯 **Análise Necessária**

Precisamos descobrir qual role está sendo recebido e salvo no login.

## 🔧 **Instruções para o Gemini**

### **Passo 1: Adicionar Logs de Depuração no TokenManager**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/utils/TokenManager.java`

**Adicionar import:**
```java
import android.util.Log;
```

**Modificar método saveSession():**
```java
public void saveSession(String token, String role, String email) {
    // 🔥 DEBUG: Mostrar dados sendo salvos
    Log.d("TokenManager", "Salvando sessão - Token: " + (token != null ? "OK" : "NULL") + ", Role: '" + role + "', Email: '" + email + "'");
    
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(TOKEN_KEY, token);
    editor.putString(USER_ROLE_KEY, role);
    editor.putString(USER_EMAIL_KEY, email);
    editor.apply();
}
```

**Modificar método getUserRole():**
```java
public String getUserRole() {
    String role = prefs.getString(USER_ROLE_KEY, "Patient");
    // 🔥 DEBUG: Mostrar role recuperado
    Log.d("TokenManager", "Role recuperado: '" + role + "' (default: 'Patient')");
    return role;
}
```

### **Passo 2: Adicionar Logs no LoginActivity**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/LoginActivity.java`

**Modificar método navigateToMain():**
```java
private void navigateToMain() {
    String userRole = tokenManager.getUserRole();
    Class<?> targetActivity;
    
    // 🔥 DEBUG: Mostrar qual role foi recebido
    Log.d(TAG, "Role recebido: '" + userRole + "'");
    
    // Lógica de navegação segura
    if (userRole != null && (userRole.equalsIgnoreCase("professional") || userRole.equalsIgnoreCase("doctor") || userRole.equalsIgnoreCase("admin"))) {
        targetActivity = ProfessionalMainActivity.class;
        Log.d(TAG, "Direcionando para ProfessionalMainActivity");
    } else {
        targetActivity = MainActivity.class;
        Log.d(TAG, "Direcionando para MainActivity (role: " + userRole + ")");
    }
    
    Log.d(TAG, "Activity alvo: " + targetActivity.getSimpleName());
    
    Intent intent = new Intent(this, targetActivity);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
}
```

### **Passo 3: Testar e Analisar Logs**

1. **Recompile o app** com as alterações
2. **Faça login** com um usuário profissional:
   - Email: `dr.silva@smartsaude.com`
   - Senha: `prof123`
3. **Monitore os logs** no Android Studio (Logcat)
4. **Procure pelas tags:**
   - `TokenManager`
   - `LOGIN_DEBUG`

### **Passo 4: Analisar Resultados Esperados**

**Se funcionar corretamente, os logs devem mostrar:**
```
TokenManager: Salvando sessão - Token: OK, Role: 'professional', Email: 'dr.silva@smartsaude.com'
TokenManager: Role recuperado: 'professional' (default: 'Patient')
LOGIN_DEBUG: Role recebido: 'professional'
LOGIN_DEBUG: Direcionando para ProfessionalMainActivity
LOGIN_DEBUG: Activity alvo: ProfessionalMainActivity
```

**Se estiver errado, pode mostrar:**
```
TokenManager: Salvando sessão - Token: OK, Role: 'patient', Email: '...'
TokenManager: Role recuperado: 'patient' (default: 'Patient')
LOGIN_DEBUG: Role recebido: 'patient'
LOGIN_DEBUG: Direcionando para MainActivity
LOGIN_DEBUG: Activity alvo: MainActivity
```

## 🔍 **Possíveis Causas do Problema**

1. **Role vindo null do backend**
2. **Role sendo salvo incorretamente**
3. **Role sendo recuperado com valor padrão**
4. **Lógica de comparação falhando**

## 📊 **Informações de Teste**

**Usuários profissionais disponíveis:**
- `novo.admin@smartsaude.com` / `admin123` (role: admin)
- `dr.silva@smartsaude.com` / `prof123` (role: professional)

**Usuário paciente:**
- `joao.paciente@smartsaude.com` / `pac123` (role: patient)

## 🎯 **Objetivo**

Descobrir por que profissionais estão sendo direcionados para a tela de pacientes e corrigir o problema.

## 📱 **Após o Debug**

1. **Copie os logs** obtidos
2. **Identifique o problema** com base nos logs
3. **Aplique a correção necessária**
4. **Remova os logs de debug** após resolver

---

**Status:** 🔄 **AGUARDANDO IMPLEMENTAÇÃO DOS LOGS DE DEBUG**
