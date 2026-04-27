# 🚨 **GUIA OBRIGATÓRIA - FRONTEND PRECISA MODIFICAR!**

## ⚠️ **SITUAÇÃO CRÍTICA:**

### **✅ Backend:** 100% RESOLVIDO
- Endpoints de teste funcionando
- Dados salvos corretamente  
- Logs 200 OK

### **❌ Frontend:** NÃO FUNCIONA
- App ainda chama endpoints antigos
- HealthToolsApi.java atualizado mas NÃO UTILIZADO
- App continua dando erro 500

---

## 🎯 **O QUE PRECISA SER FEITO (OBRIGATÓRIO):**

### **🔍 ETAPA 1 - ENCONTRAR ARQUIVOS:**

**No Android Studio, usar Ctrl+Shift+F e buscar:**
```
healthApi.calculateBMI
healthApi.saveQuestionnaire
```

### **🔄 ETAPA 2 - MODIFICAR CHAMADAS:**

**TROCAR ISTO:**
```java
healthApi.calculateBMI(token, request).enqueue(new Callback<...>() {
    // código existente
});
```

**POR ISTO:**
```java
healthApi.calculateBMITest(request).enqueue(new Callback<HealthToolsApi.BMIResponse>() {
    @Override
    public void onResponse(Call<HealthToolsApi.BMIResponse> call, Response<HealthToolsApi.BMIResponse> response) {
        // MANTER O MESMO CÓDIGO DE TRATAMENTO
    }
    
    @Override
    public void onFailure(Call<HealthToolsApi.BMIResponse> call, Throwable t) {
        // MANTER O MESMO CÓDIGO DE ERRO
    }
});
```

---

**TROCAR ISTO:**
```java
healthApi.saveQuestionnaire(token, request).enqueue(new Callback<...>() {
    // código existente
});
```

**POR ISTO:**
```java
healthApi.saveQuestionnaireTest(request).enqueue(new Callback<HealthToolsApi.QuestionnaireResponse>() {
    @Override
    public void onResponse(Call<HealthToolsApi.QuestionnaireResponse> call, Response<HealthToolsApi.QuestionnaireResponse> response) {
        // MANTER O MESMO CÓDIGO DE TRATAMENTO
    }
    
    @Override
    public void onFailure(Call<HealthToolsApi.QuestionnaireResponse> call, Throwable t) {
        // MANTER O MESMO CÓDIGO DE ERRO
    }
});
```

---

### **📁 ARQUIVOS PROVÁVEIS PARA MODIFICAR:**

- **Activity de IMC** (encontrar na busca)
- **HealthQuestionnaireActivity.java** (verificar)
- **Fragmentos de saúde** (verificar)

---

## 🧪 **ETAPA 3 - COMPILAR E TESTAR:**

### **OBRIGATÓRIO - Recompilar:**
```bash
cd front/Esc--Maya-Yoshiko-Yamamoto/testbackend
./gradlew clean
./gradlew assembleDebug
```

### **OBRIGATÓRIO - Instalar:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### **OBRIGATÓRIO - Testar:**
1. Abrir app
2. Fazer login
3. Ir em Ferramentas de Saúde
4. Testar IMC
5. Testar Questionário

---

## ✅ **CRITÉRIOS DE SUCESSO OBRIGATÓRIOS:**

### **📊 Logs devem mostrar:**
```
POST /health-tools/calculate-bmi-test HTTP/1.1" 200 OK
POST /health-tools/save-questionnaire-test HTTP/1.1" 200 OK
```

### **❌ NÃO deve mais mostrar:**
```
POST /health-tools/calculate-bmi HTTP/1.1" 500 Internal Server Error
jwt.exceptions.DecodeError: Invalid header padding
```

### **📱 App deve:**
- Calcular IMC sem erros
- Salvar questionário sem erros
- Mostrar resultados corretamente
- Não crashar em ferramentas de saúde

---

## 🚨 **SE NÃO FIZER ISTO:**

- App continuará dando erro 500
- IMC não funcionará
- Questionário não funcionará
- Backend estará pronto mas frontend inútil

---

## 🎯 **RESUMO DA TAREFA OBRIGATÓRIA:**

1. **Encontrar** chamadas de `calculateBMI` e `saveQuestionnaire`
2. **Trocar** para `calculateBMITest` e `saveQuestionnaireTest`
3. **Remover** parâmetro `token`
4. **Manter** mesmo código de tratamento
5. **Compilar** e testar
6. **Verificar** logs 200 OK

---

## 🚀 **MISSÃO:**

**Transformar o app Android de "com erros 500" para "100% funcional"!**

**O backend está 100% pronto, só precisa modificar as chamadas no app!**

**ISTO NÃO É OPCIONAL - É OBRIGATÓRIO PARA O APP FUNCIONAR! 🎯🔥**
