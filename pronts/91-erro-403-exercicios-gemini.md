# # **ERRO 403 FORBIDDEN - UPLOAD DE EXERCÍCIOS - GEMINI**

## # **PROBLEMA IDENTIFICADO:**

### # **Logs do erro:**
```
smartsaude-exercise | POST /exercises/upload/image HTTP/1.1" 403 Forbidden
smartsaude-auth     | GET /auth/verify HTTP/1.1" 200 OK
```

### # **Diagnóstico:**
- # **Token JWT:** Válido (auth service responde 200)
- # **Permissão:** Negada (exercise service retorna 403)
- # **Causa:** **Usuário não tem role/permissão** para upload de exercícios

## # **SOLUÇÕES POSSÍVEIS:**

### # **SOLUÇÃO 1: Verificar Role do Usuário**

#### # **Debug no app:**
```java
// # Em AddExerciseActivity, no método initViews():
private void initViews() {
    // ... código existente ...
    
    SharedPreferences prefs = getSharedPreferences("SmartSaudePrefs", MODE_PRIVATE);
    token = prefs.getString("jwt_token", "");
    String role = prefs.getString("user_role", "Unknown");
    String userId = prefs.getString("user_id", "Unknown");
    
    Log.d(TAG, "=== DEBUG AUTENTICAÇÃO ===");
    Log.d(TAG, "Token: " + (token.isEmpty() ? "VAZIO" : "OK"));
    Log.d(TAG, "Role: " + role);
    Log.d(TAG, "User ID: " + userId);
    Log.d(TAG, "Exercise URL: " + Constants.EXERCISE_BASE_URL);
    
    // # Verificar se usuário tem permissão
    if (!"professional".equals(role) && !"admin".equals(role)) {
        Log.e(TAG, "ERRO: Usuário não tem permissão para criar exercícios!");
        Log.e(TAG, "Role atual: " + role);
        Toast.makeText(this, "Apenas profissionais podem criar exercícios", Toast.LENGTH_LONG).show();
        btnSave.setEnabled(false);
        return;
    }
    
    Log.d(TAG, "Usuário tem permissão para exercícios");
}
```

### # **SOLUÇÃO 2: Fazer Login como Profissional**

#### # **Verificar tipo de usuário:**
1. # **Fazer logout** do app atual
2. # **Fazer login** com usuário **profissional**
3. # **Verificar se role está sendo salvo** corretamente

#### # **Criar usuário profissional (se não existir):**
```bash
# # Testar criar usuário profissional via API:
curl -X POST "http://localhost:8080/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "profissional@teste.com",
    "password": "123456",
    "name": "Profissional Teste",
    "role": "professional"
  }'
```

### # **SOLUÇÃO 3: Corrigir Backend de Exercícios**

#### # **Verificar se endpoint exige role específica:**
```bash
# # Testar endpoint com token válido:
# # 1. Fazer login e pegar token:
TOKEN=$(curl -s -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "profissional@teste.com", "password": "123456"}' | \
  jq -r '.access_token')

# # 2. Testar upload:
curl -X POST "http://localhost:8081/exercises/upload/image" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/dev/null"

# # Se retornar 403, verificar código do backend
```

#### # **Verificar código do backend:**
```python
# # No backend de exercícios, verificar se há verificação de role:
# # Arquivo: exercise_service/app/routers/exercise_router.py

@router.post("/upload/image")
async def upload_image(
    file: UploadFile = File(...),
    current_user: User = Depends(get_current_user),  # # Pode estar faltando role check
    db: Session = Depends(get_db)
):
    # # Verificar se há verificação de role aqui:
    if current_user.role not in ["professional", "admin"]:
        raise HTTPException(status_code=403, detail="Apenas profissionais podem fazer upload")
    
    # ... resto do código ...
```

### # **SOLUÇÃO 4: Modificar Backend (Se necessário)**

