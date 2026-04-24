# 🎯 **GUIA ESPECÍFICO - CORRIGIR RANKING (PASSO A PASSO)**

## 📁 **ARQUIVOS QUE PRECISA MODIFICAR**

### **✅ ARQUIVOS JÁ CORRIGIDOS (NÃO PRECISA MEXER):**
- ✅ `LeaderboardEntry.java` - Já tem campo `is_real_user`
- ✅ `LeaderboardActivity.java` - Já usa `TaskApi` corretamente
- ✅ `TaskApi.java` - Já tem endpoint `getLeaderboard`

### **⚠️ ARQUIVOS QUE PRECISA MELHORAR:**

---

## 🔧 **PASSO 1: MELHORAR LeaderboardAdapter.java**

### **📍 Arquivo:** `/front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/adapters/LeaderboardAdapter.java`

### **🔄 SUBSTITUA O MÉTODO `onBindViewHolder` (LINHAS 32-47):**

```java
@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    LeaderboardEntry entry = entries.get(position);
    
    // 🔥 **DADOS CORRETOS**
    holder.position.setText("#" + entry.getPosition());
    holder.name.setText(entry.getName());
    holder.points.setText(String.valueOf(entry.getPoints()) + " pts");
    
    // 🏆 **MEDALHAS PARA TOP 3**
    switch (entry.getPosition()) {
        case 1:
            if (entry.isRealUser()) {
                holder.position.setText("👑");  // Coroa para usuário real
                holder.position.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                holder.position.setText("🥇");
                holder.position.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
            }
            break;
        case 2:
            holder.position.setText("🥈");
            holder.position.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            break;
        case 3:
            holder.position.setText("🥉");
            holder.position.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_light));
            break;
        default:
            holder.position.setText("#" + entry.getPosition());
            holder.position.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_secondary));
    }
    
    // 🔥 **DESTACAR USUÁRIO REAL**
    if (entry.isRealUser()) {
        holder.name.setText(entry.getName() + " (Você)");
        holder.name.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.primary));
        holder.name.setTypeface(null, android.graphics.Typeface.BOLD);
        
        // Background especial para usuário real
        holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.primary_light));
    } else {
        holder.name.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_main));
        holder.name.setTypeface(null, android.graphics.Typeface.NORMAL);
        holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
    }
}
```

---

## 🔧 **PASSO 2: MELHORAR item_leaderboard.xml**

### **📍 Arquivo:** `/front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/item_leaderboard.xml`

### **🔄 SUBSTITUA TODO O CONTEÚDO:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="8dp"
    app:cardCornerRadius="12dp"
    app:cardElevation="3dp"
    app:cardBackgroundColor="@android:color/white"
    app:strokeWidth="1dp"
    app:strokeColor="@color/border_soft">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center_vertical"
        android:orientation="horizontal"
        android:padding="16dp">

        <!-- Posição com Medalha -->
        <TextView
            android:id="@+id/text_position"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:gravity="center"
            android:text="#1"
            android:textSize="20sp"
            android:textStyle="bold"
            android:textColor="@color/primary"
            android:background="@drawable/circle_background"
            android:layout_marginEnd="16dp" />

        <!-- Info do Usuário -->
        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">

            <TextView
                android:id="@+id/text_name"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Nome do Usuário"
                android:textColor="@color/text_main"
                android:textSize="16sp"
                android:textStyle="bold" />

            <TextView
                android:id="@+id/text_tasks"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Tarefas concluídas: 5"
                android:textColor="@color/text_secondary"
                android:textSize="12sp"
                android:visibility="gone" /> <!-- Escondido por enquanto -->

        </LinearLayout>

        <!-- Pontos -->
        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:gravity="end">

            <TextView
                android:id="@+id/text_points"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="134"
                android:textColor="@color/primary"
                android:textSize="18sp"
                android:textStyle="bold" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="pontos"
                android:textColor="@color/text_secondary"
                android:textSize="12sp" />

        </LinearLayout>

    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

---

## 🔧 **PASSO 3: CRIAR circle_background.xml**

### **📍 Arquivo:** `/front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/drawable/circle_background.xml`

### **🔄 CRIE ESTE ARQUIVO (SE NÃO EXISTIR):**

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    
    <solid android:color="@color/primary_light" />
    <stroke 
        android:width="2dp"
        android:color="@color/primary" />
</shape>
```

---

## 🔧 **PASSO 4: VERIFICAR LeaderboardActivity.java**

### **📍 Arquivo:** `/front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/LeaderboardActivity.java`

### **✅ VERIFIQUE SE A LINHA 80 ESTÁ ASSIM:**

```java
api.getLeaderboard("Bearer " + token).enqueue(new Callback<List<LeaderboardEntry>>() {
```

### **🔥 SE ESTIVER ASSIM, ESTÁ CORRETO! NÃO PRECISA MEXER.**

---

## 🎯 **RESULTADO ESPERADO**

### **📱 DEPOIS DAS MUDANÇAS, O RANKING VAI MOSTRAR:**

```
👑 Paciente Teste (Você)     134 pontos
   Tarefas concluídas: 6

🥈 Dr. Silva Bot            120 pontos
   Tarefas concluídas: 5

🥉 Ana Bot                  95 pontos
   Tarefas concluídas: 4

#4 Carlos Bot               80 pontos
   Tarefas concluídas: 3

#5 Maria Bot                65 pontos
   Tarefas concluídas: 2
```

### **🌟 DIFERENÇAIS VISUAIS:**

- 👑 **Coroa para usuário real** em 1º lugar
- 🎯 **"(Você)"** ao lado do nome do usuário real
- 🌟 **Background azul claro** para usuário real
- 🥇🥈🥉 **Medalhas** para top 3
- 📱 **Cards modernos** com cantos arredondados
- ⭕ **Círculo na posição** com fundo colorido

---

## 📋 **CHECKLIST FINAL**

### **✅ O QUE PRECISA FAZER:**

- [ ] **LeaderboardAdapter.java** - Substituir método `onBindViewHolder`
- [ ] **item_leaderboard.xml** - Substituir layout completo
- [ ] **circle_background.xml** - Criar arquivo drawable (se não existir)

### **🧪 TESTAR:**

1. **Abrir ranking** - Deve mostrar "Paciente Teste (Você)"
2. **Verificar medalhas** - 👑🥈🥉 para top 3
3. **Verificar destaque** - Background azul para usuário real
4. **Verificar pontos** - "134 pts" formato correto

---

## 🚀 **INSTRUÇÕES PARA O GEMINI**

### **📋 COPIE E COLE EXATAMENTE:**

1. **Abra** `LeaderboardAdapter.java`
2. **Substitua** as linhas 32-47 com o código do Passo 1
3. **Abra** `item_leaderboard.xml`
4. **Substitua** todo o conteúdo com o código do Passo 2
5. **Crie** `circle_background.xml` com o código do Passo 3 (se não existir)
6. **Teste** o ranking

### **🎯 RESULTADO:**

**O ranking vai ficar profissional com usuário real destacado! 🚀**
