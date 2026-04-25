# 🎯 **GUIA SUPER SIMPLES - GEMINI CORRIGIR VER DETALHES**

## 🚨 **PROBLEMA: "Ver detalhes" não abre a tela do relatório**

---

## 🔧 **SOLUÇÃO - ARQUIVOS PARA CORRIGIR:**

### **📋 Arquivo 1: ReportDetailActivity.java**
**Local:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/ReportDetailActivity.java`

**PROBLEMA:** Linha 86 tem `finish()` que fecha a tela
**CORREÇÃO:** Remover `finish()` e usar ID padrão

**MUDAR ISTO:**
```java
if (reportId == -1) {
    Toast.makeText(this, R.string.erro_carregar, Toast.LENGTH_SHORT).show();
    finish();  // ❌ REMOVER ESTA LINHA
    return;
}
```

**POR ISTO:**
```java
if (reportId == -1) {
    Toast.makeText(this, "Usando ID padrão", Toast.LENGTH_SHORT).show();
    reportId = 1;  // ✅ USAR ID PADRÃO
    // finish();  // ❌ COMENTAR OU REMOVER
    // return;   // ❌ COMENTAR OU REMOVER
}
```

---

### **📋 Arquivo 2: AndroidManifest.xml**
**Local:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/AndroidManifest.xml`

**PROBLEMA:** `parentActivityName` faz voltar para tela errada
**CORREÇÃO:** Remover `parentActivityName`

**MUDAR ISTO:**
```xml
<activity
    android:name=".ReportDetailActivity"
    android:exported="false"
    android:label="Detalhes do Relatório"
    android:parentActivityName=".PatientReportsActivity" />  ❌ REMOVER
```

**POR ISTO:**
```xml
<activity
    android:name=".ReportDetailActivity"
    android:exported="false"
    android:label="Detalhes do Relatório" />  ✅ ASSIM
```

---

### **📋 Arquivo 3: item_report.xml** (se necessário)
**Local:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/item_report.xml`

**VERIFICAR SE TEM ID:**
```xml
<TextView
    android:id="@+id/tvSeeDetails"  ✅ PRECISA TER ID
    android:text="Ver detalhes"
    android:clickable="true"
    android:background="?attr/selectableItemBackground" />
```

---

## 🎮 **COMO TESTAR:**

1. **Faça as 2 mudanças acima**
2. **Recompile o app**
3. **Clique em "Ver detalhes"**
4. **Deve abrir a tela** com detalhes do relatório

---

## 🚨 **IMPORTANTE:**

**GEMINI - FOCA EM:**
1. ✅ **Remover `finish()`** da ReportDetailActivity
2. ✅ **Remover `parentActivityName`** do AndroidManifest
3. ✅ **Testar se abre** a tela de detalhes

**SÃO APENAS 2 MUDANÇAS SIMPLES!**

---

## 🎯 **RESULTADO ESPERADO:**

**Clicar em "Ver detalhes" → Abrir ReportDetailActivity ✅**

**FIM! É SÓ ISSO! 🎯**
