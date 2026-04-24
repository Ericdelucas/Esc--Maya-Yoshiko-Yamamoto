# 🔧 **CORRIGIR SPINNER DE PACIENTES - GUIA ESPECÍFICO**

## 🎯 **PROBLEMA IDENTIFICADO**

**Spinner de pacientes não mostra nada quando clica em "Nova Tarefa"**

---

## 🔍 **ANÁLISE DO PROBLEMA**

### **O que está acontecendo:**
1. Spinner está vazio (sem pacientes carregados)
2. API para listar pacientes não está sendo chamada
3. Adapter do Spinner não está configurado corretamente
4. Possível erro de permissão ou token

---

## 🛠️ **SOLUÇÃO PASSO A PASSO**

### **PASSO 1: VERIFICAR SE EXISTE API DE PACIENTES**

#### **Arquivo:** `ProfessionalMainActivity.java`
#### **Local exato:** `app/src/main/java/com/example/testbackend/ProfessionalMainActivity.java`

**O que procurar:**
```java
// Procure por código que carrega pacientes
// Se não existir, precisa adicionar
```

### **PASSO 2: CRIAR MÉTODO PARA CARREGAR PACIENTES**

#### **Adicionar este método na ProfessionalMainActivity.java:**

```java
private void loadPatientsForSpinner() {
    // Mostrar loading
    showLoadingDialog();
    
    // API para listar pacientes
    String token = getToken(); // Obter token JWT
    PatientApi patientApi = ApiClient.getPatientClient().create(PatientApi.class);
    
    patientApi.getPatients("Bearer " + token).enqueue(new Callback<List<Patient>>() {
        @Override
        public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
            hideLoadingDialog();
            
            if (response.isSuccessful() && response.body() != null) {
                List<Patient> patients = response.body();
                setupPatientSpinner(patients);
            } else {
                Toast.makeText(ProfessionalMainActivity.this, 
                    "Erro ao carregar pacientes: " + response.code(), 
                    Toast.LENGTH_SHORT).show();
                Log.e("PATIENTS_ERROR", "Response code: " + response.code());
            }
        }
        
        @Override
        public void onFailure(Call<List<Patient>> call, Throwable t) {
            hideLoadingDialog();
            Toast.makeText(ProfessionalMainActivity.this, 
                "Erro de conexão: " + t.getMessage(), 
                Toast.LENGTH_SHORT).show();
            Log.e("PATIENTS_ERROR", "Network error", t);
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
            Log.d("PATIENT_SELECTED", "Paciente selecionado: " + selectedPatient.getName());
        }
        
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Nada selecionado
        }
    });
}
```

### **PASSO 3: VERIFICAR SE EXISTE PatientApi**

#### **Arquivo:** `app/src/main/java/com/example/testbackend/network/PatientApi.java`

**Se não existir, criar:**
```java
package com.example.testbackend.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import com.example.testbackend.models.Patient;

public interface PatientApi {
    @GET("pacientes") // Verificar endpoint correto
    Call<List<Patient>> getPatients(@Header("Authorization") String token);
    
    @GET("patients") // Tentar este endpoint se o acima não funcionar
    Call<List<Patient>> getPatientsAlt(@Header("Authorization") String token);
}
```

### **PASSO 4: VERIFICAR SE EXISTE Patient MODEL**

#### **Arquivo:** `app/src/main/java/com/example/testbackend/models/Patient.java`

**Se não existir, criar:**
```java
package com.example.testbackend.models;

public class Patient {
    private Integer id;
    private String name;
    private String email;
    private String cpf;
    private String phone;
    private String birth_date;
    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getBirth_date() { return birth_date; }
    public void setBirth_date(String birth_date) { this.birth_date = birth_date; }
    
    // Override toString para aparecer corretamente no spinner
    @Override
    public String toString() {
        return name != null ? name : "Paciente sem nome";
    }
}
```

### **PASSO 5: ATUALIZAR ApiClient**

#### **Arquivo:** `app/src/main/java/com/example/testbackend/network/ApiClient.java`

**Adicionar método:**
```java
public static Retrofit getPatientClient() {
    return new Retrofit.Builder()
        .baseUrl(Constants.AUTH_BASE_URL) // Usar auth-service
        .addConverterFactory(GsonConverterFactory.create())
        .build();
}
```

### **PASSO 6: INTEGRAR NO DIÁLOGO DE CRIAÇÃO**

#### **No método showCreateTaskDialog():**

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

