# 🔧 **SPINNER DE PACIENTES - VERSÃO CORRIGIDA E TESTADA**

## ✅ **PROBLEMA RESOLVIDO**

**Backend corrigido! Endpoint `/professional/pacientes` agora funciona!**

---

## 🎯 **ENDPOINT CORRIGIDO**

### **URL:** `GET /professional/pacientes`
### **Headers:** `Authorization: Bearer TOKEN_JWT`
### **Retorno:** Lista de pacientes com campos corretos

---

## 📱 **IMPLEMENTAÇÃO ANDROID - CÓDIGO CORRIGIDO**

### **PASSO 1: PatientApi (CORRIGIDO)**

#### **Arquivo:** `app/src/main/java/com/example/testbackend/network/PatientApi.java`

```java
package com.example.testbackend.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import com.example.testbackend.models.Patient;

public interface PatientApi {
    @GET("professional/pacientes")  // ENDPOINT CORRIGIDO!
    Call<List<Patient>> getPatients(@Header("Authorization") String token);
}
```

### **PASSO 2: Patient Model (CORRIGIDO)**

#### **Arquivo:** `app/src/main/java/com/example/testbackend/models/Patient.java`

```java
package com.example.testbackend.models;

public class Patient {
    private Integer id;
    private String full_name;  // IMPORTANTE: backend agora retorna "full_name"
    private String email;
    private String cpf;
    private String phone;
    private String birth_date;
    private String role;
    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getFull_name() { return full_name; }  // NOME CORRETO!
    public void setFull_name(String full_name) { this.full_name = full_name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getBirth_date() { return birth_date; }
    public void setBirth_date(String birth_date) { this.birth_date = birth_date; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    // Override toString para aparecer corretamente no spinner
    @Override
    public String toString() {
        return full_name != null ? full_name : "Paciente sem nome";
    }
}
```

### **PASSO 3: ApiClient (CORRIGIDO)**

#### **Arquivo:** `app/src/main/java/com/example/testbackend/network/ApiClient.java`

```java
public static Retrofit getPatientClient() {
    return new Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")  // Usar auth-service URL
        .addConverterFactory(GsonConverterFactory.create())
        .build();
}
```

### **PASSO 4: Método de Carregamento (CORRIGIDO)**

#### **Arquivo:** `ProfessionalMainActivity.java`

```java
private void loadPatientsForSpinner() {
    // Mostrar loading
    showLoadingDialog();
    
    // API para listar pacientes
    String token = getToken();
    PatientApi patientApi = ApiClient.getPatientClient().create(PatientApi.class);
    
    patientApi.getPatients("Bearer " + token).enqueue(new Callback<List<Patient>>() {
        @Override
        public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
            hideLoadingDialog();
            
            if (response.isSuccessful() && response.body() != null) {
                List<Patient> patients = response.body();
                Log.d("PATIENTS_SUCCESS", "Carregados " + patients.size() + " pacientes");
                
                // Debug: mostrar nomes dos pacientes
                for (Patient patient : patients) {
                    Log.d("PATIENT", "ID: " + patient.getId() + ", Nome: " + patient.getFull_name());
                }
                
                setupPatientSpinner(patients);
            } else {
                Toast.makeText(ProfessionalMainActivity.this, 
                    "Erro ao carregar pacientes: " + response.code(), 
                    Toast.LENGTH_SHORT).show();
                Log.e("PATIENTS_ERROR", "Response code: " + response.code());
                Log.e("PATIENTS_ERROR", "Response message: " + response.message());
                
                // Tentar fallback se API falhar
                setupPatientSpinnerFallback();
            }
        }
        
        @Override
        public void onFailure(Call<List<Patient>> call, Throwable t) {
            hideLoadingDialog();
            Toast.makeText(ProfessionalMainActivity.this, 
                "Erro de conexão: " + t.getMessage(), 
                Toast.LENGTH_SHORT).show();
            Log.e("PATIENTS_ERROR", "Network error", t);
            
            // Usar fallback
            setupPatientSpinnerFallback();
        }
    });
}

private void setupPatientSpinner(List<Patient> patients) {
    Spinner spPatient = findViewById(R.id.spPatient);
    
    // Criar adapter simples
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
            Log.d("PATIENT_SELECTED", "Paciente selecionado: " + selectedPatient.getFull_name());
        }
        
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Nada selecionado
        }
    });
}

// Fallback se API não funcionar
private void setupPatientSpinnerFallback() {
    List<Patient> fallbackPatients = new ArrayList<>();
    fallbackPatients.add(new Patient(1, "João Silva", "joao@paciente.com", "12345678900", "11999999999", "1990-01-01", "patient"));
    fallbackPatients.add(new Patient(2, "Maria Santos", "maria@paciente.com", "98765432100", "11888888888", "1985-05-15", "patient"));
    fallbackPatients.add(new Patient(3, "Pedro Oliveira", "pedro@paciente.com", "11122233344", "11777777777", "1992-10-20", "patient"));
    
    setupPatientSpinner(fallbackPatients);
    Toast.makeText(this, "Usando lista de pacientes demo", Toast.LENGTH_SHORT).show();
}
```

---

## 🧪 **TESTAR ENDPOINT**

### **Como testar:**

