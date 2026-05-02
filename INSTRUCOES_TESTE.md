# 📱 INSTRUÇÕES PARA TESTAR FUNCIONALIDADE DE SELEÇÃO DE PACIENTES

## 🎯 Objetivo
Testar a funcionalidade que permite ao profissional selecionar pacientes e visualizar seus exercícios específicos.

## 🔧 Pré-requisitos

### 1. Backend Local
```bash
cd /home/eric-de-lucas/Documentos/GitHub/PI3/back/Esc--Maya-Yoshiko-Yamamoto/Backend
docker-compose up -d
```

### 2. Verificar Backend
```bash
curl http://192.168.15.6:8080/health
# Deve retornar: {"status":"ok"}
```

## 📱 Instalação do APK

### 1. APK Compilado
O APK está pronto em:
```
/home/eric-de-lucas/Documentos/GitHub/PI3/front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/build/outputs/apk/debug/app-debug.apk
```

### 2. Instalar no Android Emulator
```bash
# Abrir Android Studio
# AVD Manager → Iniciar um emulador
# Arrastar o APK para o emulador OU
adb install app-debug.apk
```

## 🧪 Fluxo de Teste

### 1. Login
1. Abrir o app
2. Fazer login com:
   - **Email**: test@test.com
   - **Senha**: 123456

### 2. Acessar "Meus Exercícios"
1. Clicar no botão "Meus Exercícios" na tela principal
2. Aguardar carregamento

### 3. Verificar Funcionalidades

#### ✅ Deve Funcionar:
- **Título**: "Meus Exercícios" → "Exercícios: [Nome do Paciente]"
- **Botão**: "Selecionar Paciente" visível e clicável
- **Lista**: Exercícios do paciente selecionado aparecem
- **Pontos**: "🏆 ericdelucass | Pontos: 0 | Nível: Nível 1"

#### 📋 Teste de Seleção:
1. Clicar em "Selecionar Paciente"
2. **Diálogo deve aparecer** com:
   - Paciente 3 (ID: 3) - 21 exercícios
   - Paciente 5 (ID: 5) - 0 exercícios  
   - Paciente 6 (ID: 6) - 0 exercícios
3. Selecionar "Paciente 5"
4. **Título deve mudar**: "Exercícios: Paciente 5"
5. **Lista deve ficar vazia**: 0 exercícios
6. **Botão deve mudar**: "Paciente 5"

## 🔍 Logs para Debug

### Verificar Logs no Android Studio
```bash
# No Android Studio Logcat, procurar por:
NETWORK_AUDIT
EXERCISE_DEBUG
```

### Logs Esperados:
```
🌐 >>> AUDITORIA DE REDE ATIVA <<<
🌐 URL AUTH: http://192.168.15.6:8080/
🌐 Localhost - Desenvolvimento
```

## 🚨 Possíveis Problemas e Soluções

### Problema: "Nenhum paciente disponível"
**Causa**: Android não consegue acessar o backend
**Solução**:
1. Verificar se Docker está rodando: `docker-compose ps`
2. Verificar IP: `ip addr show`
3. Atualizar Constants.java com IP correto
4. Reinstalar APK

### Problema: "Erro de conexão"
**Causa**: Firewall ou rede bloqueada
**Solução**:
1. Testar conexão: `curl http://192.168.15.6:8080/health`
2. Verificar se porta 8080 está aberta
3. Reiniciar Docker: `docker-compose restart`

### Problema: "Erro 401/403"
**Causa**: Token expirado ou inválido
**Solução**:
1. Fazer logout e login novamente
2. Verificar se usuário tem role "professional"

## 📊 Resultados Esperados

### ✅ Teste Bem-Sucedido:
- ✅ Login funciona
- ✅ Lista de 3 pacientes aparece
- ✅ Diálogo de seleção funciona
- ✅ Exercícios do paciente aparecem
- ✅ Título atualiza dinamicamente
- ✅ Botão mostra nome do paciente

### 🎯 Cenários Testados:
1. **Paciente com exercícios**: Paciente 3 → 21 exercícios
2. **Paciente sem exercícios**: Paciente 5 → 0 exercícios
3. **Mudança entre pacientes**: Alternar funciona
4. **Tratamento de erros**: 404, 401 tratados

## 🎉 Conclusão

Se todos os passos funcionarem, a funcionalidade está **100% operacional** e pronta para produção!

**APK Final**: `app-debug.apk` (8.6MB)
**Backend**: `http://192.168.15.6:8080`
**Status**: ✅ Pronto para teste
