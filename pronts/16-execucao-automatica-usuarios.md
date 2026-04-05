# 🚀 EXECUÇÃO AUTOMÁTICA - CRIAÇÃO DE USUÁRIOS

## 📋 **SCRIPT CRIADO**

Criei o script `create_test_users_final.py` que vai criar todos os usuários necessários.

## 🔧 **COMO EXECUTAR**

### **Opção 1: Direto no terminal**
```bash
cd /home/eric-de-lucas/Documentos/GitHub/PI3/back/Esc--Maya-Yoshiko-Yamamoto/Backend
python3 create_test_users_final.py
```

### **Opção 2: Via Docker (se o Python não estiver instalado)**
```bash
docker exec smartsaude-mysql mysql -u smartuser -psmartpass -e "
DELETE FROM users WHERE email LIKE '%@smartsaude.com';

INSERT INTO users (email, password_hash, role, full_name, created_at, updated_at) VALUES
('novo.admin@smartsaude.com', 'c2FsdC5rZXk=', 'admin', 'Administrador Teste', NOW(), NOW()),
('profissional@smartsaude.com', 'c2FsdC5rZXk=', 'professional', 'Profissional Saúde', NOW(), NOW()),
('dr.silva@smartsaude.com', 'c2FsdC5rZXk=', 'doctor', 'Dr. Silva Teste', NOW(), NOW()),
('joao.paciente@smartsaude.com', 'c2FsdC5rZXk=', 'patient', 'João Paciente', NOW(), NOW()),
('usuario.teste.2026@smartsaude.com', 'c2FsdC5rZXk=', 'patient', 'Usuário Teste', NOW(), NOW());
"

SELECT email, role, full_name FROM users ORDER BY role, email;
```

## 👤 **USUÁRIOS QUE SERÃO CRIADOS**

### **Profissionais (vão para ProfessionalMainActivity):**
- `novo.admin@smartsaude.com` / `admin123` (role: admin)
- `profissional@smartsaude.com` / `prof123` (role: professional)
- `dr.silva@smartsaude.com` / `prof123` (role: doctor)

### **Pacientes (vão para MainActivity):**
- `joao.paciente@smartsaude.com` / `pac123` (role: patient)
- `usuario.teste.2026@smartsaude.com` / `teste123` (role: patient)

## 🎯 **APÓS EXECUTAR O SCRIPT**

1. **Teste no app Android com:**
   - `profissional@smartsaude.com` / `prof123` → Deve ir para ProfessionalMainActivity
   - `joao.paciente@smartsaude.com` / `pac123` → Deve ir para MainActivity

2. **Se ainda não funcionar, verifique:**
   - Logs do app Android
   - Se ProfessionalMainActivity existe
   - Se não há erros de build

---

**Execute o script AGORA e vamos testar o direcionamento!**
