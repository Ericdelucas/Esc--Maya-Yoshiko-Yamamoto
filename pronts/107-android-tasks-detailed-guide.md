# 📱 **GUIA DETALHADO PARA IMPLEMENTAÇÃO - ANDROID TASKS SYSTEM**

## ⚠️ **AVISO IMPORTANTE - LEIA COM ATENÇÃO**

**Este guia é EXTREMAMENTE detalhado porque o Gemini precisa de instruções ESPECÍFICAS e PASSO A PASSO. Não assuma nada, explique TUDO.**

---

## 🔍 **ANÁLISE DO PROBLEMA ATUAL**

### **O que está errado na imagem:**
1. **Tela de profissional**: Botão "Criar Exercício" (ERRADO - deveria ser "Criar Tarefa")
2. **Tela de exercícios**: Mostrando apenas exercícios, sem tarefas
3. **Login de paciente**: Não está funcionando/aparecendo
4. **Criação de exercícios**: Pedindo imagem/vídeo desnecessariamente

### **O que precisa ser consertado:**
1. **MUDAR botão "Criar Exercício" para "Criar Tarefa"**
2. **REMOVER campos de imagem/vídeo da criação**
3. **ADICIONAR tarefas na lista de exercícios do paciente**
4. **CORRIGIR login do paciente**

---

## 🎯 **PLANO DE AÇÃO DETALHADO**

### **PASSO 1: CORRIGIR TELA DO PROFISSIONAL**

#### **Arquivo:** `ProfessionalMainActivity.java`
#### **Local exato:** `app/src/main/java/com/example/testbackend/ProfessionalMainActivity.java`

**O que encontrar:**
```java
// Procure por este código EXATO:
Button btnCreateExercise = findViewById(R.id.btnCreateExercise);
btnCreateExercise.setOnClickListener(v -> {
    startActivity(new Intent(this, CreateExerciseActivity.class));
});
```

**O que fazer:**
```java
// MUDAR para:
Button btnCreateTask = findViewById(R.id.btnCreateExercise); // Manter ID se não puder mudar
btnCreateTask.setText("Criar Tarefa"); // Mudar texto
btnCreateTask.setOnClickListener(v -> {
    // NÃO ir para CreateExerciseActivity
    // Criar diálogo para criar tarefa
    showCreateTaskDialog();
});
```

**Método para adicionar:**
```java
private void showCreateTaskDialog() {
    // Criar diálogo com campos:
    // - Título (EditText)
    // - Descrição (EditText)
    // - Pontos (EditText number)
    // - Paciente (Spinner)
    // - Frequência (Spinner)
    // - Botão Salvar
}
```

### **PASSO 2: CORRIGIR TELA DE CRIAÇÃO**

#### **Arquivo:** `CreateExerciseActivity.java`
#### **Local exato:** `app/src/main/java/com/example/testbackend/CreateExerciseActivity.java`

**O que fazer:**
1. **RENOMEAR arquivo** para `CreateTaskActivity.java`
2. **MUDAR toda lógica** de exercício para tarefa
3. **REMOVER upload de imagem/vídeo**
4. **ADICIONAR campos de tarefa**

**Código ANTIGO (remover):**
```java
// REMOVER estas linhas:
ImageView ivThumbnail = findViewById(R.id.ivThumbnail);
Button btnUploadImage = findViewById(R.id.btnUploadImage);
Button btnUploadVideo = findViewById(R.id.btnUploadVideo);
```

**Código NOVO (adicionar):**
```java
// ADICIONAR estas linhas:
EditText etTitle = findViewById(R.id.etTitle);
EditText etDescription = findViewById(R.id.etDescription);
EditText etPoints = findViewById(R.id.etPoints);
Spinner spPatient = findViewById(R.id.spPatient);
Spinner spFrequency = findViewById(R.id.spFrequency);
Button btnSave = findViewById(R.id.btnSave);
```

### **PASSO 3: CORRIGIR TELA DE EXERCÍCIOS DO PACIENTE**

#### **Arquivo:** `ExerciseListActivity.java`
#### **Local exato:** `app/src/main/java/com/example/testbackend/ExerciseListActivity.java`

**O que encontrar:**
```java
// Código atual carrega apenas exercícios:
exerciseApi.getExercises().enqueue(new Callback<List<Exercise>>() {
    @Override
    public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
        exercises = response.body();
        adapter = new ExerciseAdapter(exercises);
        recyclerView.setAdapter(adapter);
    }
});
```

**O que fazer:**
```java
// MODIFICAR para carregar exercícios E tarefas:
private List<Object> mixedItems = new ArrayList<>(); // Mistura de exercícios e tarefas

private void loadExercisesAndTasks() {
    // 1. Carregar exercícios existentes
    exerciseApi.getExercises().enqueue(new Callback<List<Exercise>>() {
        @Override
        public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
            if (response.isSuccessful()) {
                mixedItems.addAll(response.body());
                loadTasks(); // Carregar tarefas depois
            }
        }
    });
}

private void loadTasks() {
    // 2. Carregar tarefas do paciente
    int patientId = getCurrentUserId(); // Obter ID do usuário logado
    taskApi.getPatientTasks("Bearer " + token, patientId).enqueue(new Callback<List<Task>>() {
        @Override
        public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
            if (response.isSuccessful()) {
                mixedItems.addAll(response.body());
                setupMixedAdapter(); // Configurar adapter misto
            }
        }
    });
}

private void setupMixedAdapter() {
    // Criar adapter que mostra ambos
    adapter = new MixedExerciseTaskAdapter(mixedItems, this);
    recyclerView.setAdapter(adapter);
}
```

