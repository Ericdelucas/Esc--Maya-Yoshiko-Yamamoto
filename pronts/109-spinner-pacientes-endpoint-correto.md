# 🔧 **ENDPOINT DE PACIENTES CRIADO - ATUALIZAÇÃO IMEDIATA**

## ✅ **PROBLEMA RESOLVIDO**

**Endpoint `/pacientes` foi criado no backend!**

---

## 🎯 **ENDPOINT NOVO DISPONÍVEL**

### **URL:** `GET /professional/pacientes`
### **Headers:** `Authorization: Bearer TOKEN_JWT`
### **Retorno:** Lista de pacientes

---

## 📱 **COMO ATUALIZAR O ANDROID**

### **PASSO 1: ATUALIZAR PatientApi**

#### **Arquivo:** `app/src/main/java/com/example/testbackend/network/PatientApi.java`

```java
package com.example.testbackend.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import com.example.testbackend.models.Patient;

public interface PatientApi {
    @GET("professional/pacientes")  // ENDPOINT CORRETO!
    Call<List<Patient>> getPatients(@Header("Authorization") String token);
}
```

### **PASSO 2: ATUALIZAR ApiClient**

#### **Arquivo:** `app/src/main/java/com/example/testbackend/network/ApiClient.java`

```java
public static Retrofit getPatientClient() {
    return new Retrofit.Builder()
        .baseUrl(Constants.AUTH_BASE_URL)  // Usar auth-service
        .addConverterFactory(GsonConverterFactory.create())
        .build();
}
```

### **PASSO 3: VERIFICAR Patient MODEL**

#### **Arquivo:** `app/src/main/java/com/example/testbackend/models/Patient.java`

```java
package com.example.testbackend.models;

public class Patient {
    private Integer id;
    private String full_name;  // IMPORTANTE: usar "full_name" não "name"
    private String email;
    private String cpf;
    private String phone;
    private String birth_date;
    private String role;
    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getFull_name() { return full_name; }  // IMPORTANTE!
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

### **PASSO 4: ATUALIZAR MÉTODO DE CARREGAMENTO**

#### **Arquivo:** `ProfessionalMainActivity.java`

```java
private void loadPatientsForSpinner() {
    // Mostrar loading
    showLoadingDialog();
    
    // API para listar pacientes - ENDPOINT CORRETO!
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
                Log.e("PATIENTS_ERROR", "Response body: " + response.message());
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
```

---

## 🧪 **TESTAR ENDPOINT MANUALMENTE**

### **Como testar no terminal:**

```bash
# 1. Fazer login para pegar token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"seu_email@profissional.com","password":"sua_senha"}'

# 2. Usar token retornado para testar endpoint
curl -H "Authorization: Bearer SEU_TOKEN_AQUI" \
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
  },
  {
    "id": 2,
    "full_name": "Maria Santos",
    "email": "maria@paciente.com",
    "cpf": "98765432100",
    "phone": "11888888888",
    "birth_date": "1985-05-15",
    "role": "patient"
  }
]
```

---

## 🔍 **DEBUG - SE CONTINUAR COM ERROS**

### **VERIFICAÇÃO 1: Token JWT**

```java
private void debugToken() {
    String token = getToken();
    Log.d("TOKEN_DEBUG", "Token: " + token);
    Log.d("TOKEN_DEBUG", "Token length: " + (token != null ? token.length() : 0));
    
    // Testar token na API /auth/me
    AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
    authApi.getMe("Bearer " + token).enqueue(new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful()) {
                User user = response.body();
                Log.d("USER_DEBUG", "User: " + user.getFull_name() + ", Role: " + user.getRole());
            } else {
                Log.e("USER_DEBUG", "Token inválido: " + response.code());
            }
        }
        
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            Log.e("USER_DEBUG", "Erro ao validar token", t);
        }
    });
}
```

### **VERIFICAÇÃO 2: Permissões**

```java
private void checkProfessionalAccess() {
    String token = getToken();
    AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
    
    authApi.getMe("Bearer " + token).enqueue(new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful()) {
                User user = response.body();
                String role = user.getRole();
                
                if (!"professional".equals(role) && !"doctor".equals(role) && !"admin".equals(role)) {
                    Toast.makeText(ProfessionalMainActivity.this, 
                        "Apenas profissionais podem acessar pacientes", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                
                Log.d("ROLE_DEBUG", "Acesso permitido para: " + role);
                loadPatientsForSpinner(); // Carregar pacientes só depois de verificar
            }
        }
        
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            Log.e("ROLE_ERROR", "Erro ao verificar permissões", t);
        }
    });
}
```

---

## 🚨 **PROBLEMAS COMUNS E SOLUÇÕES**

### **Problema: 403 Forbidden**
**Causa:** Token inválido ou usuário não é profissional  
**Solução:** Verificar token e role do usuário

### **Problema: Spinner vazio mas API funciona**
**Causa:** Adapter não configurado corretamente  
**Solução:** Verificar setupPatientSpinner()

### **Problema: Nomes aparecem como null**
**Causa:** Usando "name" em vez de "full_name"  
**Solução:** Corrigir Patient model para usar "full_name"

---

## ✅ **CHECKLIST FINAL**

Antes de testar:

- [ ] **Endpoint `/professional/pacientes` está funcionando** (testar com curl)
- [ ] **PatientApi.java** usa endpoint correto
- [ ] **Patient.java** usa "full_name" não "name"
- [ ] **Token JWT** é válido
- [ ] **Usuário** tem role "professional" ou "doctor"
- [ ] **ApiClient** usa AUTH_BASE_URL
- [ ] **Adapter** está configurado corretamente

---

## 🎯 **RESULTADO ESPERADO**

1. **Clicar em "Nova Tarefa"**
2. **Spinner carrega com lista de pacientes**
3. **Conseguir selecionar paciente**
4. **Criar tarefa** com paciente selecionado
5. **Tarefa aparece** na lista do paciente

---

## 🚀 **PRÓXIMOS PASSOS**

1. **Testar endpoint manualmente**
2. **Implementar código Android**
3. **Debugar se necessário**
4. **Testar fluxo completo**

**Agora o endpoint existe e vai funcionar! 🎉**
