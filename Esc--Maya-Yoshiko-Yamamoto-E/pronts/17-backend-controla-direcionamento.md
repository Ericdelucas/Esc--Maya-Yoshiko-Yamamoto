# 🎯 SOLUÇÃO DEFINITIVA - BACKEND CONTROLA O DIRECIONAMENTO

## 🎯 **ESTRATÉGIA: Backend Controla Tudo**

O backend vai dizer exatamente para qual tela o frontend deve ir, sem depender de lógica complexa no Android.

## 🔧 **ALTERAÇÕES NO BACKEND**

### **Arquivo: auth-service/app/routers/auth_router.py**

**Modificar a resposta do login para incluir a activity de destino:**

```python
@router.post("/login")
async def login(login_data: UserLoginIn, db: Session = Depends(get_db)):
    try:
        user = db.query(User).filter(User.email == login_data.email).first()
        
        if not user or not verify_password(login_data.password, user.password_hash):
            raise HTTPException(status_code=401, detail="Credenciais inválidas")
        
        # 🔥 NOVO: Determinar activity de destino
        target_activity = determine_target_activity(user.role)
        
        access_token = create_access_token(data={"sub": user.email, "role": user.role})
        
        return {
            "access_token": access_token,
            "token": access_token,
            "user_role": user.role,
            "role": user.role,
            "full_name": user.full_name,
            "email": user.email,
            # 🔥 NOVO: Backend diz para qual tela ir
            "target_activity": target_activity,
            "is_professional": user.role in ["professional", "doctor", "admin"]
        }
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro interno: {str(e)}")

def determine_target_activity(role: str) -> str:
    """Backend decide qual activity abrir"""
    if role in ["professional", "doctor", "admin"]:
        return "ProfessionalMainActivity"
    else:
        return "MainActivity"
```

## 📱 **ALTERAÇÕES NO FRONTEND**

### **Arquivo: models/LoginResponse.java**

**Adicionar novos campos:**

```java
public class LoginResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("user_role")
    private String userRole;

    @SerializedName("role")
    private String role;

    // 🔥 NOVOS CAMPOS
    @SerializedName("target_activity")
    private String targetActivity;

    @SerializedName("is_professional")
    private boolean isProfessional;

    // Getters existentes...

    public String getTargetActivity() {
        return targetActivity;
    }

    public boolean isProfessional() {
        return isProfessional;
    }
}
```

### **Arquivo: LoginActivity.java**

**Simplificar o método de navegação:**

```java
private void navigateToCorrectActivity() {
    // 🔥 SIMPLIFICADO: Usa o que o backend mandou
    String targetActivity = loginResponse.getTargetActivity();
    boolean isProfessional = loginResponse.isProfessional();
    
    Log.d(TAG, "Backend mandou ir para: " + targetActivity);
    Log.d(TAG, "É profissional? " + isProfessional);
    
    Class<?> activityClass;
    
    // Backend decide, frontend só executa
    if ("ProfessionalMainActivity".equals(targetActivity)) {
        activityClass = ProfessionalMainActivity.class;
        Log.d(TAG, "🏥 PROFISSIONAL -> ProfessionalMainActivity");
    } else {
        activityClass = MainActivity.class;
        Log.d(TAG, "👤 PACIENTE -> MainActivity");
    }
    
    Intent intent = new Intent(this, activityClass);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
}
```

**Modificar o performLogin para salvar a resposta:**

```java
// No método performLogin(), após sucesso:
if (response.isSuccessful() && response.body() != null) {
    LoginResponse loginResponse = response.body();
    String token = loginResponse.getToken();
    
    if (token != null && !token.isEmpty()) {
        // Salvar sessão
        tokenManager.saveSession(token, loginResponse.getUserRole(), email);
        
        // 🔥 Guardar resposta para navegação
        this.loginResponse = loginResponse;
        
        // Navegar baseado no que o backend mandou
        navigateToCorrectActivity();
    }
}
```

**Adicionar campo na classe:**
```java
private LoginResponse loginResponse; // No topo da classe LoginActivity
```

## 🧪 **TESTE APÓS ALTERAÇÕES**

### **1. Testar resposta do backend:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "profissional@smartsaude.com", "password": "prof123"}'
```

**Resposta esperada:**
```json
{
  "token": "eyJ...",
  "user_role": "professional",
  "target_activity": "ProfessionalMainActivity",
  "is_professional": true
}
```

### **2. Testar no app:**
- **Profissional:** `profissional@smartsaude.com` / `prof123` → ProfessionalMainActivity
- **Paciente:** `joao.paciente@smartsaude.com` / `pac123` → MainActivity

## 🎯 **VANTAGENS DESTA SOLUÇÃO**

1. **Backend controla** - Lógica centralizada
2. **Frontend simplificado** - Só executa o que backend manda
3. **Fácil de debugar** - Backend diz exatamente o que fazer
4. **Sem comparações complexas** no Android
5. **Consistente** - Sempre usa a mesma regra

---

**Status:** 🔄 **AGUARDANDO IMPLEMENTAÇÃO DAS ALTERAÇÕES**
