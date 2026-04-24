# # **IMPLEMENTAR RATE LIMITING NO LOGIN - PARA O GEMINI**

## # **PROBLEMA IDENTIFICADO:**
- # **Login sem limite:** Usuário pode tentar infinitas vezes
- # **Risco de segurança:** Ataques de força bruta
- # **Solução necessária:** Rate limiting com bloqueio temporário

## # **ESPECIFICAÇÕES EXIGIDAS:**
- # **Máximo de tentativas:** 10 tentativas falhas
- # **Duração do bloqueio:** 5 minutos após exceder limite
- # **Contagem por:** Email do usuário
- # **Feedback:** Mostrar tentativas restantes

## # **IMPLEMENTAÇÃO JÁ CRIADA NO BACKEND:**

### # **1. Serviço Rate Limiter (rate_limiter.py):**
```python
# # Arquivo criado: Backend/auth-service/app/services/rate_limiter.py
class RateLimiter:
    def __init__(self):
        self.max_attempts = 10
        self.block_duration_minutes = 5
        self.window_minutes = 15
        self.attempts = {}
        self.lock = Lock()
    
    def record_attempt(self, identifier: str, success: bool = False) -> dict:
        # # Registra tentativa e retorna status
        # # Bloqueia após 10 tentativas falhas
        # # Libera após 5 minutos
    
    def get_status(self, identifier: str) -> dict:
        # # Retorna status atual do rate limiting
```

### # **2. Endpoint Modificado (auth_router.py):**
```python
@router.post("/login")
def login(payload: UserLoginIn, request: Request, svc: AuthService = Depends(get_auth_service)):
    # # Verificar rate limiting
    rate_status = rate_limiter.record_attempt(payload.email, success=False)
    
    if not rate_status["allowed"]:
        # # Usuário bloqueado - HTTP 429
        raise HTTPException(
            status_code=429,
            detail={
                "error": "too_many_attempts",
                "message": "Muitas tentativas. Tente novamente em X minutos.",
                "retry_after": 300  # # 5 minutos em segundos
            }
        )
    
    try:
        login_data = svc.login(email=payload.email, password=payload.password)
        rate_limiter.record_attempt(payload.email, success=True)  # # Limpa contador
        return login_data
    except Unauthorized:
        # # Retorna tentativas restantes
        raise HTTPException(
            status_code=401,
            detail={
                "error": "invalid_credentials", 
                "message": f"Credenciais inválidas. Restam {rate_status['attempts_remaining']} tentativas."
            }
        )

@router.get("/login-status/{email}")
def get_login_status(email: str) -> dict:
    # # Endpoint para frontend verificar status
    return rate_limiter.get_status(email)
```

## # **PROBLEMAS ATUAIS:**
1. # **Rate limiter não está funcionando** (testado manualmente)
2. # **Container pode não estar atualizado**
3. # **Importação pode estar falhando**

## # **SOLUÇÕES PARA O GEMINI IMPLEMENTAR:**

### # **1. VERIFICAR SE BACKEND ESTÁ ATUALIZADO:**
```bash
# # 1. Fazer pull das alterações mais recentes
cd /home/eric-de-lucas/Documentos/GitHub/PI3/back/Esc--Maya-Yoshiko-Yamamoto
git pull origin E

# # 2. Reconstruir o container do auth-service
cd Backend
docker-compose down auth-service
docker-compose build auth-service
docker-compose up -d auth-service

# # 3. Verificar se está funcionando
curl -s http://localhost:8080/health
```

### # **2. TESTAR RATE LIMITING MANUALMENTE:**
```bash
# # Testar com múltiplas tentativas falhas:
for i in {1..12}; do 
    echo "Tentativa $i:"
    curl -s -w "\nHTTP: %{http_code}\n" \
         -X POST http://localhost:8080/auth/login \
         -H "Content-Type: application/json" \
         -d '{"email": "test@example.com", "password": "wrong"}'
    echo ""
done

# # Esperado:
# # Tentativas 1-9: HTTP 401 com "Restam X tentativas"
# # Tentativa 10: HTTP 401 com "Restam 1 tentativa"  
# # Tentativa 11+: HTTP 429 com "Muitas tentativas. Tente novamente em X minutos."
```

