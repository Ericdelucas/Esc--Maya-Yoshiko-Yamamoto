# 🎯 INSTRUÇÕES GEMINI - IMPLEMENTAR CALENDÁRIO COMPLETO

## 📋 **RESUMO DA IMPLEMENTAÇÃO**

Criar um calendário funcional para agendamentos que abre ao clicar no botão "Agenda" do ProfessionalMainActivity.

## 🗓️ **ARQUIVOS A CRIAR**

### **1. CalendarActivity.java**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/CalendarActivity.java`

**Copiar o código completo do pront 28-criacao-calendario-smartsaude.md**

### **2. CalendarAdapter.java**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/CalendarAdapter.java`

**Copiar o código completo do pront 28-criacao-calendario-smartsaude.md**

### **3. Appointment.java (Model)**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/models/Appointment.java`

**Copiar o código completo do pront 28-criacao-calendario-smartsaude.md**

### **4. AddAppointmentDialog.java**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/AddAppointmentDialog.java`

**Copiar o código completo do pront 29-layouts-calendario-smartsaude.md**

### **5. DayAppointmentsDialog.java**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/DayAppointmentsDialog.java`

**Copiar o código completo do pront 29-layouts-calendario-smartsaude.md**

### **6. DayAppointmentsAdapter.java**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/DayAppointmentsAdapter.java`

**Copiar o código completo do pront 29-layouts-calendario-smartsaude.md**

## 🎨 **LAYOUTS XML A CRIAR**

### **1. activity_calendar.xml**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/activity_calendar.xml`

**Copiar o código do pront 28-criacao-calendario-smartsaude.md**

### **2. item_calendar_day.xml**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/item_calendar_day.xml`

**Copiar o código do pront 29-layouts-calendario-smartsaude.md**

### **3. dialog_add_appointment.xml**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/dialog_add_appointment.xml`

**Copiar o código do pront 29-layouts-calendario-smartsaude.md**

### **4. dialog_day_appointments.xml**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/dialog_day_appointments.xml`

**Copiar o código do pront 29-layouts-calendario-smartsaude.md**

### **5. item_appointment.xml**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/item_appointment.xml`

**Copiar o código do pront 29-layouts-calendario-smartsaude.md**

## 🎨 **DRAWABLES A CRIAR**

### **1. indicator_dot.xml**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/drawable/indicator_dot.xml`

**Copiar o código do pront 29-layouts-calendario-smartsaude.md**

### **2. appointment_item_bg.xml**
**Caminho:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/drawable/appointment_item_bg.xml`

**Copiar o código do pront 29-layouts-calendario-smartsaude.md**

## 🔄 **MODIFICAÇÃO NECESSÁRIA**

### **ProfessionalMainActivity.java**
**Adicionar no método onCreate() após setupViews():**

```java
btnCalendar = findViewById(R.id.btnCalendar);
btnCalendar.setOnClickListener(v -> {
    Intent intent = new Intent(ProfessionalMainActivity.this, CalendarActivity.class);
    startActivity(intent);
});
```

## 📱 **ADICIONAR NO AndroidManifest.xml**

**Adicionar a nova Activity:**
```xml
<activity
    android:name=".CalendarActivity"
    android:theme="@style/Theme.AppCompat.Light.DarkActionBar"
    android:exported="false" />
```

## 🧪 **COMO TESTAR**

### **1. Compilar e executar**
- Build do projeto sem erros
- Abrir o app

### **2. Testar funcionalidades**
1. **Login como profissional**
2. **Clicar no botão "Agenda"**
3. **Deve abrir o CalendarActivity**
4. **Navegar entre meses** (setas)
5. **Clicar em um dia** → Dialog com agendamentos
6. **Clicar no FAB (+)** → Dialog para adicionar agendamento
7. **Verificar indicadores** nos dias com agendamentos

## 🎯 **FUNCIONALIDADES ESPERADAS**

✅ **Calendário mensal** com navegação  
✅ **Indicadores visuais** nos dias com agendamentos  
✅ **Click no dia** → mostrar agendamentos do dia  
✅ **FAB para adicionar** novos agendamentos  
✅ **Dialog para criar** agendamentos  
✅ **Mock data** para teste inicial  

## 🚨 **IMPORTANTE**

1. **Criar todos os arquivos** exatamente como especificado
2. **Não esquecer os drawables** (indicadores e backgrounds)
3. **Adicionar Activity no Manifest**
4. **Modificar ProfessionalMainActivity** para abrir o calendário
5. **Testar todas as funcionalidades**

## 📊 **RESULTADO ESPERADO**

```
✅ Calendário funcional
✅ Navegação entre meses
✅ Indicadores de agendamentos
✅ Diálogo para adicionar
✅ Diálogo para visualizar do dia
✅ Mock data funcionando
```

---

**Status:** 🔄 **AGUARDANDO IMPLEMENTAÇÃO COMPLETA DO CALENDÁRIO**
