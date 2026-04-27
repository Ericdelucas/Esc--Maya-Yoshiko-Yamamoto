# 🛠️ **GUIA - MODIFICAR APP ANDROID**

## ✅ **HEALTHTOOLSAPI.JA JÁ ATUALIZADO!**

### **🔥 O que foi adicionado:**
```java
@POST("health-tools/calculate-bmi-test")
Call<BMIResponse> calculateBMITest(@Body BMICalculationRequest request);

@POST("health-tools/save-questionnaire-test")
Call<QuestionnaireResponse> saveQuestionnaireTest(@Body QuestionnaireRequest request);
```

---

## 🎯 **ONDE MODIFICAR NO APP:**

### **✅ Encontrar onde chama IMC:**
Procure por: `healthApi.calculateBMI(token, request)`

**Trocar para:**
```java
healthApi.calculateBMITest(request).enqueue(new Callback<HealthToolsApi.BMIResponse>() {
    @Override
    public void onResponse(Call<HealthToolsApi.BMIResponse> call, Response<HealthToolsApi.BMIResponse> response) {
        // Mesmo código de tratamento
    }
    
    @Override
    public void onFailure(Call<HealthToolsApi.BMIResponse> call, Throwable t) {
        // Mesmo código de erro
    }
});
```

### **✅ Encontrar onde chama Questionário:**
Procure por: `healthApi.saveQuestionnaire(token, request)`

**Trocar para:**
```java
healthApi.saveQuestionnaireTest(request).enqueue(new Callback<HealthToolsApi.QuestionnaireResponse>() {
    @Override
    public void onResponse(Call<HealthToolsApi.QuestionnaireResponse> call, Response<HealthToolsApi.QuestionnaireResponse> response) {
        // Mesmo código de tratamento
    }
    
    @Override
    public void onFailure(Call<HealthToolsApi.QuestionnaireResponse> call, Throwable t) {
        // Mesmo código de erro
    }
});
```

---

## 🔍 **COMO ENCONTRAR ESSES CÓDIGOS:**

### **✅ No Android Studio:**
1. **Ctrl+Shift+F** (busca global)
2. **Procurar:** `calculateBMI`
3. **Procurar:** `saveQuestionnaire`
4. **Modificar as chamadas**

### **✅ Prováveis arquivos:**
- `HealthQuestionnaireActivity.java` - já modificado
- Alguma activity de IMC/cálculo
- Fragmentos de saúde

---

## 🧪 **COMO TESTAR:**

### **✅ Passo 1 - Recompilar:**
```bash
cd front/Esc--Maya-Yoshiko-Yamamoto/testbackend
./gradlew clean
./gradlew assembleDebug
```

### **✅ Passo 2 - Instalar:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### **✅ Passo 3 - Redirecionar porta:**
```bash
adb reverse tcp:8080 tcp:8080
```

### **✅ Passo 4 - Testar:**
1. **Abrir app**
2. **Fazer login**
3. **Ir em Ferramentas de Saúde**
4. **Testar IMC e Questionário**

---

## 📊 **RESULTADO ESPERADO:**

### **✅ Sem mais erros 500:**
- **IMC:** Deve calcular e mostrar resultado
- **Questionário:** Deve salvar e mostrar pontuação
- **Logs:** Apenas 200 OK no backend

### **✅ Logs no backend:**
```
POST /health-tools/calculate-bmi-test HTTP/1.1" 200 OK
POST /health-tools/save-questionnaire-test HTTP/1.1" 200 OK
```

---

## 🚨 **SE AINDA DER ERRO:**

### **❌ Verificar:**
- [ ] **Recompilou o app?**
- [ ] **Instalou o APK novo?**
- [ ] **Usou os métodos `-test`?**
- [ ] **Redirecionou porta?**

### **❌ Debug no app:**
```java
// Adicionar logs
Log.d("HealthTools", "Usando endpoint de teste: calculateBMITest");
Log.d("HealthTools", "Response: " + response.code());
```

---

## 🎯 **RESUMO:**

1. **✅ HealthToolsApi.java** - atualizado com endpoints de teste
2. **🔄 Modificar chamadas** no app para usar `-test`
3. **🧪 Recompilar e testar**
4. **📊 Verificar logs 200 OK**

**Agora é só modificar as chamadas no app e recompilar! 🚀🎯**