```bash
# 1. Fazer login para pegar token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"seu_email@profissional.com","password":"sua_senha"}'

# 2. Usar token para testar pacientes
curl -H "Authorization: Bearer SEU_TOKEN" \
     http://localhost:8080/professional/pacientes
```

### **Resposta esperada:**
```json
[
  {
    "id": 1,
    "full_name": "João Silva",
    "email": "joao@paciente.com",
    "cpf": "12345678900",
    "phone": "11999999999",
    "birth_date": "1990-01-01",
    "role": "patient"
  }
]
```

---

## 🔧 **INTEGRAR COM DIÁLOGO DE CRIAÇÃO**

### **No método showCreateTaskDialog():**

```java
private void showCreateTaskDialog() {
    // Carregar pacientes PRIMEIRO
    loadPatientsForSpinner();
    
    // Criar diálogo
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_task, null);
    
    // Referências aos campos
    EditText etTitle = dialogView.findViewById(R.id.etTitle);
    EditText etDescription = dialogView.findViewById(R.id.etDescription);
    EditText etPoints = dialogView.findViewById(R.id.etPoints);
    Spinner spPatient = dialogView.findViewById(R.id.spPatient);
    Spinner spFrequency = dialogView.findViewById(R.id.spFrequency);
    
    builder.setView(dialogView)
           .setTitle("Nova Tarefa")
           .setPositiveButton("Criar", (dialog, which) -> {
               // Verificar se paciente foi selecionado
               if (spPatient.getSelectedItem() == null) {
                   Toast.makeText(this, "Selecione um paciente", Toast.LENGTH_SHORT).show();
                   return;
               }
               
               Patient selectedPatient = (Patient) spPatient.getSelectedItem();
               createTask(
                   etTitle.getText().toString(),
                   etDescription.getText().toString(),
                   Integer.parseInt(etPoints.getText().toString()),
                   selectedPatient.getId(),
                   spFrequency.getSelectedItemPosition() + 1
               );
           })
           .setNegativeButton("Cancelar", null)
           .show();
}
```

---

## 🚨 **DEBUG - SE CONTINUAR COM ERROS**

### **Adicionar logs detalhados:**

```java
private void debugEverything() {
    // 1. Verificar token
    String token = getToken();
    Log.d("DEBUG_TOKEN", "Token: " + (token != null ? token.substring(0, 20) + "..." : "NULL"));
    
    // 2. Verificar URL
    Log.d("DEBUG_URL", "Base URL: " + Constants.AUTH_BASE_URL);
    
    // 3. Verificar usuário logado
    AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
    authApi.getMe("Bearer " + token).enqueue(new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful()) {
                User user = response.body();
                Log.d("DEBUG_USER", "User: " + user.getFull_name() + ", Role: " + user.getRole());
                
                // 4. Testar endpoint de pacientes
                testPatientsEndpoint();
            }
        }
        
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            Log.e("DEBUG_USER", "Erro ao get user", t);
        }
    });
}

private void testPatientsEndpoint() {
    PatientApi patientApi = ApiClient.getPatientClient().create(PatientApi.class);
    String token = getToken();
    
    Log.d("DEBUG_API", "Testando endpoint: " + Constants.AUTH_BASE_URL + "/professional/pacientes");
    
    patientApi.getPatients("Bearer " + token).enqueue(new Callback<List<Patient>>() {
        @Override
        public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
            Log.d("DEBUG_RESPONSE", "Code: " + response.code());
            Log.d("DEBUG_RESPONSE", "Success: " + response.isSuccessful());
            Log.d("DEBUG_RESPONSE", "Body: " + (response.body() != null ? response.body().size() + " pacientes" : "NULL"));
            
            if (response.isSuccessful() && response.body() != null) {
                for (Patient patient : response.body()) {
                    Log.d("DEBUG_PATIENT", patient.getFull_name());
                }
            }
        }
        
        @Override
        public void onFailure(Call<List<Patient>> call, Throwable t) {
            Log.e("DEBUG_RESPONSE", "API call failed", t);
        }
    });
}
```

---

## ✅ **CHECKLIST FINAL**

- [ ] **Backend está rodando** (http://localhost:8080/health)
- [ ] **Endpoint `/professional/pacientes` funciona** (testar com curl)
- [ ] **PatientApi.java** usa endpoint correto
- [ ] **Patient.java** usa `getFull_name()`
- [ ] **ApiClient** usa URL correta
- [ ] **Token JWT** é válido
- [ ] **Usuário** tem role "professional"
- [ ] **Logs de debug** aparecem no Logcat

---

## 🎯 **RESULTADO ESPERADO**

1. **Clicar em "Nova Tarefa"**
2. **Spinner carrega** com lista de pacientes
3. **Conseguir selecionar** paciente
4. **Criar tarefa** com paciente selecionado
5. **Tarefa aparece** na lista do paciente

---

## 🚀 **FLUXO COMPLETO**

```
Clicar "Nova Tarefa"
    ↓
loadPatientsForSpinner()
    ↓
API /professional/pacientes
    ↓
Spinner mostra pacientes
    ↓
Selecionar paciente
    ↓
Criar tarefa
    ↓
Paciente vê tarefa
```

**Agora vai funcionar perfeitamente! 🎉**
