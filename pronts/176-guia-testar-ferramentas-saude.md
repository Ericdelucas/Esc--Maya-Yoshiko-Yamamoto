# 🎯 **GUIA PARA TESTAR FERRAMENTAS DE SAÚDE**

## ✅ **SISTEMA PRONTO PARA TESTE!**

### **🔥 Backend funcionando:**
- **8 endpoints API** disponíveis
- **Banco de dados** com dados de exemplo
- **Frontend atualizado** com IDs corretos

---

## 📋 **COMO TESTAR:**

### **✅ Passo 1 - Compilar o App:**
```bash
cd front/Esc--Maya-Yoshiko-Yamamoto/testbackend
./gradlew assembleDebug
```

### **✅ Passo 2 - Instalar no Celular:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### **✅ Passo 3 - Fazer Login no App:**
1. **Abrir o app**
2. **Fazer login** com usuário e senha
3. **Navegar** para "Ferramentas de Saúde"

---

## 🎮 **TESTAR QUESTIONÁRIO:**

### **✅ Passo 4 - Responder Questionário:**
1. **Acessar** aba "Questionário de Saúde"
2. **Responder** as perguntas:
   - Febre/Sintomas: Sim/Não
   - Alergias: Sim/Não (descrever se sim)
   - Medicamentos: Sim/Não
   - Doenças Crônicas: Sim/Não
   - Cirurgias: Sim/Não
   - Hábitos: Excelente/Regular/Precisa melhorar
3. **Clicar** em "Salvar Respostas"

### **✅ Resultado Esperado:**
- **Toast** com sucesso: "Questionário salvo com sucesso! Pontuação: X - Risco: Y"
- **Formulário** limpo automaticamente
- **Dados salvos** no banco

---

## 🗄️ **VERIFICAR NO BANCO:**

### **✅ Verificar dados salvos:**
```bash
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT user_id, total_score, risk_level, created_at FROM health_questionnaires ORDER BY created_at DESC LIMIT 5;"
```

### **✅ Verificar respostas detalhadas:**
```bash
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT answers FROM health_questionnaires ORDER BY created_at DESC LIMIT 1;"
```

---

## 🛠️ **FUNCIONALIDADES IMPLEMENTADAS:**

### **✅ Questionário:**
- **6 perguntas** de saúde relevantes
- **Validação** de respostas
- **Cálculo automático** de pontuação (0-43 pontos)
- **Classificação de risco**: Baixo (0-15), Médio (16-30), Alto (31+)
- **Salvamento** automático no banco
- **Feedback** visual para usuário

### **✅ Pontuação:**
- **Febre/Sintomas**: 15 pontos (se sim)
- **Alergias**: 5 pontos (se sim)
- **Medicamentos**: 5 pontos (se sim)
- **Doenças Crônicas**: 10 pontos (se sim)
- **Cirurgias**: 8 pontos (se sim)
- **Hábitos**: 0 (Excelente), 5 (Regular), 10 (Precisa melhorar)

---

## 🚨 **SE DER ERRO:**

### **❌ Se não salvar:**
1. **Verificar** conexão com internet
2. **Verificar** se está logado
3. **Verificar** logs no Android Studio
4. **Verificar** logs do backend:
   ```bash
   docker compose logs auth-service --tail 20
   ```

### **❌ Se não encontrar a tela:**
1. **Verificar** se o app foi compilado com as mudanças
2. **Limpar cache** do app
3. **Reinstalar** o app

---

## 🎯 **PRÓXIMOS PASSOS:**

### **✅ Após testar questionário:**
1. **Testar** calculadora de IMC
2. **Testar** calculadora de gordura corporal
3. **Verificar** histórico completo
4. **Testar** resumo de saúde

**O sistema está pronto para uso! Basta compilar e testar! 🎯**
