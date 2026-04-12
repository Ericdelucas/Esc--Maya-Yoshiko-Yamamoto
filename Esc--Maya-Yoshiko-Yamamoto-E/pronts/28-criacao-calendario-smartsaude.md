# 📅 CRIAÇÃO DE CALENDÁRIO - SMARTSAUDE

## 🎯 **OBJETIVO**

Criar um calendário para agendamentos que aparece ao clicar no botão "Agenda" no ProfessionalMainActivity.

## 📱 **Onde Implementar**

### **Localização:**
- **Activity:** `ProfessionalMainActivity.java`
- **Botão:** `btnCalendar` (já existe)
- **Ação:** Abrir nova `CalendarActivity`

## 🗓️ **Componente CalendarActivity**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/CalendarActivity.java`

```java
public class CalendarActivity extends AppCompatActivity {
    
    private static final String TAG = "CALENDAR_DEBUG";
    private TokenManager tokenManager;
    private RecyclerView rvCalendar;
    private CalendarAdapter calendarAdapter;
    private TextView tvMonthYear;
    private ImageButton btnPrevMonth, btnNextMonth;
    private FloatingActionButton fabAddAppointment;
    
    private List<Appointment> appointments = new ArrayList<>();
    private Calendar currentCalendar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        
        tokenManager = new TokenManager(this);
        currentCalendar = Calendar.getInstance();
        
        setupViews();
        setupCalendar();
        loadAppointments();
    }
    
    private void setupViews() {
        tvMonthYear = findViewById(R.id.tvMonthYear);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        rvCalendar = findViewById(R.id.rvCalendar);
        fabAddAppointment = findViewById(R.id.fabAddAppointment);
        
        btnPrevMonth.setOnClickListener(v -> previousMonth());
        btnNextMonth.setOnClickListener(v -> nextMonth());
        fabAddAppointment.setOnClickListener(v -> openAddAppointmentDialog());
    }
    
    private void setupCalendar() {
        calendarAdapter = new CalendarAdapter(this, appointments, this::onDateClick);
        rvCalendar.setLayoutManager(new GridLayoutManager(this, 7)); // 7 dias da semana
        rvCalendar.setAdapter(calendarAdapter);
        
        updateMonthYear();
    }
    
    private void loadAppointments() {
        // Buscar agendamentos da API
        String token = tokenManager.getAuthToken();
        if (token != null) {
            // TODO: Implementar chamada à API para buscar agendamentos
            // Por enquanto, dados mock para teste
            appointments = generateMockAppointments();
            calendarAdapter.updateAppointments(appointments);
        }
    }
    
    private void previousMonth() {
        currentCalendar.add(Calendar.MONTH, -1);
        updateMonthYear();
        calendarAdapter.updateCalendar(currentCalendar);
    }
    
    private void nextMonth() {
        currentCalendar.add(Calendar.MONTH, 1);
        updateMonthYear();
        calendarAdapter.updateCalendar(currentCalendar);
    }
    
    private void updateMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("pt", "BR"));
        tvMonthYear.setText(sdf.format(currentCalendar.getTime()));
    }
    
    private void onDateClick(int day, int month, int year) {
        // Abrir diálogo para adicionar/ver agendamentos do dia
        openDayAppointmentsDialog(day, month, year);
    }
    
    private void openAddAppointmentDialog() {
        // Dialog para criar novo agendamento
        AddAppointmentDialog dialog = new AddAppointmentDialog(this, appointment -> {
            // Salvar na API
            saveAppointment(appointment);
        });
        dialog.show();
    }
    
    private void openDayAppointmentsDialog(int day, int month, int year) {
        // Dialog para mostrar agendamentos do dia
        DayAppointmentsDialog dialog = new DayAppointmentsDialog(
            this, day, month, year, appointments
        );
        dialog.show();
    }
    
    private void saveAppointment(Appointment appointment) {
        // TODO: Implementar salvamento na API
        appointments.add(appointment);
        calendarAdapter.updateAppointments(appointments);
    }
    
    private List<Appointment> generateMockAppointments() {
        List<Appointment> mock = new ArrayList<>();
        
        // Adicionar alguns agendamentos de exemplo
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 15);
        mock.add(new Appointment(
            1, "Consulta Dr. Silva", cal.getTime(), "Consulta de rotina"
        ));
        
        cal.set(Calendar.DAY_OF_MONTH, 20);
        mock.add(new Appointment(
            2, "Sessão Fisioterapia", cal.getTime(), "Sessão de reabilitação"
        ));
        
        return mock;
    }
}
```

