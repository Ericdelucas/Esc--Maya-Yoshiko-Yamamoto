# 🚨 CORREÇÃO URGENTE - LOGIN ANDROID - PASSO A PASSO DETALHADO

## 🎯 **OBJETIVO**
Corrigir a porta do auth-service de 8081 para 8080 no arquivo Constants.java

## 📍 **ARQUIVO PARA EDITAR**
```
front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/utils/Constants.java
```

## 🔍 **PASSO 1 - ABRIR O ARQUIVO**
1. No Android Studio, vá para o painel de arquivos (Project Explorer)
2. Navegue até: `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/utils/`
3. Clique no arquivo `Constants.java` para abri-lo

## 🔍 **PASSO 2 - LOCALIZAR A LINHA INCORRETA**
Dentro do arquivo Constants.java, encontre estas linhas:

**LINHA 8 (INCORRETA):**
```java
public static final String AUTH_BASE_URL = "http://" + HOST + ":8081/";
```

**LINHA 9 (INCORRETA):**
```java
public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";
```

## 🔧 **PASSO 3 - FAZER A CORREÇÃO EXATA**

### **MUDAR LINHA 8:**
**APAGUE completamente a linha 8:**
```java
public static final String AUTH_BASE_URL = "http://" + HOST + ":8081/";
```

**ESCREVA no lugar (linha 8 corrigida):**
```java
public static final String AUTH_BASE_URL = "http://" + HOST + ":8080/";
```

### **MUDAR LINHA 9:**
**APAGUE completamente a linha 9:**
```java
public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";
```

**ESCREVA no lugar (linha 9 corrigida):**
```java
public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";
```

## ✅ **PASSO 4 - VERIFICAR O RESULTADO**

**O arquivo deve ficar EXATAMENTE assim nas linhas 8 e 9:**
```java
public static final String AUTH_BASE_URL = "http://" + HOST + ":8080/";
public static final String EXERCISE_BASE_URL = "http://" + HOST + ":8081/";
```

## 🔍 **PASSO 5 - VERIFICAR OUTRAS LINHAS (NÃO MEXER!)**
As outras linhas devem permanecer IGUAIS:

**LINHA 10 (NÃO MEXER):**
```java
public static final String HEALTH_BASE_URL = "http://" + HOST + ":8071/";
```

**LINHA 11 (NÃO MEXER):**
```java
public static final String TRAINING_BASE_URL = "http://" + HOST + ":8030/";
```

**LINHA 12 (NÃO MEXER):**
```java
public static final String AI_HTTP_URL = "http://" + HOST + ":8090/ai/process-frame";
```

## 🚀 **PASSO 6 - COMPILAR O APP**

1. No Android Studio, vá em `Build` → `Clean Project`
2. Espere limpar completamente
3. Vá em `Build` → `Rebuild Project`
4. Espere terminar de compilar
5. Instale o app no celular/emulador

## 🧪 **PASSO 7 - TESTAR O LOGIN**

Use estas credenciais para testar:

**USUÁRIO ADMIN:**
- Email: `novo.admin@smartsaude.com`
- Senha: `admin123`

**USUÁRIO PROFISSIONAL:**
- Email: `dr.silva@smartsaude.com`
- Senha: `prof123`

**USUÁRIO PACIENTE:**
- Email: `joao.paciente@smartsaude.com`
- Senha: `pac123`

## ⚠️ **RESUMO DA MUDANÇA**

**ANTES (ERRADO):**
- AUTH_BASE_URL = `http://localhost:8081/` ❌

**DEPOIS (CORRETO):**
- AUTH_BASE_URL = `http://localhost:8080/` ✅

## 📋 **CHECKLIST FINAL**

- [ ] Abriu o arquivo Constants.java
- [ ] Mudou linha 8 de 8081 para 8080
- [ ] Linha 9 permanece 8081 (exercise service)
- [ ] Linhas 10, 11, 12 não foram alteradas
- [ ] Fez Clean Project
- [ ] Fez Rebuild Project
- [ ] Instalou o app
- [ ] Testou login com as credenciais acima

## 🚨 **SE NÃO FUNCIONAR**

1. Verifique se mudou APENAS a linha 8
2. Verifique se não tem espaços extras
3. Verifique se fez Clean + Rebuild
4. Verifique se o backend está rodando (docker compose up)

---

**IMPORTANTE:** Mude APENAS o número 8081 para 8080 na linha AUTH_BASE_URL! NADA MAIS!