#### # **Adicionar verificação de role ou remover restrição:**
```python
# # Opção 1: Permitir qualquer usuário autenticado
@router.post("/upload/image")
async def upload_image(
    file: UploadFile = File(...),
    current_user: User = Depends(get_current_user),  # # Só verifica se está autenticado
    db: Session = Depends(get_db)
):
    # # Remove verificação de role
    # ... resto do código ...

# # Opção 2: Adicionar verificação explícita
@router.post("/upload/image")
async def upload_image(
    file: UploadFile = File(...),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    # # Log para debug
    print(f"Upload attempt - User: {current_user.email}, Role: {current_user.role}")
    
    # # Permitir múltiplos roles
    if current_user.role not in ["professional", "admin", "patient"]:
        raise HTTPException(
            status_code=403, 
            detail=f"Permissão negada. Role atual: {current_user.role}"
        )
    
    # ... resto do código ...
```

### # **SOLUÇÃO 5: Verificar Token no Header**

#### # **Debug do token sendo enviado:**
```java
// # Em AddExerciseActivity, antes da chamada da API:
private void uploadImage() {
    try {
        Log.d(TAG, "=== DEBUG UPLOAD ===");
        Log.d(TAG, "Token length: " + token.length());
        Log.d(TAG, "Token starts with: " + (token.startsWith("Bearer ") ? "SIM" : "NÃO"));
        
        if (!token.startsWith("Bearer ")) {
            token = "Bearer " + token;
            Log.d(TAG, "Token corrigido: " + token.substring(0, 30) + "...");
        }
        
        File file = getFileFromUri(imageUri, "img_upload");
        RequestBody requestFile = RequestBody.create(file, MediaType.parse(getContentResolver().getType(imageUri)));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        ExerciseApi api = ApiClient.getExerciseClient().create(ExerciseApi.class);
        
        Log.d(TAG, "URL: " + Constants.EXERCISE_BASE_URL + "exercises/upload/image");
        Log.d(TAG, "Auth Header: " + token.substring(0, 30) + "...");
        
        api.uploadImage(token, body).enqueue(new Callback<FileUploadResponse>() {
            // ... callback existente ...
        });
    } catch (Exception e) {
        Log.e(TAG, "ERRO NO UPLOAD", e);
        handleError("Erro ao processar imagem: " + e.getMessage());
    }
}
```

## # **DIAGNÓSTICO PASSO A PASSO:**

### # **PASSO 1: Verificar role do usuário atual**
```bash
# # Verificar logs do app:
adb logcat -s "EXERCISE_UPLOAD_DEBUG" | grep "Role:"
```

### # **PASSO 2: Testar com usuário profissional**
```bash
# # Fazer login com profissional e verificar:
adb logcat -s "EXERCISE_UPLOAD_DEBUG" | grep "Role:"
```

### # **PASSO 3: Verificar backend**
```bash
# # Verificar logs do backend de exercícios:
docker logs smartsaude-exercise --tail 50
```

### # **PASSO 4: Testar API diretamente**
```bash
# # Com token de profissional:
curl -X POST "http://localhost:8081/exercises/upload/image" \
  -H "Authorization: Bearer TOKEN_PROFISSIONAL" \
  -F "file=@/dev/null"
```

## # **RESULTADO ESPERADO:**

### # **Após correção:**
- # **Logs mostram** role correto do usuário
- # **Upload funciona** para usuários profissionais
- # **Mensagem clara** se usuário não tiver permissão
- # **Backend loga** tentativas de upload para debug

---

## # **PARA O GEMINI - AÇÕES IMEDIATAS:**

**1. Adicionar debug de role no AddExerciseActivity**
**2. Verificar se usuário atual é profissional**
**3. Se não for, fazer login com usuário profissional**
**4. Se necessário, modificar backend para permitir pacientes**
**5. Testar com diferentes tipos de usuário**

**O erro 403 indica problema de permissão - quase sempre o usuário não é profissional!**
