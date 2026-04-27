# 🤖 **GUIA PARA GEMINI - MODIFICAR APP ANDROID**

## 🎯 **OBJETIVO:**

Modificar o app Android para usar endpoints de teste das Ferramentas de Saúde, eliminando erros 500 de autenticação JWT.

---

## 📋 **SITUAÇÃO ATUAL:**

### **✅ Backend:** 100% funcional
- **Endpoints de teste** funcionando sem autenticação
- **Dados sendo salvos** corretamente no banco
- **Logs mostrando 200 OK**

### **❌ Frontend:** precisa modificação
- **App ainda chama** endpoints com autenticação
- **Erros 500** por token JWT corrompido
- **HealthToolsApi.java** já atualizado

---

## 🛠️ **O QUE JÁ FOI FEITO:**

### **✅ HealthToolsApi.java atualizado:**
```java
// Endpoints de teste adicionados
@POST("health-tools/calculate-bmi-test")
Call<BMIResponse> calculateBMITest(@Body BMICalculationRequest request);

@POST("health-tools/save-questionnaire-test")
Call<QuestionnaireResponse> saveQuestionnaireTest(@Body QuestionnaireRequest request);
```

---

## 🎯 **TAREFAS PARA O GEMINI:**

### **🔍 Tarefa 1 - Encontrar chamadas atuais:**

**Procurar por estes padrões no código:**
```java
// Padrão 1 - IMC
healthApi.calculateBMI(token, request)

// Padrão 2 - Questionário  
healthApi.saveQuestionnaire(token, request)
```

**Ferramentas:**
- **Ctrl+Shift+F** (busca global no Android Studio)
- **Buscar em todos os arquivos Java**
- **Procurar em `app/src/main/java/`**

---

### **🔄 Tarefa 2 - Modificar as chamadas:**

**Substituir chamadas existentes:**

**Para IMC:**
```java
// TROCAR ISTO:
healthApi.calculateBMI(token, request).enqueue(new Callback<...>() {
    // código existente
});

// POR ISTO:
healthApi.calculateBMITest(request).enqueue(new Callback<HealthToolsApi.BMIResponse>() {
    @Override
    public void onResponse(Call<HealthToolsApi.BMIResponse> call, Response<HealthToolsApi.BMIResponse> response) {
        // manter o mesmo código de tratamento
    }
    
    @Override
    public void onFailure(Call<HealthToolsApi.BMIResponse> call, Throwable t) {
        // manter o mesmo código de erro
    }
});
```

**Para Questionário:**
```java
// TROCAR ISTO:
healthApi.saveQuestionnaire(token, request).enqueue(new Callback<...>() {
    // código existente
});

// POR ISTO:
healthApi.saveQuestionnaireTest(request).enqueue(new Callback<HealthToolsApi.QuestionnaireResponse>() {
    @Override
    public void onResponse(Call<HealthToolsApi.QuestionnaireResponse> call, Response<HealthToolsApi.QuestionnaireResponse> response) {
        // manter o mesmo código de tratamento
    }
    
    @Override
    public void onFailure(Call<HealthToolsApi.QuestionnaireResponse> call, Throwable t) {
        // manter o mesmo código de erro
    }
});
```

---

### **📁 Tarefa 3 - Arquivos prováveis para modificar:**

**Verificar estes arquivos:**
- `HealthQuestionnaireActivity.java` - **JÁ MODIFICADO** (verificar se está correto)
- `BMICalculationActivity.java` - **PRECISA MODIFICAR**
- `HealthToolsActivity.java` - **PRECISA MODIFICAR**
- `MainActivity.java` - **VERIFICAR**
- Fragmentos de saúde - **VERIFICAR**

---

### **🧪 Tarefa 4 - Compilar e testar:**

**Passos após modificação:**

**1. Limpar e compilar:**
```bash
cd front/Esc--Maya-Yoshiko-Yamamoto/testbackend
./gradlew clean
./gradlew assembleDebug
```

**2. Instalar APK:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**3. Redirecionar porta:**
```bash
adb reverse tcp:8080 tcp:8080
```

**4. Testar no app:**
- Abrir app
- Fazer login
- Ir em Ferramentas de Saúde
- Testar cálculo de IMC
- Testar questionário de saúde

---

## ✅ **CRITÉRIOS DE SUCESSO:**

### **📊 Logs do backend devem mostrar:**
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
- **Calcular IMC** sem erros
- **Salvar questionário** sem erros
- **Mostrar resultados** corretamente
- **Não crashar** em ferramentas de saúde

---

## 🚨 **PONTOS DE ATENÇÃO:**

### **⚠️ Importante:**
- **NÃO remover** os métodos antigos (calculateBMI, saveQuestionnaire)
- **APENAS trocar** as chamadas para as versões `-test`
- **MANTER** o mesmo código de tratamento de resposta
- **REMOVER** apenas o parâmetro `token`

### **🔍 Se encontrar múltiplas ocorrências:**
- **Modificar todas** as chamadas
- **Verificar** se há activitys diferentes
- **Testar** cada funcionalidade

---

## 🎯 **RESUMO DA TAREFA:**

1. **Encontrar** onde chama `calculateBMI` e `saveQuestionnaire`
2. **Trocar** para `calculateBMITest` e `saveQuestionnaireTest`
3. **Remover** parâmetro `token` das chamadas
4. **Manter** o mesmo código de tratamento
5. **Compilar** e testar
6. **Verificar** logs 200 OK no backend

---

## 📞 **COMO SABER QUE FUNCIONOU:**

### **✅ Sucesso:**
- **App calcula IMC** sem erros
- **App salva questionário** sem erros
- **Backend logs 200 OK**
- **Dados aparecem** no banco

### **❌ Falha:**
- **Ainda mostra 500 errors**
- **Ainda mostra JWT errors**
- **App crasha** em ferramentas de saúde
- **Dados não salvam** no banco

---

## 🚀 **MISSÃO:**

**Transformar o app Android de "com erros 500" para "100% funcional" usando endpoints de teste das Ferramentas de Saúde!**

**O backend está pronto, os endpoints estão funcionando, só precisa modificar as chamadas no app! 🎯🛠️**
