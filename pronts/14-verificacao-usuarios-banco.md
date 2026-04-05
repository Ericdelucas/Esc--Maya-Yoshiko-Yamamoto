# 🔍 VERIFICAÇÃO DE USUÁRIOS NO BANCO

## 🎯 **PROBLEMAS IDENTIFICADOS**

1. **`dr.silva@smartsaude.com` não funciona** (401 Unauthorized)
2. **Todos vão para a mesma tela** (MainActivity)

## 📊 **COMO VERIFICAR USUÁRIOS EXISTENTES**

### **Passo 1: Conectar no MySQL via Docker**
```bash
# No terminal, na pasta Backend:
docker exec -it smartsaude-mysql mysql -u smartuser -psmartpass
```

### **Passo 2: Verificar todos os usuários**
```sql
USE smartsaude;
SELECT id, email, role, full_name, created_at FROM users ORDER BY role, email;
```

### **Passo 3: Verificar senhas (hashes)**
```sql
SELECT email, password_hash, role FROM users WHERE role IN ('professional', 'doctor', 'admin');
```

## 🧪 **TESTE DE LOGIN DIRETO NO BACKEND**

### **Testar cada usuário via curl:**
```bash
# Testar com professional:
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "profissional@smartsaude.com", "password": "prof123"}'

# Testar com admin:
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "novo.admin@smartsaude.com", "password": "admin123"}'

# Testar com paciente:
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "joao.paciente@smartsaude.com", "password": "pac123"}'
```

## 📋 **USUÁRIOS QUE DEVEM EXISTIR**

Baseado nos scripts anteriores:

### **Profissionais/Admins:**
- `novo.admin@smartsaude.com` / `admin123` (role: admin)
- `profissional@smartsaude.com` / `prof123` (role: professional)
- `dr.silva@smartsaude.com` / `prof123` (role: professional/doctor)

### **Pacientes:**
- `joao.paciente@smartsaude.com` / `pac123` (role: patient)
- `usuario.teste.2026@smartsaude.com` / `teste123` (role: patient)

## 🔧 **SE USUÁRIOS NÃO EXISTIREM**

### **Recriar usuários de teste:**
```bash
# Na pasta Backend:
docker exec -it smartsaude-mysql mysql -u smartuser -psmartpass smartsaude < create_test_users_fixed.sql
```

### **Ou criar manualmente:**
```sql
-- Criar admin:
INSERT INTO users (email, password_hash, role, full_name) VALUES 
('novo.admin@smartsaude.com', 'HASH_AQUI', 'admin', 'Administrador Teste');

-- Criar professional:
INSERT INTO users (email, password_hash, role, full_name) VALUES 
('profissional@smartsaude.com', 'HASH_AQUI', 'professional', 'Profissional Teste');

-- Criar paciente:
INSERT INTO users (email, password_hash, role, full_name) VALUES 
('joao.paciente@smartsaude.com', 'HASH_AQUI', 'patient', 'João Paciente');
```

## 🎯 **DIAGNÓSTICO DO DIRECIONAMENTO**

### **Se todos vão para MainActivity:**

1. **Verificar logs do app:**
   ```
   TokenManager_DEBUG: Salvando sessão - Role: 'professional'
   LOGIN_DEBUG: Iniciando decisão de navegação. Role recuperado: 'professional'
   LOGIN_DEBUG: 🏥 PROFISSIONAL DETECTADO: professional -> Abrindo ProfessionalMainActivity
   ```

2. **Se logs mostram direcionamento correto mas app vai para MainActivity:**
   - **ProfessionalMainActivity não existe?**
   - **Erro no AndroidManifest.xml?**
   - **Activity não registrada?**

## 🚨 **AÇÕES IMEDIATAS**

1. **Verificar usuários no banco**
2. **Testar login via curl**
3. **Verificar logs do app Android**
4. **Verificar se ProfessionalMainActivity existe**

---

**Status:** 🔄 **AGUARDANDO VERIFICAÇÃO DE USUÁRIOS E TESTES**
