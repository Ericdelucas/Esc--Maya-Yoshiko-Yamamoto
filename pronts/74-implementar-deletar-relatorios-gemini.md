# # **IMPLEMENTAR FUNCIONALIDADE DELETAR RELATÓRIOS - PARA O GEMINI**

## # **STATUS ATUAL:**
- # **Backend:** Endpoint DELETE já existe e funciona (HTTP 200)
- # **Frontend:** API já tem endpoint deleteReport
- # **Frontend:** Adapter já tem OnReportLongClickListener
- # **Frontend:** Fragment já tem método onReportLongClick vazio
- # **O que falta:** Implementar a lógica de deleção

## # **TESTE DO BACKEND - CONFIRMADO:**
```bash
# # Endpoint DELETE funcionando:
curl -X DELETE http://localhost:8080/reports/1
# # Resposta: {"message": "Relatório excluído com sucesso"} HTTP 200
```

## # **IMPLEMENTAÇÃO EXIGIDA - PASSO A PASSO:**

### # **1. IMPLEMENTAR DIÁLOGO DE CONFIRMAÇÃO**
```java
// # EM ReportListFragment.java - substituir método onReportLongClick:

private void onReportLongClick(PatientReport report) {
    // # Mostrar diálogo de confirmação
    new androidx.appcompat.app.AlertDialog.Builder(getContext())
        .setTitle("Excluir Relatório")
        .setMessage("Tem certeza que deseja excluir o relatório \"" + report.getTitle() + "\"?")
        .setPositiveButton("Excluir", (dialog, which) -> deleteReport(report))
        .setNegativeButton("Cancelar", null)
        .show();
}
```

### # **2. IMPLEMENTAR MÉTODO DELETE REPORT**
```java
// # ADICIONAR em ReportListFragment.java:

private void deleteReport(PatientReport report) {
    // # Mostrar loading
    if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    
    api.deleteReport(report.getId()).enqueue(new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            
            if (response.isSuccessful()) {
                // # Remover da lista
                int position = reports.indexOf(report);
                if (position != -1) {
                    reports.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, reports.size());
                }
                
                // # Mostrar sucesso
                Toast.makeText(getContext(), "Relatório excluído com sucesso", Toast.LENGTH_SHORT).show();
                
                // # Atualizar empty state
                if (tvEmptyState != null) {
                    tvEmptyState.setVisibility(reports.isEmpty() ? View.VISIBLE : View.GONE);
                }
                
            } else {
                // # Tratar erro específico
                String errorMessage = "Erro ao excluir relatório";
                try {
                    if (response.code() == 404) {
                        errorMessage = "Relatório não encontrado";
                    } else if (response.code() == 403) {
                        errorMessage = "Sem permissão para excluir";
                    } else if (response.errorBody() != null) {
                        errorMessage = "Erro: " + response.errorBody().string();
                    }
                } catch (Exception e) {
                    errorMessage = "Erro ao processar resposta";
                }
                
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }
        
        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            
            String errorMessage = "Erro de conexão: " + t.getMessage();
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    });
}
```

### # **3. ADICIONAR IMPORTS NECESSÁRIOS**
```java
// # ADICIONAR no topo de ReportListFragment.java:
import androidx.appcompat.app.AlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
```

### # **4. MELHORAR LAYOUT - ADICIONAR BOTÃO DELETE (OPCIONAL)**
```xml
<!-- # EM item_report.xml - adicionar após linha 99: -->

<ImageButton
    android:id="@+id/btnDelete"
    android:layout_width="32dp"
    android:layout_height="32dp"
    android:src="@android:drawable/ic_menu_delete"
    android:background="?attr/selectableItemBackgroundBorderless"
    android:contentDescription="Excluir relatório"
    android:tint="#F44336"
    android:visibility="gone"/>
```

### # **5. ATUALIZAR ADAPTER PARA BOTÃO DELETE (OPCIONAL)**
```java
// # EM ReportAdapter.java - adicionar no onBindViewHolder:

// # Adicionar referência ao botão delete
ImageButton btnDelete = holder.itemView.findViewById(R.id.btnDelete);

// # Configurar clique do botão delete
btnDelete.setOnClickListener(v -> {
    if (longClickListener != null) {
        longClickListener.onLongClick(report);
    }
});

// # Mostrar botão delete no long press
holder.itemView.setOnLongClickListener(v -> {
    // # Animar botão delete
    btnDelete.setVisibility(View.VISIBLE);
    return true;
});

// # Esconder botão ao clicar em outro item
holder.itemView.setOnClickListener(v -> {
    btnDelete.setVisibility(View.GONE);
    clickListener.onClick(report);
});
```

### # **6. ADICIONAR ANIMAÇÕES (OPCIONAL)**
```java
// # EM ReportListFragment.java - melhorar deleteReport:

private void deleteReport(PatientReport report) {
    int position = reports.indexOf(report);
    
    // # Animar remoção
    if (position != -1) {
        // # Animação de fade out
        View itemView = recyclerView.getLayoutManager().findViewByPosition(position);
        if (itemView != null) {
            itemView.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> {
                    // # Remover da API
                    deleteFromApi(report, position);
                })
                .start();
        } else {
            // # Fallback direto
            deleteFromApi(report, position);
        }
    }
}

private void deleteFromApi(PatientReport report, int position) {
    api.deleteReport(report.getId()).enqueue(new Callback<Void>() {
        // # ... mesmo código de onResponse e onFailure
    });
}
```

