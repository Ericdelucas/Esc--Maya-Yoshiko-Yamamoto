# 🚨 PROBLEMA IDENTIFICADO - PORTA INCORRETA NO CALENDÁRIO

## ❌ **ERRO CRÍTICO ENCONTRADO**

### **O problema está na configuração de portas:**

#### **CalendarActivity.java:**
```java
// ❌ Usando getAppointmentClient() que aponta para porta 8085
AppointmentApi api = ApiClient.getAppointmentClient().create(AppointmentApi.class);
```

#### **ApiClient.java:**
```java
public static Retrofit getAppointmentClient() {
    if (appointmentRetrofit == null) {
        appointmentRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.PACIENTES_BASE_URL)  // ❌ PORTA 8085!
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build();
    }
    return appointmentRetrofit;
}
```

#### **Constants.java:**
```java
// ❌ PACIENTES SERVICE (Porta 8085) - ERRADO!
public static final String PACIENTES_BASE_URL = "http://" + HOST + ":8085/";
```

## 🎯 **DIAGNÓSTICO DEFINITIVO**

### **O que está acontecendo:**
```
✅ Login: AUTH_BASE_URL = http://10.0.2.2:8080/  (CORRETO)
❌ Calendário: PACIENTES_BASE_URL = http://10.0.2.2:8085/  (ERRADO!)
```

### **Backend real:**
```
✅ auth-service: Porta 8080 (funcionando)
✅ appointments: Porta 8080 (mesmo serviço do auth!)
❌ Não existe serviço na porta 8085!
```

## 🔧 **SOLUÇÃO IMEDIATA**

### **Opção 1: Corrigir CalendarActivity.java (RECOMENDADO)**
```java
// ❌ ANTES:
AppointmentApi api = ApiClient.getAppointmentClient().create(AppointmentApi.class);

// ✅ DEPOIS:
AppointmentApi api = ApiClient.getAuthClient().create(AppointmentApi.class);
```

### **Opção 2: Criar Constants.APPOINTMENTS_BASE_URL**
```java
// Em Constants.java:
public static final String APPOINTMENTS_BASE_URL = "http://" + HOST + ":8080/";

// Em ApiClient.java:
public static Retrofit getAppointmentClient() {
    if (appointmentRetrofit == null) {
        appointmentRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.APPOINTMENTS_BASE_URL)  // ✅ PORTA 8080
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build();
    }
    return appointmentRetrofit;
}
```

## 🧪 **TESTE DE CONFIRMAÇÃO**

### **Verificar se porta 8085 existe:**
```bash
curl http://localhost:8085/health
# Resultado esperado: Connection refused (não existe)
```

### **Verificar se appointments funciona na 8080:**
```bash
curl http://localhost:8080/appointments/month/2026/4
# Resultado esperado: 403 Forbidden (precisa auth, mas endpoint existe)
```

## 📝 **INSTRUÇÕES PARA GEMINI**

### **Correção simples e imediata:**

#### **1. CalendarActivity.java - Linha 90:**
```java
// ❌ Substituir:
AppointmentApi api = ApiClient.getAppointmentClient().create(AppointmentApi.class);

// ✅ Por:
AppointmentApi api = ApiClient.getAuthClient().create(AppointmentApi.class);
```

#### **2. CalendarActivity.java - Linha 155:**
```java
// ❌ Substituir:
AppointmentApi api = ApiClient.getAppointmentClient().create(AppointmentApi.class);

// ✅ Por:
AppointmentApi api = ApiClient.getAuthClient().create(AppointmentApi.class);
```

## 🎯 **RESULTADO ESPERADO**

### **Antes (errado):**
```
❌ CalendarActivity → getAppointmentClient() → Porta 8085 → Connection refused
❌ "Erro de conexão com o servidor (Porta 8085)"
```

### **Depois (corrigido):**
```
✅ CalendarActivity → getAuthClient() → Porta 8080 → Funciona!
✅ Agendamentos carregados do banco
```

## 📊 **RESUMO**

```
✅ Login: Funciona (porta 8080)
✅ Backend: Rodando (porta 8080)
✅ Appointments: Existem (porta 8080)
❌ CalendarActivity: Tentando porta 8085 (não existe)
```

**É só mudar getAppointmentClient() para getAuthClient() no CalendarActivity!**

---

**Status:** 🚨 **PROBLEMA IDENTIFICADO - PORTA ERRADA 8085 → 8080**
