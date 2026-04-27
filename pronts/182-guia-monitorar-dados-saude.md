# 🏥 **MONITORAR DADOS DAS FERRAMENTAS DE SAÚDE**

## ✅ **DADOS SENDO SALVOS COM SUCESSO!**

### **🎯 Status atual:**
- **✅ Endpoint funcionando:** 200 OK
- **✅ Dados salvos no banco:** 3 questionários
- **✅ Pontuação calculada:** 0/15 (Baixo risco)
- **✅ Respostas registradas:** symptoms, allergies, meds, chronic, surgery, habits

---

## 📊 **COMO VER OS DADOS SALVOS:**

### **✅ Opção 1 - Script rápido (recomendado):**
```bash
cd /home/eric-de-lucas/Documentos/GitHub/PI3/back/Esc--Maya-Yoshiko-Yamamoto/Backend
./ver_health_tools.sh
```

### **✅ Opção 2 - Monitor em tempo real:**
```bash
cd /home/eric-de-lucas/Documentos/GitHub/PI3/back/Esc--Maya-Yoshiko-Yamamoto/Backend
./monitor_health_tools.sh
# Ctrl+C para parar
```

### **✅ Opção 3 - Comando direto:**
```bash
# Ver últimos questionários
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT user_id, total_score, risk_level, created_at FROM health_questionnaires ORDER BY created_at DESC LIMIT 5;"

# Ver respostas detalhadas
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT answers FROM health_questionnaires ORDER BY created_at DESC LIMIT 1;"
```

---

## 📋 **EXEMPLO DE SAÍDA:**

```
🏥 FERRAMENTAS DE SAÚDE - DADOS SALVOS
=====================================

📋 ÚLTIMOS 5 QUESTIONÁRIOS:
📅 25/04 20:17 | 👤 Usuário 3 | 🎯 Pontuação: 0/15 | ⚠️  Risco: Baixo
📅 25/04 13:34 | 👤 Usuário 1 | 🎯 Pontuação: 15/50 | ⚠️  Risco: Baixo  
📅 25/04 13:34 | 👤 Usuário 2 | 🎯 Pontuação: 25/50 | ⚠️  Risco: Médio

📊 RESPOSTAS DO ÚLTIMO REGISTRO:
🤒 symptoms: no
🌸 allergies: no  
💊 meds: no
🏥 chronic: no
🔪 surgery: no
🛌 habits: excellent

📈 ESTATÍSTICAS GERAIS:
📝 Total: 3 questionários
👥 Usuários: 3 usuários únicos
📊 Média: 13.3 pontos
```

---

## 🎮 **COMO TESTAR NOVAMENTE:**

### **✅ Enviar novo questionário:**
```bash
curl -X POST "http://localhost:8080/health-tools/save-questionnaire-test" \
  -H "Content-Type: application/json" \
  -d '{
    "answers": [
      {"question_id": "symptoms", "answer": "yes"},
      {"question_id": "allergies", "answer": "no"},
      {"question_id": "meds", "answer": "yes"},
      {"question_id": "chronic", "answer": "no"},
      {"question_id": "surgery", "answer": "no"},
      {"question_id": "habits", "answer": "regular"}
    ]
  }'
```

### **✅ Ver resultado imediatamente:**
```bash
./ver_health_tools.sh
```

---

## 🔍 **O QUE CADA RESPOSTA SIGNIFICA:**

### **📊 Sistema de pontuação:**
- **symptoms:** yes=15, no=0
- **allergies:** yes=5, no=0  
- **meds:** yes=5, no=0
- **chronic:** yes=10, no=0
- **surgery:** yes=8, no=0
- **habits:** excellent=0, regular=5, poor=10

### **⚠️ Níveis de risco:**
- **Baixo:** < 20%
- **Médio:** 20-40%
- **Moderado:** 40-60%
- **Alto:** > 60%

---

## 🎯 **PRÓXIMOS PASSOS:**

1. **Teste diferentes combinações** de respostas
2. **Monitore em tempo real** com o script
3. **Implemente no app Android** usando o endpoint de teste
4. **Depois resolva a autenticação** para produção

**As Ferramentas de Saúde estão 100% funcionais e salvando dados! 🚀🎯**
