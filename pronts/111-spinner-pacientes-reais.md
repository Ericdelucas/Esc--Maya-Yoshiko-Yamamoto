# 🔧 **SPINNER COM PACIENTES REAIS - DADOS DO BANCO**

## ✅ **PROBLEMA RESOLVIDO**

**Backend agora retorna pacientes REAIS cadastrados no sistema!**

---

## 🎯 **PACIENTES REAIS NO BANCO**

### **Dados atuais dos pacientes:**

```sql
-- Pacientes cadastrados no sistema:
ID  | full_name | email                | role
----|-----------|----------------------|------
2   | aaaaa     | aaaaa@hotmail.com     | patient
3   | cria      | cria@gmail.com        | patient
```

**Importante:** Como `full_name` está NULL no banco, o backend usa o email como nome de exibição.

---

## 📱 **IMPLEMENTAÇÃO ANDROID - PACIENTES REAIS**

### **PASSO 1: PatientModel (CORRIGIDO)**

#### **Arquivo:** `app/src/main/java/com/example/testbackend/models/Patient.java`

```java
package com.example.testbackend.models;

public class Patient {
    private Integer id;
    private String full_name;  // Vem do backend (email se full_name for null)
    private String email;
    private String role;
    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    // Override toString para aparecer corretamente no spinner
    @Override
    public String toString() {
        // Usar o nome que veio do backend (já tratado)
        return full_name != null ? full_name : "Paciente sem nome";
    }
}
```

### **PASSO 2: REMOVER FALLBACK FICTÍCIO**

#### **Arquivo:** `ProfessionalMainActivity.java`

```java
private void loadPatientsForSpinner() {
    // Mostrar loading
    showLoadingDialog();
    
    // API para listar pacientes REAIS
    String token = getToken();
    PatientApi patientApi = ApiClient.getPatientClient().create(PatientApi.class);
    
    patientApi.getPatients("Bearer " + token).enqueue(new Callback<List<Patient>>() {
        @Override
        public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
            hideLoadingDialog();
            
            if (response.isSuccessful() && response.body() != null) {
                List<Patient> patients = response.body();
                Log.d("PATIENTS_SUCCESS", "Carregados " + patients.size() + " pacientes REAIS");
                
                // Debug: mostrar pacientes REAIS
                for (Patient patient : patients) {
                    Log.d("PATIENT_REAL", "ID: " + patient.getId() + 
                          ", Nome: " + patient.getFull_name() + 
                          ", Email: " + patient.getEmail());
                }
                
                if (patients.isEmpty()) {
                    Toast.makeText(ProfessionalMainActivity.this, 
                        "Nenhum paciente encontrado. Cadastre pacientes primeiro.", 
                        Toast.LENGTH_LONG).show();
                    return;
                }
                
                setupPatientSpinner(patients);
            } else {
                Toast.makeText(ProfessionalMainActivity.this, 
                    "Erro ao carregar pacientes: " + response.code(), 
                    Toast.LENGTH_SHORT).show();
                Log.e("PATIENTS_ERROR", "Response code: " + response.code());
                Log.e("PATIENTS_ERROR", "Response message: " + response.message());
                
                // NÃO usar fallback - mostrar erro real
                showErrorDialog();
            }
        }
        
        @Override
        public void onFailure(Call<List<Patient>> call, Throwable t) {
            hideLoadingDialog();
            Toast.makeText(ProfessionalMainActivity.this, 
                "Erro de conexão: " + t.getMessage(), 
                Toast.LENGTH_SHORT).show();
            Log.e("PATIENTS_ERROR", "Network error", t);
            
            // NÃO usar fallback - mostrar erro real
            showErrorDialog();
        }
    });
}

private void showErrorDialog() {
    new AlertDialog.Builder(this)
        .setTitle("Erro ao Carregar Pacientes")
        .setMessage("Não foi possível carregar a lista de pacientes. Verifique:\n" +
                   "1. Sua conexão com a internet\n" +
                   "2. Se você está logado como profissional\n" +
                   "3. Se existem pacientes cadastrados no sistema")
        .setPositiveButton("Tentar Novamente", (dialog, which) -> loadPatientsForSpinner())
        .setNegativeButton("Cancelar", null)
        .show();
}

private void setupPatientSpinner(List<Patient> patients) {
    Spinner spPatient = findViewById(R.id.spPatient);
    
    // Criar adapter com pacientes REAIS
    ArrayAdapter<Patient> adapter = new ArrayAdapter<Patient>(
        this, 
        android.R.layout.simple_spinner_item, 
        patients
    );
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spPatient.setAdapter(adapter);
    
    // Listener para capturar seleção
    spPatient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Patient selectedPatient = (Patient) parent.getItemAtPosition(position);
            Log.d("PATIENT_SELECTED", "Paciente REAL selecionado: " + selectedPatient.getFull_name());
            Log.d("PATIENT_SELECTED", "ID do paciente: " + selectedPatient.getId());
            Log.d("PATIENT_SELECTED", "Email: " + selectedPatient.getEmail());
        }
        
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Nada selecionado
        }
    });
}
```