### # **3. VERIFICAR STATUS DO RATE LIMITER:**
```bash
# # Verificar status atual:
curl -s http://localhost:8080/auth/login-status/test@example.com | jq .

# # Esperado após algumas tentativas falhas:
{
  "identifier": "test@example.com",
  "blocked": false,
  "attempts_used": 3,
  "attempts_remaining": 7,
  "max_attempts": 10,
  "block_until": null
}
```

### # **4. IMPLEMENTAR CORREÇÕES SE NECESSÁRIO:**

#### # **4.1 Se rate limiter não foi importado:**
```python
# # Em auth_router.py - adicionar import:
from app.services.rate_limiter import rate_limiter

# # Se o arquivo não existir, criar versão simplificada:
import time
from datetime import datetime, timedelta
from threading import Lock

class SimpleRateLimiter:
    def __init__(self):
        self.attempts = {}
        self.lock = Lock()
        self.max_attempts = 10
        self.block_duration = 300  # # 5 minutos
    
    def record_attempt(self, email: str, success: bool = False) -> dict:
        with self.lock:
            now = datetime.now()
            
            if email not in self.attempts:
                self.attempts[email] = {"count": 0, "last_attempt": now, "blocked_until": None}
            
            user_data = self.attempts[email]
            
            # # Verificar se está bloqueado
            if user_data["blocked_until"] and now < user_data["blocked_until"]:
                remaining = int((user_data["blocked_until"] - now).total_seconds())
                return {
                    "allowed": False,
                    "blocked": True,
                    "retry_after": remaining,
                    "message": f"Muitas tentativas. Tente novamente em {remaining//60} minutos."
                }
            
            # # Se sucesso, resetar contador
            if success:
                self.attempts[email] = {"count": 0, "last_attempt": now, "blocked_until": None}
                return {"allowed": True, "success": True}
            
            # # Se falha, incrementar contador
            user_data["count"] += 1
            user_data["last_attempt"] = now
            
            # # Verificar se atingiu limite
            if user_data["count"] >= self.max_attempts:
                user_data["blocked_until"] = now + timedelta(seconds=self.block_duration)
                remaining = self.block_duration
                return {
                    "allowed": False,
                    "blocked": True,
                    "retry_after": remaining,
                    "message": f"Muitas tentativas. Tente novamente em {remaining//60} minutos."
                }
            
            remaining = self.max_attempts - user_data["count"]
            return {
                "allowed": True,
                "blocked": False,
                "success": False,
                "attempts_remaining": remaining,
                "message": f"Credenciais inválidas. Restam {remaining} tentativas."
            }

# # Instância global
rate_limiter = SimpleRateLimiter()
```

#### # **4.2 Se endpoint não estiver funcionando:**
```python
# # Adicionar endpoint de status após o login:
@router.get("/login-status/{email}")
def get_login_status(email: str) -> dict:
    try:
        return rate_limiter.get_status(email)
    except Exception as e:
        return {"error": "Rate limiter not available", "details": str(e)}
```

### # **5. IMPLEMENTAR MELHORIAS NO FRONTEND:**

