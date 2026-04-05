# 🔍 ERRO 409 CONFLICT - REGISTRO DE USUÁRIOS

## 📋 **Diagnóstico do Problema**

### ✅ **Backend está funcionando CORRETAMENTE**
- **Auth Service:** Rodando na porta 8080 ✅
- **Frontend:** Já corrigido para usar porta 8080 ✅  
- **Registro:** Funcionando para novos usuários ✅

### ❌ **Causa do Erro 409 Conflict**
O frontend está tentando registrar **emails que já existem** no banco de dados.

## 🧪 **Comprovação do Funcionamento**

```bash
# ✅ Novo usuário - FUNCIONA
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "Teste User", "email": "novo.email@smartsaude.com", "password": "123456", "role": "patient"}'

# ❌ Email duplicado - ERRO 409
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "Teste User", "email": "admin@smartsaude.com", "password": "123456", "role": "patient"}'
```

## 👥 **Usuários Já Existentes no Banco**

| Email | Função | Status |
|-------|-------|--------|
| admin@smartsaude.com | admin | ❌ Já existe |
| profissional@smartsaude.com | professional | ❌ Já existe |
| paciente@smartsaude.com | patient | ❌ Já existe |
| teste@teste.com | professional | ❌ Já existe |
| novo.admin@smartsaude.com | admin | ❌ Já existe |
| dr.silva@smartsaude.com | professional | ❌ Já existe |
| joao.paciente@smartsaude.com | patient | ❌ Já existe |
| usuario.teste.2026@smartsaude.com | patient | ❌ Já existe |

## 🎯 **Soluções**

### **Opção 1: Usar Usuários Existentes (RECOMENDADO)**
Use os usuários que já existem para fazer login:

**👑 Administrador:**
- Email: `novo.admin@smartsaude.com`
- Senha: `admin123`

**👨‍⚕️ Profissional:**
- Email: `dr.silva@smartsaude.com`
- Senha: `prof123`

**👤 Paciente:**
- Email: `joao.paciente@smartsaude.com`
- Senha: `pac123`

### **Opção 2: Criar Novo Usuário**
Use um email NUNCA utilizado antes:

**Exemplo:**
- Email: `meu.email.unico@smartsaude.com`
- Senha: `123456`
- Função: `patient`

### **Opção 3: Limpar Banco (NÃO RECOMENDADO)**
```bash
# Apenas se precisar resetar tudo
docker exec smartsaude-mysql mysql -u smartuser -psmartpass smartsaude -e "DELETE FROM users;"
```

## 🔧 **Para Desenvolvimento**

### **Criar Usuários de Teste Rápidos:**
```bash
# Admin
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "Admin Teste", "email": "admin.teste.$(date +%s)@smartsaude.com", "password": "admin123", "role": "admin"}'

# Profissional  
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "Prof Teste", "email": "prof.teste.$(date +%s)@smartsaude.com", "password": "prof123", "role": "professional"}'

# Paciente
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "Paciente Teste", "email": "paciente.teste.$(date +%s)@smartsaude.com", "password": "pac123", "role": "patient"}'
```

## ✅ **Verificação Final**

1. **Backend:** ✅ Funcionando
2. **Frontend:** ✅ Corrigido (porta 8080)
3. **Registro:** ✅ Funciona para emails novos
4. **Login:** ✅ Funciona com usuários existentes

## 🎉 **Conclusão**

**Não há erro no sistema!** O erro 409 é **comportamento esperado** ao tentar registrar um email que já existe. 

**Use os usuários existentes** para testar o login ou crie novos emails únicos para registrar novos usuários.

---

**Status:** ✅ **SISTEMA FUNCIONANDO CORRETAMENTE**