### # **7. MELHORAR TRATAMENTO DE ERROS**
```java
// # EM ReportListFragment.java - adicionar método de logging:

private static final String TAG = "ReportListFragment";

// # Em deleteReport - adicionar logs:
Log.d(TAG, "Excluindo relatório ID: " + report.getId());
Log.d(TAG, "Response code: " + response.code());

if (!response.isSuccessful() && response.errorBody() != null) {
    Log.e(TAG, "Error body: " + response.errorBody().string());
}
```

## # **TESTES PARA REALIZAR:**

### # **1. TESTE BÁSICO:**
```java
// # 1. Long press em um relatório
// # 2. Dialog deve aparecer com título e mensagem
// # 3. Clicar em "Excluir"
// # 4. Loading deve aparecer
// # 5. Relatório deve sumir da lista
// # 6. Toast de sucesso deve aparecer
```

### # **2. TESTE DE ERRO:**
```java
// # 1. Desconectar internet antes de excluir
// # 2. Tentar excluir
// # 3. Toast de erro de conexão deve aparecer
// # 4. Relatório deve permanecer na lista
```

### # **3. TESTE DE EMPTY STATE:**
```java
// # 1. Excluir todos os relatórios
// # 2. Empty state deve aparecer
// # 3. Swipe refresh deve funcionar
```

## # **VERIFICAÇÕES FINAIS:**

### # **1. VERIFICAR API:**
```java
// # EM PatientReportApi.java - confirmar endpoint:
@DELETE("reports/{reportId}")
Call<Void> deleteReport(@Path("reportId") int reportId);
```

### # **2. VERIFICAR BACKEND:**
```bash
# # Testar endpoint diretamente:
curl -X DELETE http://localhost:8080/reports/999
# # Deve retornar 404 se não existir
```

### # **3. VERIFICAR LAYOUT:**
```xml
<!-- # Confirmar que item_report.xml tem todos os IDs necessários:
tvTitle, tvDate, tvType, tvPatient, tvPainScale -->
```

## # **EXPERIÊNCIA DO USUÁRIO ESPERADA:**

### # **Fluxo Ideal:**
1. # **Usuário vê lista** de relatórios
2. # **Long press** em um relatório
3. # **Dialog aparece** com confirmação
4. # **Usuário clica "Excluir"**
5. # **Loading aparece** brevemente
6. # **Relatório some** da lista com animação
7. # **Toast de sucesso** aparece
8. # **Lista atualizada** automaticamente

### # **Tratamento de Erros:**
- # **404:** "Relatório não encontrado"
- # **403:** "Sem permissão para excluir"
- # **Conexão:** "Erro de conexão: [motivo]"
- # **Genérico:** "Erro ao excluir relatório"

## # **COMANDOS PARA TESTE:**

### # **1. COMPILAR E INSTALAR:**
```bash
cd /home/eric-de-lucas/Documentos/GitHub/PI3/back/Esc--Maya-Yoshiko-Yamamoto/front/Esc--Maya-Yoshiko-Yamamoto/testbackend
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### # **2. CONFIGURAR AMBIENTE:**
```bash
adb reverse tcp:8080 tcp:8080
docker ps | grep -E "(mysql|auth)"  # # Verificar backend rodando
```

### # **3. TESTAR FUNCIONALIDADE:**
```bash
# # 1. Criar relatório de teste
curl -X POST http://localhost:8080/reports/ \
  -H "Content-Type: application/json" \
  -d '{"patient_id": 1, "professional_id": 37, "report_date": "2026-04-21T15:00:00", "report_type": "EVOLUTION", "title": "Teste Delete"}'

# # 2. Verificar que aparece no app
# # 3. Long press e excluir
# # 4. Verificar que some da lista
# # 5. Verificar API: curl http://localhost:8080/reports/ | jq .
```

## # **RESUMO DA IMPLEMENTAÇÃO:**

### # **Arquivos a Modificar:**
1. # **ReportListFragment.java** - Implementar deleteReport() e onReportLongClick()
2. # **item_report.xml** - (Opcional) Adicionar botão delete
3. # **ReportAdapter.java** - (Opcional) Suporte para botão delete

### # **Métodos a Implementar:**
- # `deleteReport(PatientReport report)` - Chamada API e tratamento
- # `onReportLongClick(PatientReport report)` - Diálogo de confirmação

### # **Funcionalidades a Adicionar:**
- # **Dialog de confirmação** com título e mensagem
- # **Chamada DELETE** para a API
- # **Remoção animada** da lista
- # **Tratamento de erros** específico
- # **Toast de feedback** para usuário

---

## # **IMPORTANTE PARA O GEMINI:**

**A estrutura já está 90% pronta! Você só precisa:**
1. # **Implementar o diálogo** em `onReportLongClick()`
2. # **Implementar a chamada DELETE** em `deleteReport()`
3. # **Tratar erros** e feedback para usuário
4. # **Testar** o fluxo completo

**O backend já funciona e a API já está configurada. É só implementar a lógica no frontend!**