## 🎨 **Layout XML**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/res/layout/activity_calendar.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <!-- Header do Calendário -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical"
        android:layout_marginBottom="16dp">

        <ImageButton
            android:id="@+id/btnPrevMonth"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:src="@drawable/ic_chevron_left"
            android:background="?attr/selectableItemBackgroundBorderless" />

        <TextView
            android:id="@+id/tvMonthYear"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Janeiro 2024"
            android:textSize="18sp"
            android:textStyle="bold"
            android:gravity="center" />

        <ImageButton
            android:id="@+id/btnNextMonth"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:src="@drawable/ic_chevron_right"
            android:background="?attr/selectableItemBackgroundBorderless" />

    </LinearLayout>

    <!-- Calendário -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvCalendar"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <!-- FAB para adicionar agendamento -->
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabAddAppointment"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="16dp"
        android:src="@drawable/ic_add"
        android:contentDescription="Adicionar Agendamento" />

</LinearLayout>
```

## 📋 **CalendarAdapter**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/CalendarAdapter.java`

```java
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    
    private Context context;
    private List<Appointment> appointments;
    private OnDateClickListener onDateClickListener;
    private Calendar currentCalendar;
    private List<Integer> daysInMonth;
    
    public interface OnDateClickListener {
        void onDateClick(int day, int month, int year);
    }
    
    public CalendarAdapter(Context context, List<Appointment> appointments, OnDateClickListener listener) {
        this.context = context;
        this.appointments = appointments;
        this.onDateClickListener = listener;
        this.currentCalendar = Calendar.getInstance();
        generateDaysInMonth();
    }
    
    public void updateCalendar(Calendar calendar) {
        this.currentCalendar = calendar;
        generateDaysInMonth();
        notifyDataSetChanged();
    }
    
    public void updateAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }
    
    private void generateDaysInMonth() {
        daysInMonth = new ArrayList<>();
        
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Domingo
        int daysInMonthCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Adicionar dias vazios antes do primeiro dia
        for (int i = 0; i < firstDayOfWeek; i++) {
            daysInMonth.add(0);
        }
        
        // Adicionar dias do mês
        for (int i = 1; i <= daysInMonthCount; i++) {
            daysInMonth.add(i);
        }
    }
    
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        int day = daysInMonth.get(position);
        
        if (day == 0) {
            holder.tvDay.setText("");
            holder.itemView.setClickable(false);
            return;
        }
        
        holder.tvDay.setText(String.valueOf(day));
        
        // Verificar se tem agendamentos neste dia
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        
        boolean hasAppointments = hasAppointmentsOnDay(day, month, year);
        holder.indicator.setVisibility(hasAppointments ? View.VISIBLE : View.GONE);
        
        holder.itemView.setOnClickListener(v -> {
            if (day != 0) {
                onDateClickListener.onDateClick(day, month, year);
            }
        });
    }
    
    private boolean hasAppointmentsOnDay(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0, 0);
        
        Calendar endCal = Calendar.getInstance();
        endCal.set(year, month, day, 23, 59, 59, 999);
        
        for (Appointment appointment : appointments) {
            Calendar appCal = Calendar.getInstance();
            appCal.setTime(appointment.getDate());
            
            if (appCal.getTimeInMillis() >= cal.getTimeInMillis() && 
                appCal.getTimeInMillis() <= endCal.getTimeInMillis()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int getItemCount() {
        return daysInMonth.size();
    }
    
    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        View indicator;
        
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            indicator = itemView.findViewById(R.id.indicator);
        }
    }
}
```

## 📄 **Model Appointment**

### **Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/models/Appointment.java`

```java
public class Appointment {
    private int id;
    private String title;
    private Date date;
    private String description;
    
    public Appointment(int id, String title, Date date, String description) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
```

## 🔄 **Modificar ProfessionalMainActivity**

### **No método onCreate(), adicionar:**
```java
btnCalendar = findViewById(R.id.btnCalendar);
btnCalendar.setOnClickListener(v -> {
    Intent intent = new Intent(ProfessionalMainActivity.this, CalendarActivity.class);
    startActivity(intent);
});
```

## 🧪 **COMO TESTAR**

1. **Clique no botão "Agenda"** no ProfessionalMainActivity
2. **Deve abrir o CalendarActivity** com o calendário do mês atual
3. **Navegue entre os meses** usando as setas
4. **Clique em um dia** para ver/adicionar agendamentos
5. **Use o FAB** para adicionar novos agendamentos

---

**Status:** 🔄 **AGUARDANDO IMPLEMENTAÇÃO DO CALENDÁRIO**
