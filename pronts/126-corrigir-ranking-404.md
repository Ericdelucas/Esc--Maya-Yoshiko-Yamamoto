# 🚨 **CORRIGIR RANKING 404 - SOLUÇÃO RÁPIDA**

## ⚠️ **PROBLEMA IDENTIFICADO**

### **O que está acontecendo:**
- ✅ **`/tasks/user-points`** funciona (200 OK)
- ❌ **`/leaderboard`** dá 404 Not Found
- ❌ **Ranking não aparece** na tela

### **Causa do Problema:**

O `LeaderboardActivity` está chamando a URL errada:

```java
// ❌ ERRADO - ExerciseApi chama "/leaderboard"
ExerciseApi api = ApiClient.getTaskClient().create(ExerciseApi.class);
api.getLeaderboard("Bearer " + token); // Chama "/leaderboard" ❌

// ✅ CORRETO - TaskApi chama "/tasks/leaderboard"
TaskApi api = ApiClient.getTaskClient().create(TaskApi.class);
api.getLeaderboard("Bearer " + token, 50); // Chama "/tasks/leaderboard" ✅
```

---

## 🔧 **SOLUÇÃO IMEDIATA**

### **Mudar LeaderboardActivity.java:**

```java
// MUDAR ISTO:
private void fetchLeaderboard() {
    if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    
    ExerciseApi api = ApiClient.getTaskClient().create(ExerciseApi.class);
    api.getLeaderboard("Bearer " + token).enqueue(new Callback<List<LeaderboardEntry>>() {
        // ...
    });
}

// PARA ISTO:
private void fetchLeaderboard() {
    if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    
    TaskApi api = ApiClient.getTaskClient().create(TaskApi.class);
    api.getLeaderboard("Bearer " + token, 50).enqueue(new Callback<List<LeaderboardEntry>>() {
        @Override
        public void onResponse(Call<List<LeaderboardEntry>> call, Response<List<LeaderboardEntry>> response) {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            
            if (response.isSuccessful() && response.body() != null) {
                entries.clear();
                entries.addAll(response.body());
                adapter.notifyDataSetChanged();
                
                Log.d(TAG, "Ranking carregado: " + response.body().size() + " entradas");
            } else {
                Toast.makeText(LeaderboardActivity.this, "Erro ao carregar ranking", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Erro: " + response.code());
            }
        }
        
        @Override
        public void onFailure(Call<List<LeaderboardEntry>> call, Throwable t) {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            Toast.makeText(LeaderboardActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Falha", t);
        }
    });
}
```

### **Adicionar import:**

```java
import com.example.testbackend.network.TaskApi;
```

---

## 🎯 **RESULTADO ESPERADO**

### **Após a correção:**

1. **Logs mudam de:**
   ```
   GET /leaderboard HTTP/1.1" 404 Not Found ❌
   ```
   
2. **Para:**
   ```
   GET /tasks/leaderboard?limit=50 HTTP/1.1" 200 OK ✅
   ```

3. **Ranking aparece com:**
   ```
   🥇 #1 Paciente Teste - 130 pontos
   🥈 #2 Paciente Secundário - 50 pontos  
   🥉 #3 Profissional - 25 pontos
   ```

---

## 📋 **CHECKLIST RÁPIDO**

### **Mudar em LeaderboardActivity.java:**

- [ ] **Importar TaskApi** - `import com.example.testbackend.network.TaskApi;`
- [ ] **Mudar API** - `ExerciseApi` → `TaskApi`
- [ ] **Mudar chamada** - `api.getLeaderboard("Bearer " + token, 50)`
- [ ] **Testar** - Abrir tela de ranking

### **Verificar nos logs:**

- [ ] **Deve mostrar** `GET /tasks/leaderboard?limit=50 HTTP/1.1" 200 OK`
- [ ] **Não deve mais mostrar** `GET /leaderboard HTTP/1.1" 404 Not Found`

---

## 🚀 **SOLUÇÃO DEFINITIVA**

**É só mudar 3 linhas no LeaderboardActivity.java:**

1. **Import:** `import com.example.testbackend.network.TaskApi;`
2. **API:** `TaskApi api = ApiClient.getTaskClient().create(TaskApi.class);`
3. **Chamada:** `api.getLeaderboard("Bearer " + token, 50)`

**O ranking vai aparecer imediatamente! 🎯**
