# 📋 Relatório de Configuração de Portas - Backend vs Frontend

## 🎯 Objetivo
Este documento compara as configurações de portas entre o backend (atualmente rodando) e o frontend Android, identificando as diferenças que precisam ser ajustadas.

## 📊 Tabela Comparativa

| Serviço / Módulo | Porta Backend (Atual) | Porta Frontend (Android) | Status | URL Backend | URL Frontend |
|:---|:---:|:---:|:---:|:---|:---|
| **Autenticação (Auth)** | **8085** | 8080 | ⚠️ **DIFERENTE** | http://localhost:8085 | http://10.0.2.2:8080 |
| **Exercícios (Exercise)** | **8082** | 8081 | ⚠️ **DIFERENTE** | http://localhost:8082 | http://10.0.2.2:8081 |
| **Treinamento (Training)** | **8030** | 8030 | ✅ **IGUAL** | http://localhost:8030 | http://10.0.2.2:8030 |
| **Análise (Analytics)** | **8050** | 8050 | ✅ **IGUAL** | http://localhost:8050 | http://10.0.2.2:8050 |
| **Prontuário Eletrônico (EHR)** | **8061** | 8060 | ⚠️ **DIFERENTE** | http://localhost:8061 | http://10.0.2.2:8060 |
| **Notificações** | **8070** | 8070 | ✅ **IGUAL** | http://localhost:8070 | http://10.0.2.2:8070 |
| **Inteligência Artificial (AI)** | **8090** | 8090 | ✅ **IGUAL** | http://localhost:8090 | http://10.0.2.2:8090 |

## 🔧 Ações Necessárias

### 📱 Arquivos Android para Alterar

#### 1. `Constants.java`
**Localização:** `app/src/main/java/com/seuprojeto/util/Constants.java`

**Portas que precisam ser atualizadas:**
```java
// ANTES (atual)
public static final String AUTH_BASE_URL = "http://10.0.2.2:8080/";
public static final String EXERCISE_BASE_URL = "http://10.0.2.2:8081/";
public static final String EHR_BASE_URL = "http://10.0.2.2:8060/";

// DEPOIS (corrigido)
public static final String AUTH_BASE_URL = "http://10.0.2.2:8085/";
public static final String EXERCISE_BASE_URL = "http://10.0.2.2:8082/";
public static final String EHR_BASE_URL = "http://10.0.2.2:8061/";
```

#### 2. `ApiClient.java`
**Localização:** `app/src/main/java/com/seuprojeto/network/ApiClient.java`

**Verificar se há configurações de portas hardcoded:**
- Buscar por URLs que contenham as portas 8080, 8081, 8060
- Substituir pelas portas corretas: 8085, 8082, 8061

## 🚨 Serviços com Problemas

### ❌ Serviços que NÃO estão funcionando no backend:
- **AI Service**: Inicia mas para inesperadamente
  - **Porta:** 8090
  - **Status:** Instável, precisa de investigação

### ✅ Serviços funcionando perfeitamente:
- Auth Service (8085)
- Notification Service (8070)  
- Analytics Service (8050)
- Exercise Service (8082)
- Training Service (8030)
- EHR Service (8061)

## 🔍 Resumo das Mudanças Necessárias

### No Android (Frontend):
1. **Auth Service:** 8080 → **8085**
2. **Exercise Service:** 8081 → **8082**  
3. **EHR Service:** 8060 → **8061**

### No Backend (Opcional):
Se preferir manter as portas originais do Android, pode reverter o backend:
1. **Auth Service:** 8085 → **8080** (conflito detectado anteriormente)
2. **Exercise Service:** 8082 → **8081**
3. **EHR Service:** 8061 → **8060**

## ⚡ Recomendação

**Manter as portas atuais do backend** e atualizar o Android, pois:
- O backend já está configurado e funcionando
- Menos risco de quebrar serviços que já funcionam
- Mudança mais simples e controlada

## 📝 Checklist de Validação

- [ ] Atualizar `Constants.java` com as novas portas
- [ ] Verificar `ApiClient.java` por URLs hardcoded
- [ ] Testar conexão com cada serviço atualizado
- [ ] Verificar se o AI Service está estável
- [ ] Testar fluxo completo de autenticação
- [ ] Testar integração entre serviços

---

**Última atualização:** 11/03/2026  
**Status:** Backend rodando, frontend precisa de atualização de portas
