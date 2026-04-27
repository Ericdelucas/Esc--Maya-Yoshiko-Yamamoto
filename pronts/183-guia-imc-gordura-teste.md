# 🏥 **GUIA - TESTE DE IMC E GORDURA CORPORAL**

## ✅ **STATUS ATUAL:**

### **❌ Problemas identificados:**
- **Endpoints de IMC/gordura corporal** com autenticação dando erro 500 (token JWT corrompido)
- **Endpoint `/metrics/imc`** não existe (404)
- **Apenas endpoint de questionário-teste** funcionando

### **✅ Soluções disponíveis:**
- **Endpoint `/save-questionnaire-test`** funcionando 100%
- **Banco de dados recebendo dados**
- **Cálculos funcionando no backend**

---

## 🛠️ **SOLUÇÃO 1 - USAR ENDPOINTS DE TESTE:**

### **✅ Endpoint de questionário (funcionando):**
```bash
curl -s -X POST "http://localhost:8080/health-tools/save-questionnaire-test" \
  -H "Content-Type: application/json" \
  -d '{
    "answers": [
      {"question_id": "symptoms", "answer": "no"},
      {"question_id": "allergies", "answer": "no"},
      {"question_id": "meds", "answer": "no"},
      {"question_id": "chronic", "answer": "no"},
      {"question_id": "surgery", "answer": "no"},
      {"question_id": "habits", "answer": "excellent"}
    ]
  }' | jq .
```

### **❌ Endpoints de IMC/gordura (precisam ser criados):**
Os endpoints `/calculate-bmi-test` e `/calculate-body-fat-test` estão com problemas técnicos.

---

## 🛠️ **SOLUÇÃO 2 - CÁLCULO MANUAL:**

### **✅ Calcular IMC manualmente:**
```bash
# Fórmula: IMC = peso / (altura²)
# Exemplo: 36.0 kg / (1.9m × 1.9m) = 9.97

echo "IMC: $(echo "scale=2; 36.0 / (1.9 * 1.9)" | bc)"
```

### **✅ Calcular gordura corporal (fórmula simplificada):**
```bash
# Fórmula de Deurenberg (simplificada)
# %Gordura = 1.20 × IMC + 0.23 × idade - 10.8 × sexo - 5.4
# sexo: M=1, F=0

# Exemplo para homem, 30 anos, IMC 9.97
echo "Gordura: $(echo "scale=2; 1.20 * 9.97 + 0.23 * 30 - 10.8 * 1 - 5.4" | bc)%"
```

---

## 🛠️ **SOLUÇÃO 3 - MODIFICAR APP ANDROID:**

### **✅ No HealthToolsApi.java:**
```java
// Adicionar apenas o método que funciona
@POST("health-tools/save-questionnaire-test")
Call<QuestionnaireResponse> saveQuestionnaireTest(@Body QuestionnaireRequest request);
```

### **✅ No app Android:**
- **Desabilitar temporariamente** cálculos de IMC/gordura
- **Focar apenas no questionário** que está funcionando
- **Mostrar mensagem** "Cálculo de IMC em manutenção"

---

## 📊 **VERIFICAR DADOS SALVOS:**

### **✅ Ver questionários no banco:**
```bash
cd /home/eric-de-lucas/Documentos/GitHub/PI3/back/Esc--Maya-Yoshiko-Yamamoto/Backend
./ver_health_tools.sh
```

### **✅ Monitor em tempo real:**
```bash
cd Backend
./monitor_health_tools.sh
```

---

## 🎯 **RECOMENDAÇÃO IMEDIATA:**

1. **Use o questionário** que está 100% funcional
2. **Desabilite IMC/gordura** temporariamente no app
3. **Mostre os dados salvos** com os scripts criados
4. **Depois resolva a autenticação** para os outros endpoints

---

## 🚨 **SE PRECISAR DOS ENDPOINTS IMEDIATAMENTE:**

### **✅ Criar endpoints simples:**
```python
# No health_tools_router.py (após o save-questionnaire-test)

@router.post("/simple-bmi")
def simple_bmi(height: float, weight: float):
    bmi = weight / (height ** 2)
    return {
        "success": True,
        "bmi": round(bmi, 2),
        "category": "Normal" if bmi < 25 else "Sobrepeso"
    }
```

**O questionário está funcionando perfeitamente! Focar nele por enquanto. 🎯📋**
