# 🎨 LAYOUTS ADICIONAIS - CALENDÁRIO

## 📱 **Layout Item Calendar Day**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/item_calendar_day.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="8dp"
    android:gravity="center"
    android:background="?attr/selectableItemBackground"
    android:clickable="true"
    android:focusable="true">

    <TextView
        android:id="@+id/tvDay"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="1"
        android:textSize="16sp"
        android:textStyle="bold" />

    <View
        android:id="@+id/indicator"
        android:layout_width="6dp"
        android:layout_height="6dp"
        android:layout_marginTop="4dp"
        android:background="@drawable/indicator_dot"
        android:visibility="gone" />

</LinearLayout>
```

## 🔵 **Indicator Drawable**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/drawable/indicator_dot.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <solid android:color="#2196F3" />
    <size android:width="6dp" android:height="6dp" />
</shape>
```

## 💬 **Dialog Adicionar Agendamento**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/AddAppointmentDialog.java`

```java
public class AddAppointmentDialog extends Dialog {
    
    private Context context;
    private OnAppointmentSavedListener listener;
    
    private EditText etTitle, etDescription, etTime;
    private Button btnSave, btnCancel;
    
    public interface OnAppointmentSavedListener {
        void onAppointmentSaved(Appointment appointment);
    }
    
    public AddAppointmentDialog(Context context, OnAppointmentSavedListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_appointment);
        
        setupViews();
    }
    
    private void setupViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etTime = findViewById(R.id.etTime);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        
        btnSave.setOnClickListener(v -> saveAppointment());
        btnCancel.setOnClickListener(v -> dismiss());
    }
    
    private void saveAppointment() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(context, "Digite o título do agendamento", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Criar agendamento para hoje
        Calendar cal = Calendar.getInstance();
        
        // Se tiver horário, definir
        if (!time.isEmpty()) {
            String[] parts = time.split(":");
            if (parts.length == 2) {
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            }
        }
        
        Appointment appointment = new Appointment(
            (int) System.currentTimeMillis(),
            title,
            cal.getTime(),
            description
        );
        
        if (listener != null) {
            listener.onAppointmentSaved(appointment);
        }
        
        dismiss();
        Toast.makeText(context, "Agendamento salvo", Toast.LENGTH_SHORT).show();
    }
}
```

## 📋 **Layout Dialog Add Appointment**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/dialog_add_appointment.xml`

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

## 📅 **Dialog Agendamentos do Dia**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/DayAppointmentsDialog.java`

```java
public class DayAppointmentsDialog extends Dialog {
    
    private Context context;
    private int day, month, year;
    private List<Appointment> allAppointments;
    
    private RecyclerView rvAppointments;
    private TextView tvDayTitle;
    private DayAppointmentsAdapter adapter;
    
    public DayAppointmentsDialog(Context context, int day, int month, int year, List<Appointment> appointments) {
        super(context);
        this.context = context;
        this.day = day;
        this.month = month;
        this.year = year;
        this.allAppointments = appointments;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_day_appointments);
        
        setupViews();
        loadDayAppointments();
    }
    
    private void setupViews() {
        tvDayTitle = findViewById(R.id.tvDayTitle);
        rvAppointments = findViewById(R.id.rvAppointments);
        
        // Formatar título
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        tvDayTitle.setText(sdf.format(cal.getTime()));
        
        // Setup RecyclerView
        adapter = new DayAppointmentsAdapter(context, new ArrayList<>());
        rvAppointments.setLayoutManager(new LinearLayoutManager(context));
        rvAppointments.setAdapter(adapter);
    }
    
    private void loadDayAppointments() {
        List<Appointment> dayAppointments = new ArrayList<>();
        
        Calendar startCal = Calendar.getInstance();
        startCal.set(year, month, day, 0, 0, 0, 0);
        
        Calendar endCal = Calendar.getInstance();
        endCal.set(year, month, day, 23, 59, 59, 999);
        
        for (Appointment appointment : allAppointments) {
            Calendar appCal = Calendar.getInstance();
            appCal.setTime(appointment.getDate());
            
            if (appCal.getTimeInMillis() >= startCal.getTimeInMillis() && 
                appCal.getTimeInMillis() <= endCal.getTimeInMillis()) {
                dayAppointments.add(appointment);
            }
        }
        
        adapter.updateAppointments(dayAppointments);
        
        if (dayAppointments.isEmpty()) {
            Toast.makeText(context, "Nenhum agendamento neste dia", Toast.LENGTH_SHORT).show();
        }
    }
}
```

## 📋 **Layout Dialog Day Appointments**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/dialog_day_appointments.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="24dp">

    <TextView
        android:id="@+id/tvDayTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="15 de Janeiro de 2024"
        android:textSize="18sp"
        android:textStyle="bold"
        android:gravity="center"
        android:layout_marginBottom="16dp" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvAppointments"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:maxHeight="300dp" />

    <Button
        android:id="@+id/btnClose"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginTop="16dp"
        android:text="Fechar"
        style="@style/Widget.MaterialComponents.Button.OutlinedButton" />

</LinearLayout>
```

## 📄 **DayAppointmentsAdapter**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/DayAppointmentsAdapter.java`

```java
public class DayAppointmentsAdapter extends RecyclerView.Adapter<DayAppointmentsAdapter.AppointmentViewHolder> {
    
    private Context context;
    private List<Appointment> appointments;
    
    public DayAppointmentsAdapter(Context context, List<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;
    }
    
    public void updateAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        
        holder.tvTitle.setText(appointment.getTitle());
        holder.tvDescription.setText(appointment.getDescription());
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("pt", "BR"));
        holder.tvTime.setText(timeFormat.format(appointment.getDate()));
    }
    
    @Override
    public int getItemCount() {
        return appointments.size();
    }
    
    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvTime;
        
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
```

## 📱 **Layout Item Appointment**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/item_appointment.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="12dp"
    android:layout_marginBottom="8dp"
    android:background="@drawable/appointment_item_bg">

    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:orientation="vertical">

        <TextView
            android:id="@+id/tvTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Título do Agendamento"
            android:textSize="16sp"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/tvDescription"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Descrição"
            android:textSize="14sp"
            android:layout_marginTop="4dp"
            android:textColor="#666666" />

    </LinearLayout>

    <TextView
        android:id="@+id/tvTime"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="14:30"
        android:textSize="14sp"
        android:textColor="#2196F3"
        android:textStyle="bold" />

</LinearLayout>
```

## 🎨 **Background Appointment Item**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/drawable/appointment_item_bg.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#F5F5F5" />
    <corners android:radius="8dp" />
    <stroke android:width="1dp" android:color="#E0E0E0" />
</shape>
```

---

**Status:** ✅ **LAYOUTS E COMPONENTES ADICIONAIS CRIADOS**
