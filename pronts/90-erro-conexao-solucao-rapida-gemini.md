# # **ERRO DE CONEXÃO - SOLUÇÃO RÁPIDA - GEMINI**

## # **PROBLEMA: "Erro de conexão de servidor"**

### # **DIAGNÓSTICO IMEDIATO:**
- # **Backend:** 100% funcional (testado com curl)
- # **adb reverse:** Foi perdido e precisa ser reconfigurado
- # **Causa:** Desconexão do dispositivo ou reinicialização

## # **SOLUÇÃO RÁPIDA (2 minutos):**

### # **PASSO 1: Reconfigurar adb reverse**
```bash
# # No terminal do backend:
adb reverse tcp:8080 tcp:8080
adb reverse tcp:8081 tcp:8081

# # Verificar se funcionou:
adb reverse --list
# # Deve mostrar:
# # UsbFfs tcp:8080 tcp:8080
# # UsbFfs tcp:8081 tcp:8081
```

### # **PASSO 2: Verificar dispositivo conectado**
```bash
# # Verificar se dispositivo está conectado:
adb devices

# # Se não aparecer:
# # 1. Reiniciar o ADB:
adb kill-server
adb start-server

# # 2. Reconectar dispositivo USB
# # 3. Reativar depuração USB no dispositivo
```

### # **PASSO 3: Testar conexão do emulador**
```bash
# # Testar se emulador está conectando:
adb shell curl -v http://127.0.0.1:8080/health

# # Se funcionar, problema está resolvido
```

### # **PASSO 4: Se ainda não funcionar, tentar IP alternativo**
```java
// # Em Constants.java, mudar temporariamente:
public static final String HOST = "10.0.2.2"; // # Emulador padrão

// # Ou se tiver IP local:
public static final String HOST = "192.168.1.XXX"; // # Seu IP
```

## # **SOLUÇÃO DEFINITIVA:**

### # **Criar script automático:**
```bash
# # Criar arquivo setup_connection.sh:
#!/bin/bash
echo "Configurando conexão Android..."

# # Verificar dispositivo
adb devices

# # Configurar portas
adb reverse tcp:8080 tcp:8080
adb reverse tcp:8081 tcp:8081

# # Verificar configuração
echo "Portas configuradas:"
adb reverse --list

# # Testar conexão
echo "Testando conexão..."
adb shell curl -s http://127.0.0.1:8080/health

echo "Pronto! Tente o app novamente."
```

### # **Adicionar verificação no app:**
```java
// # Em qualquer Activity principal, adicionar:
private void checkConnection() {
    new Thread(() -> {
        try {
            URL url = new URL(Constants.AUTH_BASE_URL + "health");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            
            int response = conn.getResponseCode();
            runOnUiThread(() -> {
                if (response == 200) {
                    Log.d("CONNECTION_CHECK", "OK - Backend conectado");
                } else {
                    Log.e("CONNECTION_CHECK", "ERRO - Code: " + response);
                    Toast.makeText(this, "Execute 'adb reverse tcp:8080 tcp:8080'", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> {
                Log.e("CONNECTION_CHECK", "ERRO: " + e.getMessage());
                Toast.makeText(this, "Erro de conexão - verifique adb reverse", Toast.LENGTH_LONG).show();
            });
        }
    }).start();
}

// # Chamar no onCreate():
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // ... código existente ...
    checkConnection();
}
```

## # **DIAGNÓSTICO AVANÇADO:**

### # **Se nada funcionar:**
1. # **Reiniciar emulador**
2. # **Limpar dados do app**
3. # **Reinstalar APK**
4. # **Verificar firewall** do computador
5. # **Testar com outro emulador**

### # **Logs para debug:**
```bash
# # Verificar logs de rede:
adb logcat -s "NETWORK_AUDIT" "NETWORK_DEBUG" "OkHttp"

# # Verificar erros de conexão:
adb logcat -s "AndroidRuntime"
```

## # **RESULTADO ESPERADO:**

### # **Após configuração:**
- # **adb reverse ativo** para portas 8080 e 8081
- # **App conectando** normalmente
- # **Login funcionando**
- # **Upload de exercícios funcionando**

---

## # **PARA O GEMINI - AÇÕES IMEDIATAS:**

**1. Execute os comandos adb reverse primeiro**
**2. Verifique se o dispositivo está conectado**
**3. Teste o app novamente**
**4. Se ainda falhar, mude HOST para "10.0.2.2"**
**5. Use o script automático para evitar problemas futuros**

**Na maioria das vezes, só precisa reexecutar os comandos adb reverse!**
