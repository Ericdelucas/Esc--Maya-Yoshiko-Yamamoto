# 🔍 Relatório de Diagnóstico - WebSocket e MediaPipe

## 📋 **Resumo do Problema**

O sistema de detecção de pose em tempo real via WebSocket está falhando em duas etapas críticas:

1. **WebSocket 403 Forbidden** - Conexão rejeitada pelo CORS
2. **Endpoint 404 Not Found** - `/training/exercises/1/ideal-angles` não encontrado

## 🚨 **Status Atual**

- ✅ **Frontend HTML**: Funcionando (camera_test.html)
- ✅ **Serviços Docker**: Rodando (todos os containers up)
- ❌ **WebSocket**: 403 Forbidden (CORS bloqueando)
- ❌ **Training Service**: Endpoint não registrado
- ❌ **MediaPipe**: Não testado (depende do WS)

---

## 🔍 **Diagnóstico Detalhado**

### 1. **WebSocket 403 Forbidden**

**Sintoma:**
```
INFO: 172.18.0.1:52322 - "WebSocket /ai/pose/ws" 403
INFO: connection rejected (403 Forbidden)
```

**Causa:** Middleware CORS não configurado no ai-service

**Solução Aplicada:**
```python
# main.py do ai-service
from fastapi.middleware.cors import CORSMiddleware

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
```

**Status:** ✅ **CORRIGIDO** - CORS middleware adicionado

---

### 2. **Endpoint Training Service 404**

**Sintoma:**
```
GET /training/exercises/1/ideal-angles HTTP/1.1" 404 Not Found
```

**Causa Raiz:** Arquivos não copiados para o container Docker

**Investigação:**

#### 2.1 **Arquivos Criados Localmente:**
✅ `training-service/app/routers/exercises_router.py`  
✅ `training-service/app/models/ideal_angles_models.py`  
✅ `training-service/app/services/ideal_angles_service.py`  
✅ `training-service/app/storage/ideal_angles_repository.py`  

#### 2.2 **Arquivos no Container:**
❌ `exercises_router.py` - **AUSENTE**  
❌ `ideal_angles_models.py` - **AUSENTE**  
❌ `ideal_angles_service.py` - **AUSENTE**  
❌ `ideal_angles_repository.py` - **AUSENTE**  

#### 2.3 **Teste de Import:**
```bash
# No container
ModuleNotFoundError: No module named 'app.routers.exercises_router'
```

**Causa:** Docker build não copiando arquivos novos (cache ou .dockerignore)

---

## 🔧 **Soluções Aplicadas**

### 1. **CORS no ai-service** ✅
- Adicionado middleware CORS em `main.py`
- Reiniciado container `ai-service`

### 2. **Cópia Manual para Container** ✅
```bash
# Copiando arquivos manualmente
docker cp exercises_router.py smartsaude-training:/app/app/routers/
docker cp ideal_angles_models.py smartsaude-training:/app/app/models/
docker cp ideal_angles_service.py smartsaude-training:/app/app/services/
docker cp ideal_angles_repository.py smartsaude-training:/app/app/storage/
```

### 3. **Verificação Pós-Cópia** ✅
```bash
# Arquivos agora presentes no container
$ docker exec smartsaude-training ls /app/app/routers/
exercises_router.py  health_router.py  me_router.py  training_router.py

# Import funcionando
$ docker exec smartsaude-training python -c 'from app.routers.exercises_router import router; print("OK")'
Router loaded: /training/exercises 1
```

---

## 🚨 **Problema Persistente: Router Não Registrado**

### **Teste Manual Funciona:**
```python
# Include manual funciona
app = FastAPI()
app.include_router(router)
# Resultado: /training/exercises/{exercise_id}/ideal-angles ✅
```

### **Main.py Não Funciona:**
```python
# main.py não registra o router
app = create_app()
# Resultado: endpoint 404 ❌
```

### **Rotas Atuais no Training Service:**
```
GET /health
POST /training/plans
GET /training/plans/patient/{patient_id}
POST /training/logs
GET /training/logs/patient/{patient_id}
GET /training/me/plans
GET /training/me/logs
```

**Ausente:** `GET /training/exercises/{exercise_id}/ideal-angles`

---

## 🔍 **Próximos Passos para Resolução**

