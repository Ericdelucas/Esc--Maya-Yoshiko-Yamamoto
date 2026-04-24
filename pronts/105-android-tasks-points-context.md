# 📋 **Contexto e Diretrizes para Implementação do Sistema de Tarefas e Pontos**

## 🎯 **IMPORTANTE: Entender o Contexto Antes de Implementar**

**Este arquivo PRECEDE o arquivo 105** e explica EXATAMENTE onde e como fazer as alterações no Android existente.

---

## 📍 **Onde Fazer as Alterações**

### **❌ NÃO CRIAR NOVAS TELAS - MODIFICAR AS EXISTENTES!**

**ERRO COMUM:** Criar novas activities separadas para tarefas
**CORRETO:** Integrar o sistema de tarefas NAS TELAS JÁ EXISTENTES

---

## 🎯 **Mapeamento Exato das Alterações**

### **1. TELA DE EXERCÍCIOS DO PACIENTE**

**Arquivo:** `ExerciseListActivity.java` (JÁ EXISTE)
**Local:** `app/src/main/java/com/example/testbackend/ExerciseListActivity.java`

**O que fazer:**
- ❌ **NÃO** criar nova "TaskListActivity"
- ✅ **MODIFICAR** a `ExerciseListActivity` existente
- ✅ **ADICIONAR** as tarefas junto com os exercícios
- ✅ **MANTER** a mesma estrutura e navegação

**Como fica a tela:**
```
Meus Exercícios (título existente)
├── Exercício 1 (existente)
├── Exercício 2 (existente)
├── ─────────────────────
├── 🎯 Tarefa: Fazer alongamento (NOVO)
│   └── ☐ RadioButton para marcar
├── 🎯 Tarefa: Beber água (NOVO)
│   └── ☐ RadioButton para marcar
└── 🎯 Tarefa: Meditar 10min (NOVO)
    └── ☐ RadioButton para marcar
```

### **2. TELA PRINCIPAL DO PROFISSIONAL**

**Arquivo:** `ProfessionalMainActivity.java` (JÁ EXISTE)
**Local:** `app/src/main/java/com/example/testbackend/ProfessionalMainActivity.java`

**O que fazer:**
- ❌ **NÃO** criar nova "CreateTaskActivity"
- ✅ **MODIFICAR** a `ProfessionalMainActivity` existente
- ✅ **ADICIONAR** botão FAB (+) para criar tarefas
- ✅ **USAR** dialog/modal para criar tarefas (não nova tela)

**Como fica a tela:**
```
Área do Profissional (título existente)
├── Minhas Consultas (existente)
├── Meus Pacientes (existente)
├── Relatórios (existente)
└── [+] FAB para criar tarefa (NOVO)
```

### **3. TELA DE RANKING**

**Arquivo:** `LeaderboardActivity.java` (JÁ EXISTE)
**Local:** `app/src/main/java/com/example/testbackend/LeaderboardActivity.java`

**O que fazer:**
- ❌ **NÃO** criar nova "PointsActivity"
- ✅ **MODIFICAR** a `LeaderboardActivity` existente
- ✅ **TROCAR** dados mock por dados reais da API
- ✅ **MANTHER** layout e estrutura existentes

---

## 🔧 **Estrutura de Arquivos - Onde Modificar**

### **Models - ADICIONAR aos arquivos existentes:**

**Arquivo:** `app/src/main/java/com/example/testbackend/models/`
```
models/
├── Exercise.java (EXISTENTE - não modificar)
├── Challenge.java (EXISTENTE - não modificar)
├── LeaderboardEntry.java (EXISTENTE - ATUALIZAR)
└── Task.java (NOVO - criar este arquivo)
```

### **Adapters - MODIFICAR o existente:**

**Arquivo:** `app/src/main/java/com/example/testbackend/adapters/`
```
adapters/
├── ExerciseAdapter.java (EXISTENTE - MODIFICAR)
│   └── Adicionar suporte a RadioButton para tarefas
└── (NÃO criar TaskAdapter separado)
```

### **APIs - ADICIONAR ao existente:**

**Arquivo:** `app/src/main/java/com/example/testbackend/network/`
```
network/
├── ApiClient.java (EXISTENTE - ADICIONAR getTaskClient())
└── (NÃO criar TaskApi separado - usar ApiClient existente)
```

---

## 📱 **Fluxo de Navegação - NÃO MUDAR!**

### **Paciente:**
```
MainActivity → ExerciseListActivity (MODIFICADA)
├── Mostra exercícios + tarefas
├── RadioButton para cada tarefa
└── Ao clicar: chama API e atualiza pontos
```

### **Profissional:**
```
ProfessionalMainActivity (MODIFICADA)
├── Tela principal existente
├── Botão FAB (+) para criar tarefa
└── Dialog para criar (não nova Activity)
```

### **Ranking:**
```
MainActivity → LeaderboardActivity (MODIFICADA)
├── Mesma estrutura existente
├── Dados reais da API
└── Layout mantido
```

---

## 🎯 **Regras de Ouro para o Gemini**

### **1. NUNCA criar Activities novas**
- ✅ Modificar as existentes
- ❌ Criar "TaskActivity", "CreateTaskActivity", etc.

### **2. NUNCA criar Adapters novos**
- ✅ Modificar `ExerciseAdapter.java` existente
- ❌ Criar "TaskAdapter.java"

### **3. NUNCA mudar navegação**
- ✅ Manter mesmos menus e botões
- ❌ Adicionar novos itens no navigation drawer

### **4. SEMPRE integrar com o existente**
- ✅ Tarefas aparecem junto com exercícios
- ✅ Ranking usa mesma tela existente
- ✅ Profissional usa FAB na tela principal

---

## 🔍 **Exemplo Prático de Modificação**

### **ANTES (ExerciseListActivity.java):**
```java
// Código existente mostra apenas exercícios
exerciseApi.getExercises().enqueue(new Callback<List<Exercise>>() {
    // ... carrega exercícios apenas
});
```

### **DEPOIS (ExerciseListActivity.java MODIFICADA):**
```java
// Código modificado mostra exercícios + tarefas
exerciseApi.getExercises().enqueue(new Callback<List<Exercise>>() {
    // ... carrega exercícios
});

// NOVO: Carregar tarefas também
taskApi.getPatientTasks().enqueue(new Callback<List<Task>>() {
    // ... carrega tarefas e adiciona à lista
});

// Adapter modificado para mostrar ambos
adapter = new MixedExerciseTaskAdapter(exercises + tasks);
```

---

## 📋 **Checklist de Implementação**

### **Para o Gemini Seguir:**

- [ ] **MODIFICAR** `ExerciseListActivity.java` (não criar nova)
- [ ] **ATUALIZAR** `ExerciseAdapter.java` com RadioButton
- [ ] **MODIFICAR** `ProfessionalMainActivity.java` com FAB
- [ ] **ATUALIZAR** `LeaderboardActivity.java` com API real
- [ ] **CRIAR** apenas os Models novos (`Task.java`)
- [ ] **ADICIONAR** métodos ao `ApiClient.java` existente

### **O QUE NÃO FAZER:**
- [ ] ❌ Criar novas Activities
- [ ] ❌ Criar novos Adapters
- [ ] ❌ Mudar navegação
- [ ] ❌ Criar novas telas

---

## 🚀 **Próximo Passo**

Depois de entender este contexto, prossiga para o arquivo **105-android-tasks-points-system.md** que contém o código detalhado de CADA modificação.

**Lembre-se: Integrar, não substituir! 🎯**
