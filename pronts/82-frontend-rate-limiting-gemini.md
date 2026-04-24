# # **IMPLEMENTAR RATE LIMITING NO FRONTEND - GEMINI**

## # **O QUE ESTÁ PRONTO NO BACKEND:**

### # **Rate Limiting 100% Funcional:**
- # **10 tentativas máximas** por email
- # **Bloqueio de 5 minutos** após exceder
- # **Mensagens claras:** "Restam X tentativas"
- # **Endpoint status:** `/auth/login-status/{email}`

### # **Respostas da API:**

#### # **Tentativas Normais (HTTP 401):**
```json
{
  "detail": {
    "error": "invalid_credentials",
    "message": "Credenciais inválidas. Restam 7 tentativas.",
    "attempts_remaining": 7,
    "max_attempts": 10
  }
}
```

#### # **Usuário Bloqueado (HTTP 429):**
```json
{
  "detail": {
    "error": "too_many_attempts",
    "message": "Muitas tentativas. Tente novamente em 5 minutos.",
    "retry_after": 300
  }
}
```

#### # **Status do Usuário:**
```bash
GET /auth/login-status/user@email.com
# # Retorna:
{
  "identifier": "user@email.com",
  "blocked": true,
  "attempts_used": 10,
  "attempts_remaining": 0,
  "max_attempts": 10,
  "block_until": "2026-04-22T03:21:26.397358"
}
```

## # **O QUE GEMINI PRECISA IMPLEMENTAR:**

### # **1. Atualizar LoginActivity.java:**

```java
@Override
public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
    if (response.isSuccessful()) {
        // Login sucesso - ir para MainActivity
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
        return;
    }
    
    // Tratar erros 401 e 429
    try {
        String errorBody = response.errorBody().string();
        JSONObject error = new JSONObject(errorBody);
        JSONObject detail = error.getJSONObject("detail");
        
        if (response.code() == 401) {
            // Tentativas restantes
            String message = detail.getString("message");
            int attemptsRemaining = detail.optInt("attempts_remaining", -1);
            
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            
            // Alerta se restarem poucas tentativas
            if (attemptsRemaining >= 0 && attemptsRemaining <= 2) {
                showWarningDialog(attemptsRemaining);
            }
            
        } else if (response.code() == 429) {
            // Usuário bloqueado
            String message = detail.getString("message");
            int retryAfter = detail.optInt("retry_after", 300);
            
            showBlockedDialog(message, retryAfter);
        }
        
    } catch (Exception e) {
        Toast.makeText(this, "Erro ao fazer login", Toast.LENGTH_SHORT).show();
    }
}

private void showWarningDialog(int attemptsRemaining) {
    new AlertDialog.Builder(this)
        .setTitle("Atenção")
        .setMessage("Restam apenas " + attemptsRemaining + " tentativas antes do bloqueio!")
        .setPositiveButton("OK", null)
        .show();
}

private void showBlockedDialog(String message, int retryAfter) {
    new AlertDialog.Builder(this)
        .setTitle("Conta Bloqueada")
        .setMessage(message)
        .setPositiveButton("OK", null)
        .setCancelable(false)
        .show();
    
    // Desabilitar botão e mostrar countdown
    btnLogin.setEnabled(false);
    startCountdownTimer(retryAfter);
}

private void startCountdownTimer(int seconds) {
    new CountDownTimer(seconds * 1000, 1000) {
        public void onTick(long millisUntilFinished) {
            int minutes = (int) (millisUntilFinished / 60000);
            int seconds = (int) (millisUntilFinished % 60000) / 1000;
            btnLogin.setText(String.format("Aguarde %d:%02d", minutes, seconds));
        }
        
        public void onFinish() {
            btnLogin.setEnabled(true);
            btnLogin.setText("Entrar");
        }
    }.start();
}
```

### # **2. Opcional: Verificar Status Antes do Login:**

```java
private void checkLoginStatus(String email) {
    // Antes de tentar login, verificar se usuário está bloqueado
    authApi.getLoginStatus(email).enqueue(new Callback<LoginStatus>() {
        @Override
        public void onResponse(Call<LoginStatus> call, Response<LoginStatus> response) {
            if (response.isSuccessful() && response.body().isBlocked()) {
                // Usuário bloqueado, mostrar countdown
                int retryAfter = calculateRetryAfter(response.body().getBlockUntil());
                showBlockedDialog("Aguarde antes de tentar novamente", retryAfter);
                return;
            }
            // Continuar com tentativa de login normal
            attemptLogin();
        }
        
        @Override
        public void onFailure(Call<LoginStatus> call, Throwable t) {
            // Se API falhar, continuar com login normal
            attemptLogin();
        }
    });
}
```

### # **3. Models Necessários:**

```java
// LoginStatus.java
public class LoginStatus {
    private String identifier;
    private boolean blocked;
    private int attemptsUsed;
    private int attemptsRemaining;
    private int maxAttempts;
    private String blockUntil;
    
    // Getters e Setters
    public boolean isBlocked() { return blocked; }
    public String getBlockUntil() { return blockUntil; }
    // ... outros getters
}
```

```java
// AuthApi.java - adicionar endpoint
@GET("auth/login-status/{email}")
Call<LoginStatus> getLoginStatus(@Path("email") String email);
```

## # **TESTES PARA GEMINI FAZER:**

### # **1. Testar Tentativas Normais:**
1. # Fazer login com senha errada
2. # Verificar mensagem: "Restam 9 tentativas"
3. # Repetir até ver "Restam 1 tentativa"
4. # Verificar alerta quando restar 2 tentativas

### # **2. Testar Bloqueio:**
1. # Fazer 10 tentativas erradas
2. # 11ª tentativa deve mostrar "Muitas tentativas. Tente novamente em 5 minutos"
3. # Botão deve ficar desabilitado com countdown
4. # Após 5 minutos, botão deve reabilitar

### # **3. Testar Reset:**
1. # Após bloqueio, fazer login com senha correta
2. # Verificar que login funciona e reseta contador
3. # Próxima tentativa errada deve mostrar "Restam 9 tentativas"

## # **RESULTADO ESPERADO:**

### # **Experiência do Usuário:**
- # **Feedback claro:** "Restam X tentativas"
- # **Alertas úteis:** Aviso antes do bloqueio
- # **Bloqueio visual:** Countdown de 5 minutos
- # **Reset automático:** Após login sucesso

### # **Segurança Robusta:**
- # **Proteção contra força bruta**
- # **Bloqueio temporário automático**
- # **Mensagens profissionais em português**

---

## # **IMPORTANTE:**

**O rate limiting está 100% funcional no backend. Implemente apenas o tratamento das respostas HTTP 401/429 no LoginActivity.**

**Não precisa mexer em mais nada - só adicionar o tratamento de erros e os diálogos!**
