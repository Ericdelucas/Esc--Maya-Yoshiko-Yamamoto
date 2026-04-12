# 🎯 INSTRUÇÕES GEMINI - CORREÇÕES FRONTEND

## 📋 **RESUMO DAS TAREFAS**

### **✅ Backend já foi corrigido:**
- Endpoint de login retorna campos de direcionamento
- Schema aceita campos nulos
- Sistema de direcionamento implementado

### **🔧 Frontend precisa corrigir:**

## 🎯 **TAREFA 1: DIRECIONAMENTO DE LOGIN**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/models/LoginResponse.java`

**Adicionar campos após o campo `role`:**
```java
@SerializedName("full_name")
private String fullName;

@SerializedName("email")
private String email;

// 🔥 NOVOS CAMPOS - Backend controla o direcionamento
@SerializedName("target_activity")
private String targetActivity;

@SerializedName("is_professional")
private boolean isProfessional;
```

**Adicionar métodos após o `getUserRole()`:**
```java
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

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/LoginActivity.java`

**1. Adicionar campo após `TokenManager tokenManager;`:**
```java
private LoginResponse loginResponse;
```

**2. Modificar `performLogin()` no `onResponse()`:**
```java
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

**3. Substituir método `navigateToCorrectActivity()`:**
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

## 🎯 **TAREFA 2: REMOVER DADOS ILUSÓRIOS**

### **Procurar e remover dados mock/hardcoded:**

**1. Encontrar onde está o "12 pacientes":**
```bash
# Use estes comandos para encontrar:
grep -r "12" front/ --include="*.java"
grep -r "tvTotalPacientes" front/ --include="*.java"
grep -r "total.*pacientes" front/ --include="*.java" -i
```

**2. Remover código como este:**
```java
// ❌ REMOVER:
int totalPacientes = 12;
tvTotalPacientes.setText("12");

// ❌ REMOVER:
List<Paciente> pacientesMock = Arrays.asList(
    new Paciente("Paciente 1"),
    new Paciente("Paciente 2"),
    // ...
);
```

**3. Substituir por dados reais da API:**
```java
// ✅ USAR ISTO:
private void carregarDadosPainel() {
    // Buscar número atual de pacientes
    apiService.getTotalPacientes().enqueue(new Callback<Integer>() {
        @Override
        public void onResponse(Call<Integer> call, Response<Integer> response) {
            if (response.isSuccessful()) {
                int total = response.body();
                tvTotalPacientes.setText(String.valueOf(total));
            }
        }
    });
}
```

## 🎯 **TAREFA 3: PAINEL DINÂMICO**

### **Arquivos a modificar (encontrar onde mostra pacientes):**
- ProfessionalMainActivity.java
- PatientsListActivity.java
- Qualquer Activity com "paciente" no nome

**Adicionar método em todas essas Activities:**
```java
@Override
protected void onResume() {
    super.onResume();
    // Sempre buscar dados atualizados ao voltar para a tela
    carregarDadosPainel();
}

private void carregarDadosPainel() {
    // Buscar dados atualizados da API
    // Implementar chamada à API para contar pacientes
}
```

**Após criar/remover paciente, adicionar:**
```java
// Após operação CRUD bem-sucedida:
carregarDadosPainel(); // Atualizar imediatamente
```

## 🧪 **COMO TESTAR TUDO**

### **Teste 1: Login**
- `profissional@smartsaude.com` / `prof123` → ProfessionalMainActivity
- `joao.paciente@smartsaude.com` / `pac123` → MainActivity

### **Teste 2: Pacientes Reais**
- App deve mostrar "7 pacientes" (não "12")
- Lista deve mostrar apenas os 7 pacientes reais

### **Teste 3: Atualização Dinâmica**
- Criar paciente → Total aumenta para 8
- Remover paciente → Total volta para 7
- Entrar no app → Sempre mostra total atual

## 🚨 **IMPORTANTE**

1. **Não mexer no backend** (já está pronto)
2. **Remover todos os dados mock/hardcoded**
3. **Usar apenas dados da API**
4. **Testar cada funcionalidade**

## 📊 **RESULTADO ESPERADO**

✅ **Login direciona corretamente**  
✅ **Painel mostra 7 pacientes reais**  
✅ **Painel atualiza dinamicamente**  
✅ **Sem dados ilusórios**

---

**Status:** 🔄 **AGUARDANDO IMPLEMENTAÇÃO DAS CORREÇÕES NO FRONTEND**
