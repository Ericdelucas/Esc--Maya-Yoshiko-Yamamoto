# 🚨 URGENTE - TABELA APPOINTMENTS NÃO EXISTE

## ❌ **PROBLEMA IDENTIFICADO**

```
Table 'smartsaude.appointments' doesn't exist
```

**Causa:** A tabela `appointments` não foi criada no banco de dados!

## ✅ **SOLUÇÃO IMEDIATA**

### **1. Criar a tabela no banco:**

```bash
cd /home/eric-de-lucas/Documentos/GitHub/PI3/back/Esc--Maya-Yoshiko-Yamamoto/Backend
docker exec smartsaude-mysql mysql -u smartuser -psmartpass smartsaude < database/migrations/create_appointments_table.sql
```

### **2. Verificar se a tabela foi criada:**

```bash
docker exec smartsaude-mysql mysql -u smartuser -psmartpass smartsaude -e "SHOW TABLES;"
```

**Deve mostrar:**
```
+----------------------+
| Tables_in_smartsaude |
+----------------------+
| appointments         |
| users                |
+----------------------+
```

### **3. Verificar estrutura da tabela:**

```bash
docker exec smartsaude-mysql mysql -u smartuser -psmartpass smartsaude -e "DESCRIBE appointments;"
```

**Deve mostrar:**
```
+------------------+-------------+------+-----+---------+----------------+
| Field            | Type        | Null | Key | Default | Extra          |
+------------------+-------------+------+-----+---------+----------------+
| id               | int         | NO   | PRI | NULL    | auto_increment |
| title            | varchar(255)| NO   |     | NULL    |                |
| description      | text        | YES  |     | NULL    |                |
| appointment_date | datetime    | NO   |     | NULL    |                |
| time             | varchar(10) | YES  |     | NULL    |                |
| professional_id  | int         | NO   |     | NULL    |                |
| patient_id       | int         | YES  |     | NULL    |                |
| status           | varchar(20) | YES  |     | scheduled |              |
| created_at       | datetime    | NO   |     | NULL    |                |
| updated_at       | datetime    | YES  |     | NULL    |                |
+------------------+-------------+------+-----+---------+----------------+
```

## 🔄 **SE O ARQUIVO SQL NÃO EXISTIR**

### **Criar manualmente:**

**Arquivo:** `Backend/database/migrations/create_appointments_table.sql`

```sql
CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    appointment_date DATETIME NOT NULL,
    time VARCHAR(10),  -- HH:MM format
    professional_id INT NOT NULL,
    patient_id INT,
    status VARCHAR(20) DEFAULT 'scheduled',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (professional_id) REFERENCES users(id),
    FOREIGN KEY (patient_id) REFERENCES users(id),
    INDEX idx_professional_date (professional_id, appointment_date),
    INDEX idx_date (appointment_date)
);
```

## 🧪 **COMO TESTAR**

### **1. Após criar tabela:**
```bash
# Reiniciar backend
docker-compose restart auth-service
```

### **2. Testar endpoint:**
```bash
# Testar criar agendamento
curl -X POST http://localhost:8080/appointments/ \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Consulta Teste",
    "description": "Descrição da consulta",
    "appointment_date": "2026-04-15",
    "time": "14:30"
  }'
```

**Resposta esperada:**
```json
{
  "message": "Agendamento criado com sucesso",
  "appointment": {
    "id": 1,
    "title": "Consulta Teste",
    "description": "Descrição da consulta",
    "appointment_date": "2026-04-15T00:00:00",
    "time": "14:30",
    "professional_id": 5,
    "patient_id": null,
    "status": "scheduled"
  }
}
```

### **3. Testar no app:**
1. **Login como profissional**
2. **Abrir calendário**
3. **Criar agendamento**
4. **Verificar se salva**

## 📊 **RESULTADO ESPERADO**

### **Antes (erro):**
```
❌ 500 Internal Server Error
❌ Table 'smartsaude.appointments' doesn't exist
```

### **Depois (corrigido):**
```
✅ 200 OK
✅ Agendamento criado com sucesso
✅ Agendamentos carregados do banco
```

---

**Status:** 🚨 **URGENTE - CRIAR TABELA APPOINTMENTS NO BANCO**