private void createTask(String title, String description, int points, int patientId, int frequency) {
    // Criar request
    TaskCreateRequest request = new TaskCreateRequest();
    request.setTitle(title);
    request.setDescription(description);
    request.setPoints_value(points);
    request.setPatient_id(patientId);
    request.setFrequency_per_week(frequency);
    request.setStart_date(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    
    // Chamar API
    TaskApi taskApi = ApiClient.getTaskClient().create(TaskApi.class);
    String token = getToken();
    
    taskApi.createTask("Bearer " + token, request).enqueue(new Callback<Task>() {
        @Override
        public void onResponse(Call<Task> call, Response<Task> response) {
            if (response.isSuccessful()) {
                Toast.makeText(ProfessionalMainActivity.this, 
                    "Tarefa criada com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfessionalMainActivity.this, 
                    "Erro ao criar tarefa: " + response.code(), Toast.LENGTH_SHORT).show();
                Log.e("TASK_ERROR", "Response: " + response.message());
            }
        }
        
        @Override
        public void onFailure(Call<Task> call, Throwable t) {
            Toast.makeText(ProfessionalMainActivity.this, 
                "Erro de conexão", Toast.LENGTH_SHORT).show();
            Log.e("TASK_ERROR", "Network error", t);
        }
    });
}
```

---

## 🚨 **DEBUG - SE CONTINUAR NÃO FUNCIONANDO**

### **VERIFICAÇÃO 1: ENDPOINT CORRETO**

**Testar endpoints manualmente:**
```bash
# Testar se endpoint existe
curl -H "Authorization: Bearer SEU_TOKEN" \
     http://localhost:8080/pacientes

curl -H "Authorization: Bearer SEU_TOKEN" \
     http://localhost:8080/patients

curl -H "Authorization: Bearer SEU_TOKEN" \
     http://localhost:8080/users/patientes
```

### **VERIFICAÇÃO 2: TOKEN VÁLIDO**

**Adicionar log para verificar token:**
```java
private void loadPatientsForSpinner() {
    String token = getToken();
    Log.d("TOKEN_DEBUG", "Token: " + token);
    Log.d("TOKEN_DEBUG", "Token length: " + (token != null ? token.length() : 0));
    
    // ... resto do código
}
```

### **VERIFICAÇÃO 3: PERMISSÕES**

**Verificar se usuário é profissional:**
```java
private void checkUserPermissions() {
    String token = getToken();
    // Decodificar token JWT para verificar role
    // Ou usar API /auth/me para verificar
    
    AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
    authApi.getMe("Bearer " + token).enqueue(new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful()) {
                User user = response.body();
                Log.d("USER_ROLE", "Role: " + user.getRole());
                
                if (!"professional".equals(user.getRole()) && !"doctor".equals(user.getRole())) {
                    Toast.makeText(ProfessionalMainActivity.this, 
                        "Apenas profissionais podem criar tarefas", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
        
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            Log.e("USER_ERROR", "Failed to get user info", t);
        }
    });
}
```

---

## 📋 **LAYOUT DO DIÁLOGO**

### **Arquivo:** `res/layout/dialog_create_task.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">
    
    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="8dp">
        
        <EditText
            android:id="@+id/etTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Título da tarefa"
            android:inputType="textCapWords" />
    </com.google.android.material.textfield.TextInputLayout>
    
    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="8dp">
        
        <EditText
            android:id="@+id/etDescription"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Descrição"
            android:minLines="3"
            android:inputType="textMultiLine" />
    </com.google.android.material.textfield.TextInputLayout>
    
    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="8dp">
        
        <EditText
            android:id="@+id/etPoints"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Pontos"
            android:inputType="number" />
    </com.google.android.material.textfield.TextInputLayout>
    
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Paciente:"
        android:layout_marginBottom="4dp"
        android:textStyle="bold" />
    
    <Spinner
        android:id="@+id/spPatient"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        android:background="@drawable/spinner_background" />
    
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Frequência semanal:"
        android:layout_marginBottom="4dp"
        android:textStyle="bold" />
    
    <Spinner
        android:id="@+id/spFrequency"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        android:entries="@array/frequency_options"
        android:background="@drawable/spinner_background" />
    
</LinearLayout>
```

---

## 🔧 **STRINGS PARA FREQUÊNCIA**

### **Arquivo:** `res/values/strings.xml`

```xml
<string-array name="frequency_options">
    <item>1 vez por semana</item>
    <item>2 vezes por semana</item>
    <item>3 vezes por semana</item>
    <item>4 vezes por semana</item>
    <item>5 vezes por semana</item>
    <item>6 vezes por semana</item>
    <item>Todos os dias</item>
</string-array>
```

---

## ✅ **CHECKLIST DE VERIFICAÇÃO**

Antes de testar, verifique:

- [ ] **PatientApi.java** existe
- [ ] **Patient.java** model existe
- [ ] **getPatientClient()** em ApiClient.java
- [ ] **loadPatientsForSpinner()** está sendo chamado
- [ ] **Token JWT** é válido
- [ ] **Endpoint** está correto
- [ ] **Permissões** do usuário
- [ ] **Layout** dialog_create_task.xml existe

---

## 🚨 **SE AINDA NÃO FUNCIONAR**

### **Solução Alternativa - Lista Simples:**

```java
// Se API não funcionar, usar lista hardcoded temporariamente
private void setupPatientSpinnerFallback() {
    List<Patient> patients = new ArrayList<>();
    patients.add(new Patient(1, "João Silva"));
    patients.add(new Patient(2, "Maria Santos"));
    patients.add(new Patient(3, "Pedro Oliveira"));
    
    setupPatientSpinner(patients);
}
```

---

## 🎯 **RESULTADO ESPERADO**

1. **Clicar em "Nova Tarefa"**
2. **Spinner de pacientes** carrega com lista
3. **Conseguir selecionar** paciente
4. **Criar tarefa** com paciente selecionado

**Se seguir estes passos, o spinner vai funcionar! 🚀**
