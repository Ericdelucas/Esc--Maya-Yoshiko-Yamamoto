# 🔧 CORREÇÕES CALENDÁRIO - DIALOG COM DATA

## 🚨 **PROBLEMAS IDENTIFICADOS**

1. **Dialog não usa o dia clicado** - Salva sempre na data atual
2. **Não mostra a data selecionada** - Usuário não vê qual dia está agendando

## ✅ **SOLUÇÕES**

### **1. Modificar CalendarActivity.java**

**Adicionar variável para data selecionada:**
```java
public class CalendarActivity extends AppCompatActivity {
    // ... variáveis existentes ...
    private Date selectedDate; // 🔥 Guardar data selecionada
    
    // ... métodos existentes ...
    
    private void onDateClick(int day, int month, int year) {
        // 🔥 Guardar a data selecionada
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        selectedDate = cal.getTime();
        
        Log.d(TAG, "📅 Data selecionada: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate));
        
        // Abrir diálogo com a data correta
        DayAppointmentsDialog dialog = new DayAppointmentsDialog(this, day, month, year, appointments);
        dialog.show();
    }
    
    private void openAddAppointmentDialog() {
        // 🔥 Verificar se há data selecionada
        if (selectedDate == null) {
            Toast.makeText(this, "Selecione um dia no calendário primeiro", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AddAppointmentDialog dialog = new AddAppointmentDialog(this, selectedDate, appointment -> {
            // Salvar na API com a data correta
            saveAppointmentToAPI(appointment);
        });
        dialog.show();
    }
}
```

### **2. Modificar AddAppointmentDialog.java**

**Atualizar construtor para receber data:**
```java
public class AddAppointmentDialog extends Dialog {
    
    private Context context;
    private OnAppointmentSavedListener listener;
    private Date selectedDate; // 🔥 Data selecionada
    
    private EditText etTitle, etDescription, etTime, etDate;
    private Button btnSave, btnCancel;
    
    public interface OnAppointmentSavedListener {
        void onAppointmentSaved(Appointment appointment);
    }
    
    // 🔥 NOVO CONSTRUTOR COM DATA
    public AddAppointmentDialog(Context context, Date selectedDate, OnAppointmentSavedListener listener) {
        super(context);
        this.context = context;
        this.selectedDate = selectedDate;
        this.listener = listener;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_appointment);
        
        setupViews();
        setupDateField(); // 🔥 Configurar campo de data
    }
    
    private void setupViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etTime = findViewById(R.id.etTime);
        etDate = findViewById(R.id.etDate); // 🔥 Campo de data
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        
        btnSave.setOnClickListener(v -> saveAppointment());
        btnCancel.setOnClickListener(v -> dismiss());
    }
    
    private void setupDateField() {
        // 🔥 Mostrar a data selecionada no campo
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etDate.setText(sdf.format(selectedDate));
        etDate.setEnabled(false); // Não permitir edição (já está selecionada)
        
        // Adicionar ícone de calendário
        etDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, 0, 0);
    }
    
    private void saveAppointment() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(context, "Digite o título do agendamento", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 🔥 Criar agendamento com a data selecionada
        Appointment appointment = new Appointment(
            (int) System.currentTimeMillis(),
            title,
            selectedDate, // 🔥 Usar data selecionada, não data atual
            description
        );
        
        if (listener != null) {
            listener.onAppointmentSaved(appointment);
        }
        
        dismiss();
        Toast.makeText(context, "Agendamento salvo para " + 
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate), 
            Toast.LENGTH_SHORT).show();
    }
}
```

### **3. Atualizar Layout dialog_add_appointment.xml**

**Adicionar campo de data:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="24dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Novo Agendamento"
        android:textSize="20sp"
        android:textStyle="bold"
        android:gravity="center"
        android:layout_marginBottom="20dp" />

    <!-- 🔥 CAMPO DE DATA -->
    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etDate"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Data do Agendamento"
            android:inputType="date"
            android:focusable="false"
            android:clickable="false" />

    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Título do Agendamento"
            android:inputType="textCapWords" />

    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etDescription"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Descrição (opcional)"
            android:inputType="textMultiLine"
            android:minLines="3" />

    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="20dp"
        style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etTime"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Horário (ex: 14:30)"
            android:inputType="time" />

    </com.google.android.material.textfield.TextInputLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="end">

        <Button
            android:id="@+id/btnCancel"
            style="@style/Widget.MaterialComponents.Button.OutlinedButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginEnd="8dp"
            android:text="Cancelar" />

        <Button
            android:id="@+id/btnSave"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Salvar" />

    </LinearLayout>

</LinearLayout>
```

### **4. Criar ícone de calendário**

**Arquivo:** `front/.../res/drawable/ic_calendar.xml`
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#666666"
        android:pathData="M19,3h-1V1h-2v2H8V1H6v2H5c-1.11,0 -1.99,0.9 -1.99,2L3,19c0,1.1 0.89,2 2,2h14c1.1,0 2,-0.9 2,-2V5c0,-1.1 -0.9,-2 -2,-2zM19,19H5V8h14v11zM7,10h5v5H7z"/>
</vector>
```

### **5. Atualizar saveAppointmentToAPI()**

**Usar data correta:**
```java
private void saveAppointmentToAPI(Appointment appointment) {
    String token = tokenManager.getAuthToken();
    if (token != null) {
        AppointmentApi api = ApiClient.getAuthClient().create(AppointmentApi.class);
        
        AppointmentCreateRequest request = new AppointmentCreateRequest();
        request.setTitle(appointment.getTitle());
        request.setDescription(appointment.getDescription());
        
        // 🔥 Usar data do agendamento, não data atual
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        request.setAppointmentDate(sdf.format(appointment.getDate()));
        request.setTime("14:30");  // Default time
        
        api.createAppointment(token, request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✅ Agendamento salvo no banco para " + 
                        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(appointment.getDate()));
                    Toast.makeText(CalendarActivity.this, "Agendamento salvo", Toast.LENGTH_SHORT).show();
                    loadAppointments();  // Recarregar do banco
                } else {
                    Log.e(TAG, "❌ Erro ao salvar agendamento: " + response.code());
                    Toast.makeText(CalendarActivity.this, "Erro ao salvar agendamento", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "❌ Falha ao salvar", t);
                Toast.makeText(CalendarActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

## 🎯 **RESULTADO ESPERADO**

✅ **Clica no dia 15** → Dialog mostra "15/01/2024"  
✅ **Salva no dia 15** → Agendamento fica marcado no dia correto  
✅ **Visual claro** → Usuário vê qual dia está agendando  
✅ **Feedback** → "Agendamento salvo para 15/01/2024"  

---

**Status:** ✅ **CORREÇÕES PRONTAS - IMPLEMENTAR NO FRONTEND**