### **PASSO 3: VERIFICAR TOKEN E PERMISSÕES**

```java
private void checkProfessionalAccess() {
    String token = getToken();
    if (token == null || token.isEmpty()) {
        Toast.makeText(this, "Você não está logado", Toast.LENGTH_LONG).show();
        finish();
        return;
    }
    
    // Verificar se usuário é profissional
    AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
    authApi.getMe("Bearer " + token).enqueue(new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful()) {
                User user = response.body();
                String role = user.getRole();
                
                Log.d("USER_CHECK", "Usuário: " + user.getEmail() + ", Role: " + role);
                
                if (!"professional".equals(role) && !"doctor".equals(role) && !"admin".equals(role)) {
                    Toast.makeText(ProfessionalMainActivity.this, 
                        "Apenas profissionais podem acessar pacientes. Seu role: " + role, 
                        Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                
                // Se for profissional, carregar pacientes
                loadPatientsForSpinner();
            } else {
                Toast.makeText(ProfessionalMainActivity.this, 
                    "Erro ao verificar permissões: " + response.code(), 
                    Toast.LENGTH_LONG).show();
                finish();
            }
        }
        
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            Toast.makeText(ProfessionalMainActivity.this, 
                "Erro de conexão ao verificar permissões", 
                Toast.LENGTH_LONG).show();
            finish();
        }
    });
}
```

---

## 🧪 **TESTAR COM PACIENTES REAIS**

### **Como testar o endpoint:**

```bash
# 1. Fazer login como profissional
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"seu_email@profissional.com","password":"sua_senha"}'

# 2. Usar token para ver pacientes REAIS
curl -H "Authorization: Bearer SEU_TOKEN" \
     http://localhost:8080/professional/pacientes
```

### **Resposta esperada (pacientes REAIS):**
```json
[
  {
    "id": 2,
    "full_name": "aaaaa",  // Email se full_name for null
    "email": "aaaaa@hotmail.com",
    "cpf": null,
    "phone": null,
    "birth_date": null,
    "role": "patient"
  },
  {
    "id": 3,
    "full_name": "cria",  // Email se full_name for null
    "email": "cria@gmail.com",
    "cpf": null,
    "phone": null,
    "birth_date": null,
    "role": "patient"
  }
]
```

---

## 🔧 **PARA CADASTRAR MAIS PACIENTES**

### **Se precisar de mais pacientes:**

```bash
# Cadastrar paciente novo
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "novo.paciente@email.com",
    "password": "senha123",
    "role": "patient"
  }'
```

### **Ou atualizar nome de pacientes existentes:**

```sql
-- Atualizar full_name no banco
UPDATE users SET full_name = 'Ana Silva' WHERE email = 'aaaaa@hotmail.com';
UPDATE users SET full_name = 'Carlos Santos' WHERE email = 'cria@gmail.com';
```

---

## 🚨 **IMPORTANTE - NÃO USAR DADOS FICTÍCIOS**

### **O que NÃO fazer:**
- ❌ Criar pacientes hardcoded ("João Silva", "Maria Santos")
- ❌ Usar lista demo se API funcionar
- ❌ Inventar dados que não existem no banco

### **O que fazer:**
- ✅ Usar apenas pacientes que vêm da API
- ✅ Mostrar erro se não houver pacientes
- ✅ Permitir que profissional cadastre pacientes primeiro

---

## ✅ **CHECKLIST FINAL**

- [ ] **Backend retorna pacientes REAIS** do banco
- [ ] **Android mostra apenas pacientes reais**
- [ ] **Sem fallback fictício** - erro real se API falhar
- [ ] **Verifica permissões** antes de carregar
- [ ] **Logs mostram dados reais** no Logcat

---

## 🎯 **RESULTADO ESPERADO**

1. **Clicar em "Nova Tarefa"**
2. **Spinner mostra pacientes REAIS:**
   - "aaaaa" (aaaaa@hotmail.com)
   - "cria" (cria@gmail.com)
3. **Conseguir selecionar paciente real**
4. **Criar tarefa** para paciente real
5. **Paciente real** vê a tarefa

---

## 🚀 **FLUXO COM DADOS REAIS**

```
Clicar "Nova Tarefa"
    ↓
checkProfessionalAccess()
    ↓
API /professional/pacientes
    ↓
Retorna pacientes REAIS do banco
    ↓
Spinner mostra: "aaaaa", "cria"
    ↓
Selecionar paciente real
    ↓
Criar tarefa para paciente real
```

**Agora o sistema usa dados REAIS! 🎉**