### **PASSO 4: CRIAR ADAPTER MISTO**

#### **Arquivo NOVO:** `MixedExerciseTaskAdapter.java`
#### **Local exato:** `app/src/main/java/com/example/testbackend/adapters/MixedExerciseTaskAdapter.java`

**Código completo:**
```java
public class MixedExerciseTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> items;
    private Context context;
    private static final int TYPE_EXERCISE = 0;
    private static final int TYPE_TASK = 1;

    public MixedExerciseTaskAdapter(List<Object> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Exercise) {
            return TYPE_EXERCISE;
        } else if (items.get(position) instanceof Task) {
            return TYPE_TASK;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EXERCISE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
            return new ExerciseViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_task_with_radio, parent, false);
            return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_EXERCISE) {
            Exercise exercise = (Exercise) items.get(position);
            ((ExerciseViewHolder) holder).bind(exercise);
        } else {
            Task task = (Task) items.get(position);
            ((TaskViewHolder) holder).bind(task, context);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder para Exercícios
    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        ImageView ivImage;

        public ExerciseViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
        }

        public void bind(Exercise exercise) {
            tvTitle.setText(exercise.getTitle());
            tvDescription.setText(exercise.getDescription());
            // Carregar imagem se existir
        }
    }

    // ViewHolder para Tarefas
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPoints;
        RadioButton radioButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvPoints = itemView.findViewById(R.id.tvTaskPoints);
            radioButton = itemView.findViewById(R.id.radioButton);
        }

        public void bind(Task task, Context context) {
            tvTitle.setText(task.getTitle());
            tvDescription.setText(task.getDescription());
            tvPoints.setText("+" + task.getPoints_value() + " pts");
            
            // Verificar se já foi completada hoje
            radioButton.setEnabled(!task.getCompleted_today());
            radioButton.setChecked(task.getCompleted_today());
            
            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && !task.getCompleted_today()) {
                    completeTask(task);
                }
            });
        }

        private void completeTask(Task task) {
            // Chamar API para completar tarefa
            TaskApi taskApi = ApiClient.getTaskClient().create(TaskApi.class);
            TaskCompletionRequest request = new TaskCompletionRequest();
            request.setTask_id(task.getId());
            
            taskApi.completeTask("Bearer " + getToken(), request).enqueue(new Callback<TaskCompletion>() {
                @Override
                public void onResponse(Call<TaskCompletion> call, Response<TaskCompletion> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Tarefa concluída! +" + task.getPoints_value() + " pontos", Toast.LENGTH_SHORT).show();
                        task.setCompleted_today(true);
                        notifyDataSetChanged(); // Atualizar UI
                    }
                }
                
                @Override
                public void onFailure(Call<TaskCompletion> call, Throwable t) {
                    Toast.makeText(context, "Erro ao concluir tarefa", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
```

### **PASSO 5: CORRIGIR LAYOUTS**

#### **Arquivo:** `activity_create_exercise.xml`
#### **Local exato:** `res/layout/activity_create_exercise.xml`

**REMOVER:**
```xml
<!-- Remover completamente estas linhas -->
<ImageView
    android:id="@+id/ivThumbnail"
    android:layout_width="100dp"
    android:layout_height="100dp" />

<Button
    android:id="@+id/btnUploadImage"
    android:text="Carregar Imagem" />

<Button
    android:id="@+id/btnUploadVideo"
    android:text="Carregar Vídeo" />
```

**ADICIONAR:**
```xml
<!-- Adicionar estas linhas -->
<EditText
    android:id="@+id/etTitle"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Título da tarefa" />

<EditText
    android:id="@+id/etDescription"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Descrição"
    android:minLines="3" />

<EditText
    android:id="@+id/etPoints"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Pontos"
    android:inputType="number" />

<Spinner
    android:id="@+id/spPatient"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:prompt="Selecione o paciente" />

<Spinner
    android:id="@+id/spFrequency"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:entries="@array/frequency_options" />
```

#### **Arquivo NOVO:** `item_task_with_radio.xml`
#### **Local exato:** `res/layout/item_task_with_radio.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp">
        
        <RadioButton
            android:id="@+id/radioButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_vertical"
            android:layout_marginEnd="16dp" />
        
        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">
            
            <TextView
                android:id="@+id/tvTaskTitle"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Título da tarefa"
                android:textStyle="bold"
                android:textSize="16sp"
                android:textColor="#333333" />
            
            <TextView
                android:id="@+id/tvTaskDescription"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Descrição da tarefa"
                android:textColor="#666666"
                android:layout_marginTop="4dp"
                android:textSize="14sp" />
            
            <TextView
                android:id="@+id/tvTaskPoints"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="+10 pontos"
                android:textColor="#4CAF50"
                android:textStyle="bold"
                android:layout_marginTop="8dp"
                android:textSize="14sp" />
            
        </LinearLayout>
        
    </LinearLayout>
    
