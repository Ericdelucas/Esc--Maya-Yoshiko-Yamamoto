# # **CORRIGIR HTTP STATUS NO RATE LIMITING - GEMINI**

## # **PROBLEMA IDENTIFICADO:**

### # **O que está acontecendo:**
- # **Rate limiting funciona:** Bloqueia após 10 tentativas
- # **Errado:** Retorna HTTP 401 em vez de HTTP 429 quando bloqueado
- # **Log mostra:** `Erro no rate limiting: 429` mas API retorna 401
- # **Resultado:** Frontend mostra "Credenciais inválidas" em vez de "Aguarde 5 minutos"

### # **Logs do problema:**
```
smartsaude-auth | Erro no rate limiting: 429: {'error': 'too_many_attempts', 'message': 'Muitas tentativas. Tente novamente em 5 minutos.', 'retry_after': 300}
smartsaude-auth | INFO: "POST /auth/login HTTP/1.1" 401 Unauthorized  # DEVERIA SER 429
```

## # **SOLUÇÃO - CORRIGIR HTTP STATUS:**

### # **1. Arquivo para corrigir:**
`Backend/auth-service/app/routers/auth_router.py`

### # **2. Problema no código:**
```python
# # LINHA ATUAL (ERRADA):
except Exception as e:
    # # Log do erro e fallback sem rate limiting
    print(f"Erro no rate limiting: {e}")
    
    # # Tentar login sem rate limiting como fallback  # # ESTE FALLBACK ESTÁ ERRADO!
    try:
        login_data = svc.login(email=payload.email, password=payload.password)
        return TokenOut(**login_data)
    except Unauthorized:
        raise HTTPException(
            status_code=401,  # # ERRADO - deveria manter 429
            detail={"error": "invalid_credentials", "message": "Credenciais inválidas"}
        )
```

### # **3. Correção necessária:**

#### # **Opção 1: Remover fallback (Recomendado):**
```python
@router.post("/login")
def login(payload: UserLoginIn, request: Request, svc: AuthService = Depends(get_auth_service)):
    try:
        # # Verificar rate limiting por email
        rate_status = rate_limiter.record_attempt(payload.email, success=False)
        
        if not rate_status.get("allowed", True):
            # # Usuário está bloqueado - RETORNAR 429
            raise HTTPException(
                status_code=status.HTTP_429_TOO_MANY_REQUESTS,  # # CORRETO
                detail={
                    "error": "too_many_attempts",
                    "message": rate_status["message"],
                    "retry_after": rate_status["retry_after"],
                    "attempts_used": rate_status.get("attempts_used", 0),
                    "max_attempts": rate_status.get("max_attempts", 10)
                },
                headers={"Retry-After": str(rate_status["retry_after"])}
            )
        
        # # Tentar fazer login
        login_data = svc.login(email=payload.email, password=payload.password)
        
        # # Login bem-sucedido - registrar sucesso no rate limiter
        rate_limiter.record_attempt(payload.email, success=True)
        
        return TokenOut(**login_data)
        
    except Unauthorized:
        # # Login falhou - rate limiter já registrou a tentativa falha acima
        # # Retornar mensagem informativa com tentativas restantes
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail={
                "error": "invalid_credentials",
                "message": rate_status.get("message", "Credenciais inválidas"),
                "attempts_remaining": rate_status.get("attempts_remaining", 0),
                "max_attempts": rate_status.get("max_attempts", 10)
            }
        )
    # # REMOVER O BLOCO EXCEPT QUE CAUSA O PROBLEMA
```

#### # **Opção 2: Corrigir fallback:**
```python
    except HTTPException as he:
        # # Se já for HTTPException (429), propagar
        if he.status_code == 429:
            raise he
        # # Se for outro erro, tratar como Unauthorized
        raise HTTPException(
            status_code=401,
            detail={"error": "invalid_credentials", "message": "Credenciais inválidas"}
        )
```

## # **PASSOS PARA O GEMINI:**

### # **1. Localizar o problema:**
```bash
# # Abrir o arquivo:
vim Backend/auth-service/app/routers/auth_router.py

# # Procurar pela linha:
# "Erro no rate limiting"
```

### # **2. Corrigir o código:**
```python
# # REMOVER ou COMENTAR este bloco inteiro:
except Exception as e:
    print(f"Erro no rate limiting: {e}")
    try:
        login_data = svc.login(email=payload.email, password=payload.password)
        return TokenOut(**login_data)
    except Unauthorized:
        raise HTTPException(
            status_code=401,
            detail={"error": "invalid_credentials", "message": "Credenciais inválidas"}
        )
```

### # **3. Reconstruir e testar:**
```bash
# # Reconstruir container:
docker compose up --build -d auth-service

# # Testar rate limiting:
for i in {1..12}; do 
    echo "Tentativa $i:"
    curl -s -w "\nHTTP: %{http_code}\n" -X POST http://localhost:8080/auth/login \
      -H "Content-Type: application/json" \
      -d '{"email": "teste@gemini.com", "password": "wrong"}'
    echo ""
done
```

### # **4. Resultado esperado:**
```
Tentativa 1-10: HTTP 401 - "Credenciais inválidas. Restam X tentativas."
Tentativa 11: HTTP 429 - "Muitas tentativas. Tente novamente em 5 minutos."
Tentativa 12: HTTP 429 - "Muitas tentativas. Tente novamente em 4 minutos."
```

## # **INSTRUÇÕES PARA FRONTEND (APÓS CORREÇÃO):**

### # **1. Tratar corretamente HTTP 429:**
```java
@Override
public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
    if (response.isSuccessful()) {
        // Login sucesso
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
        return;
    }
    
    try {
        String errorBody = response.errorBody().string();
        JSONObject error = new JSONObject(errorBody);
        JSONObject detail = error.getJSONObject("detail");
        
        if (response.code() == 401) {
            // Tentativas restantes
            String message = detail.getString("message");
            int attemptsRemaining = detail.optInt("attempts_remaining", -1);
            
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            
            if (attemptsRemaining >= 0 && attemptsRemaining <= 2) {
                showWarningDialog(attemptsRemaining);
            }
            
        } else if (response.code() == 429) {
            // # USUÁRIO BLOQUEADO - AGORA VAI FUNCIONAR!
            String message = detail.getString("message");
            int retryAfter = detail.optInt("retry_after", 300);
            
            showBlockedDialog(message, retryAfter);
        }
        
    } catch (Exception e) {
        Toast.makeText(this, "Erro ao fazer login", Toast.LENGTH_SHORT).show();
    }
}
```

## # **IMPORTANTE:**

### # **Causa do problema:**
O `except Exception` está capturando o HTTPException 429 e convertendo para 401 no fallback.

### # **Solução mais simples:**
Remover completamente o bloco `except Exception` que faz o fallback.

### # **Teste após correção:**
1. # Fazer 10 tentativas erradas
2. # 11ª deve retornar HTTP 429
3. # Frontend deve mostrar "Aguarde 5 minutos"

---

## # **RESUMO PARA O GEMINI:**

**1. Remover o bloco `except Exception` do auth_router.py**
**2. Reconstruir o container**
**3. Testar até obter HTTP 429 na 11ª tentativa**
**4. Implementar tratamento 429 no frontend**

**Isso vai corrigir o problema de mostrar "Credenciais inválidas" em vez de "Aguarde 5 minutos".**