#### # **5.1 Mostrar tentativas restantes:**
```java
// # Em LoginActivity.java - no tratamento de erro:
@Override
public void onFailure(Call<LoginResponse> call, Throwable t) {
    // # Tratar erro de rede
}

@Override
public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
    if (response.isSuccessful()) {
        // # Login sucesso
    } else {
        try {
            String errorBody = response.errorBody().string();
            JSONObject errorJson = new JSONObject(errorBody);
            
            if (response.code() == 401) {
                String message = errorJson.getString("message");
                int attemptsRemaining = errorJson.optInt("attempts_remaining", -1);
                
                if (attemptsRemaining >= 0) {
                    // # Mostrar tentativas restantes
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    
                    // # Desabilitar botão se restar poucas tentativas
                    if (attemptsRemaining <= 2) {
                        btnLogin.setEnabled(false);
                        btnLogin.setText("Aguarde antes de tentar novamente");
                    }
                } else {
                    Toast.makeText(this, "Credenciais inválidas", Toast.LENGTH_SHORT).show();
                }
            } else if (response.code() == 429) {
                // # Usuário bloqueado
                String message = errorJson.getString("message");
                int retryAfter = errorJson.optInt("retry_after", 300);
                
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                
                // # Desabilitar login e mostrar countdown
                btnLogin.setEnabled(false);
                startCountdownTimer(retryAfter);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao fazer login", Toast.LENGTH_SHORT).show();
        }
    }
}

// # Countdown timer para bloqueio:
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

#### # **5.2 Verificar status antes de tentar login:**
```java
// # Antes de tentar login, verificar status:
private void checkLoginStatus(String email) {
    authApi.getLoginStatus(email).enqueue(new Callback<LoginStatus>() {
        @Override
        public void onResponse(Call<LoginStatus> call, Response<LoginStatus> response) {
            if (response.isSuccessful()) {
                LoginStatus status = response.body();
                if (status.isBlocked()) {
                    // # Usuário bloqueado, mostrar countdown
                    startCountdownTimer(status.getRetryAfter());
                } else {
                    // # Permitir tentativa de login
                    attemptLogin();
                }
            }
        }
        
        @Override
        public void onFailure(Call<LoginStatus> call, Throwable t) {
            // # Continuar com login normal se API falhar
            attemptLogin();
        }
    });
}
```

### # **6. TESTES COMPLETOS:**

#### # **6.1 Teste de Rate Limiting:**
```bash
# # 1. Testar 10 tentativas falhas
# # 2. Verificar HTTP 401 com tentativas restantes
# # 3. Testar 11ª tentativa
# # 4. Verificar HTTP 429 com mensagem de bloqueio
# # 5. Esperar 5 minutos
# # 6. Testar novamente (deve permitir)
```

#### # **6.2 Teste de Reset:**
```bash
# # 1. Fazer algumas tentativas falhas
# # 2. Fazer login com credenciais corretas
# # 3. Verificar que contador foi resetado
# # 4. Tentar novamente - deve permitir 10 tentativas novas
```

#### # **6.3 Teste de Frontend:**
```java
// # 1. Tentar login com senha errada várias vezes
// # 2. Verificar mensagem de tentativas restantes
// # 3. Verificar bloqueio após 10 tentativas
// # 4. Verificar countdown de 5 minutos
// # 5. Verificar desbloqueio automático
```

## # **IMPLEMENTAÇÃO PASSO A PASSO:**

### # **PASSO 1: Atualizar Backend**
1. # **Fazer pull** das alterações do rate limiter
2. # **Reconstruir** container auth-service
3. # **Testar** endpoints manualmente

### # **PASSO 2: Implementar Frontend**
1. # **Atualizar LoginActivity** para tratar respostas 401/429
2. # **Implementar countdown timer** para bloqueios
3. # **Mostrar tentativas restantes** ao usuário

### # **PASSO 3: Testes Finais**
1. # **Testar fluxo completo** de rate limiting
2. # **Verificar experiência do usuário**
3. # **Testar reset** após login bem-sucedido

## # **RESULTADO ESPERADO:**

### # **Backend:**
- # **10 tentativas** máximas por email
- # **Bloqueio de 5 minutos** após limite
- # **Reset automático** após login sucesso
- # **Endpoint de status** para consulta

### # **Frontend:**
- # **Feedback claro** de tentativas restantes
- # **Bloqueio visual** com countdown
- # **Mensagens amigáveis** em português
- # **Proteção contra** ataques de força bruta

---

## # **IMPORTANTE PARA O GEMINI:**

**O rate limiting já foi implementado no backend, mas pode não estar funcionando devido a:**
1. # **Container desatualizado** - precisa reconstruir
2. # **Importação falhando** - verificar logs
3. # **Configuração incorreta** - testar manualmente

**Siga os passos acima para verificar e corrigir a implementação. Teste com as requisições curl fornecidas para confirmar que está funcionando antes de implementar no frontend.**