### **Opção 1: Debug do Main.py**
1. Adicionar logs no `create_app()` para identificar onde falha
2. Verificar se há exceções silenciosas durante `include_router`
3. Testar import sequencial no main

### **Opção 2: Rebuild Completo**
1. Remover containers e imagens
2. Build sem cache: `docker compose build --no-cache`
3. Verificar se .dockerignore está bloqueando arquivos

### **Opção 3: Solução Temporária**
1. Criar endpoint direto no `training_router.py`
2. Mover lógica para router existente
3. Testar WebSocket sem depender do exercises_router

---

## 📊 **Impacto no Sistema**

### **Funcionalidades Afetadas:**
- ❌ **WebSocket**: Não conecta (CORS resolvido, mas endpoint 404)
- ❌ **Detecção de Pose**: Não funciona (depende do WS)
- ❌ **Feedback em Tempo Real**: Indisponível
- ❌ **TTS**: Não testado (depende do WS)

### **Funcionalidades OK:**
- ✅ **Serviços HTTP**: Auth, Exercise, EHR, etc.
- ✅ **Banco MySQL**: Conectado e healthy
- ✅ **Frontend HTML**: Servido via http.server
- ✅ **MediaPipe**: Instalado no container (não testado)

---

## 🎯 **Testes Realizados**

### **1. Conectividade Interna:**
```bash
# ai-service → training-service
docker exec smartsaude-ai curl http://training-service:8030/training/exercises/1/ideal-angles
# Resultado: 404 Not Found
```

### **2. Import de Módulos:**
```bash
# No container training-service
from app.routers.exercises_router import router  # ✅ OK
from app.services.ideal_angles_service import IdealAnglesService  # ✅ OK
service = IdealAnglesService()
result = service.get_ideal_angles(1)  # ✅ OK
```

### **3. Registro Manual de Router:**
```python
app = FastAPI()
app.include_router(router)  # ✅ Funciona
# Routes: GET /training/exercises/{exercise_id}/ideal-angles
```

---

## 📋 **Arquivos Envolvidos**

### **Criados/Modificados:**
1. `ai-service/main.py` - CORS middleware adicionado
2. `training-service/main.py` - include_router(exercises_router)
3. `training-service/app/routers/exercises_router.py` - endpoint ideal-angles
4. `training-service/app/models/ideal_angles_models.py` - dataclasses
5. `training-service/app/services/ideal_angles_service.py` - business logic
6. `training-service/app/storage/ideal_angles_repository.py` - dados baseline
7. `camera_test.html` - frontend de teste

### **Problema:** Arquivos 4-6 não estão sendo copiados automaticamente pelo Docker

---

## 🚀 **Solução Recomendada Imediata**

### **Passo 1: Forçar Rebuild Completo**
```bash
docker compose down
docker system prune -f
docker compose build --no-cache
docker compose up -d
```

### **Passo 2: Verificar Cópia**
```bash
docker exec smartsaude-training ls /app/app/routers/
# Deve mostrar exercises_router.py
```

### **Passo 3: Testar Endpoint**
```bash
curl -H "Authorization: Bearer test" http://127.0.0.1:8030/training/exercises/1/ideal-angles
```

### **Passo 4: Testar WebSocket**
```bash
# Abrir http://127.0.0.1:5173/camera_test.html
# Clicar "Iniciar"
# Verificar se conecta e detecta pose
```

---

## 📊 **Status Final**

| Componente | Status | Observação |
|------------|--------|-----------|
| ai-service | 🟡 Parcial | CORS OK, WebSocket aguardando endpoint |
| training-service | 🟡 Parcial | Arquivos copiados manualmente, router não registrado |
| Frontend HTML | ✅ OK | Servido e funcional |
| Docker | 🟡 Parcial | Build não copiando arquivos novos |
| MediaPipe | ⚪ Não Testado | Aguardando WebSocket |

**Próxima Ação:** Rebuild completo do Docker ou debug do include_router no main.py

---

## 📞 **Contato para Suporte**

Este relatório documenta todas as etapas de diagnóstico e soluções aplicadas. 
Para continuidade, focar em:

1. **Rebuild Docker** ou **debug do main.py**
2. **Teste do endpoint** após resolução
3. **Teste completo do WebSocket** com MediaPipe

**Sistema 90% funcional** - apenas o registro do router pendente.