</androidx.cardview.widget.CardView>
```

---

## 🔧 **IMPLEMENTAÇÃO PASSO A PASSO**

### **PASSO 1: Preparação**
1. **Backup dos arquivos atuais**
2. **Criar branch nova** se usar Git
3. **Verificar dependências** no build.gradle

### **PASSO 2: Models**
1. **Criar Task.java** em `models/`
2. **Criar TaskCompletion.java** em `models/`
3. **Atualizar LeaderboardEntry.java** se necessário

### **PASSO 3: APIs**
1. **Adicionar TaskApi** em `network/`
2. **Atualizar ApiClient.java** com `getTaskClient()`
3. **Testar endpoints** individualmente

### **PASSO 4: Activities**
1. **MODIFICAR ProfessionalMainActivity.java**
2. **MODIFICAR CreateExerciseActivity.java** → `CreateTaskActivity.java`
3. **MODIFICAR ExerciseListActivity.java**
4. **TESTAR cada modificação** antes de prosseguir

### **PASSO 5: Adapters**
1. **CRIAR MixedExerciseTaskAdapter.java**
2. **REMOVER ExerciseAdapter.java** se não for mais necessário
3. **TESTAR adapter misto**

### **PASSO 6: Layouts**
1. **MODIFICAR activity_create_exercise.xml**
2. **CRIAR item_task_with_radio.xml**
3. **REMOVER layouts desnecessários**

### **PASSO 7: Testes**
1. **Testar criação de tarefa**
2. **Testar listagem mista**
3. **Testar conclusão com RadioButton**
4. **Testar atualização de pontos**

---

## ⚠️ **PONTOS CRÍTICOS DE ATENÇÃO**

### **NÃO ESQUECER:**
1. **Permissões** - Verificar se usuário é profissional/paciente
2. **Tokens** - Usar token JWT correto nas chamadas
3. **IDs** - Obter ID do paciente logado corretamente
4. **Validações** - Campos obrigatórios na criação
5. **Tratamento de erros** - Conexão, API, etc.

### **COMO TESTAR:**
1. **Criar tarefa como profissional**
2. **Fazer login como paciente**
3. **Verificar se tarefa aparece na lista**
4. **Clicar no RadioButton**
5. **Verificar se pontos são atualizados**

---

## 🎯 **RESULTADO ESPERADO**

### **Tela do Profissional:**
- Botão "Criar Tarefa" (não "Criar Exercício")
- Dialog com campos específicos para tarefa
- Sem upload de imagem/vídeo

### **Tela do Paciente:**
- Lista mista: exercícios + tarefas
- Tarefas com RadioButton para marcar
- Feedback visual ao completar

### **Funcionalidades:**
- ✅ Criar tarefas
- ✅ Listar tarefas
- ✅ Marcar conclusão
- ✅ Sistema de pontos
- ✅ Ranking atualizado

---

## 🚨 **SE ENCONTRAR ERROS**

### **Problema comum:** Não compila
**Solução:** Verificar imports, dependências, IDs dos layouts

### **Problema comum:** API não funciona
**Solução:** Verificar URL, token, headers

### **Problema comum:** Layout não aparece
**Solução:** Verificar IDs, setContentView(), inflater

### **Problema comum:** RadioButton não funciona
**Solução:** Verificar listener, adapter, notifyDataSetChanged()

---

## 📞 **SUPORTE E DEBUG**

### **Logs para verificar:**
```java
Log.d("TASK_DEBUG", "Criando tarefa: " + title);
Log.d("API_DEBUG", "Chamando API: " + url);
Log.d("USER_DEBUG", "Token: " + token);
```

### **Testes de API:**
```bash
# Testar backend
curl -H "Authorization: Bearer TOKEN" \
     http://localhost:8080/tasks/patient/1

curl -H "Authorization: Bearer TOKEN" \
     http://localhost:8080/tasks/leaderboard
```

---

## ✅ **CHECKLIST FINAL**

Antes de finalizar, verifique:

- [ ] Botão "Criar Tarefa" aparece na tela do profissional
- [ ] Dialog de criação funciona sem imagem/vídeo
- [ ] Tarefas aparecem na lista do paciente
- [ ] RadioButton funciona para marcar conclusão
- [ ] Pontos são atualizados após conclusão
- [ ] Ranking mostra dados reais
- [ ] Login do paciente funciona
- [ ] Sem crashes ou erros

---

## 🎉 **PRONTO PARA USO!**

Seguindo este guia detalhado, o Gemini terá todas as informações necessárias para implementar o sistema de tarefas e pontos corretamente, integrando com as telas existentes sem criar novas activities desnecessárias.

**Lembre-se: Integrar, não substituir! 🎯**
