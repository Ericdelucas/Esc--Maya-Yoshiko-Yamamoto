# 🎯 **GUIA - SISTEMA DE NOTIFICAÇÕES PARA CALENDÁRIO**

## 📋 **COMO FUNCIONA O SISTEMA ATUAL**

### **🔧 Backend - Notification Service:**

**✅ Já existe e funciona:**
- **Serviço:** `notification-service` (porta 8070)
- **Database:** tabela `notifications`
- **API:** `POST /notifications` para criar
- **Scheduler:** `reminder_scheduler_service.py` já agenda notificações

**Estrutura da notificação:**
```json
{
  "user_id": 1,
  "channel": "push",
  "title": "Lembrete de consulta",
  "message": "Você tem uma consulta hoje às 14:00",
  "schedule_at_iso": "2026-04-25T14:00:00"
}
```

---

## 🎯 **IMPLEMENTAR NOTIFICAÇÕES DE CALENDÁRIO**

### **📋 Passo 1 - Criar serviço de agendamento de consultas**

**Arquivo:** `Backend/auth-service/app/services/appointment_notification_service.py`

```python
from datetime import datetime, timedelta
from app.core.notification_client import schedule_notification

class AppointmentNotificationService:
    
    def schedule_appointment_reminders(self, appointment_id: int, professional_id: int, patient_id: int, 
                                     title: str, appointment_date: datetime):
        """Agendar notificações para consulta"""
        
        # Notificação 1 dia antes
        day_before = appointment_date - timedelta(days=1, hours=9)
        schedule_notification(
            user_id=patient_id,
            channel="push",
            title=f"⏰ Lembrete de Consulta",
            message=f"Você tem uma consulta amanhã às {appointment_date.strftime('%H:%M')}: {title}",
            schedule_at_iso=day_before.isoformat()
        )
        
        # Notificação 1 hora antes
        hour_before = appointment_date - timedelta(hours=1)
        schedule_notification(
            user_id=patient_id,
            channel="push",
            title=f"🩺 Consulta em 1 hora",
            message=f"Sua consulta começa em 1 hora: {title}",
            schedule_at_iso=hour_before.isoformat()
        )
        
        # Notificação para o profissional
        hour_before_prof = appointment_date - timedelta(hours=1)
        schedule_notification(
            user_id=professional_id,
            channel="push",
            title=f"👨‍⚕️ Consulta Agendada",
            message=f"Paciente agendado para {appointment_date.strftime('%H:%M')}: {title}",
            schedule_at_iso=hour_before_prof.isoformat()
        )
        
        return True
```

---

### **📋 Passo 2 - Integrar com criação de consultas**

**Arquivo:** `Backend/auth-service/app/routers/appointment_router.py`

**Adicionar no endpoint de criar consulta:**

```python
from app.services.appointment_notification_service import AppointmentNotificationService

@router.post("/appointments", response_model=AppointmentOut)
def create_appointment(appointment: AppointmentCreate, db: Session = Depends(get_db)):
    # Criar consulta
    new_appointment = AppointmentRepository(db).create(appointment)
    
    # 🔥 AGENDAR NOTIFICAÇÕES
    notification_service = AppointmentNotificationService()
    notification_service.schedule_appointment_reminders(
        appointment_id=new_appointment.id,
        professional_id=appointment.professional_id,
        patient_id=appointment.patient_id,
        title=appointment.title,
        appointment_date=appointment.appointment_date
    )
    
    return AppointmentOut.from_orm(new_appointment)
```

---

### **📋 Passo 3 - Criar job diário para consultas do dia**

**Arquivo:** `Backend/auth-service/app/services/daily_appointment_notifier.py`

```python
from datetime import datetime, time
from app.storage.database.appointment_repository import AppointmentRepository
from app.core.notification_client import schedule_notification

class DailyAppointmentNotifier:
    
    def send_daily_appointments(self, db: Session):
        """Enviar notificações de consultas do dia"""
        
        today = datetime.now().date()
        start_time = datetime.combine(today, time(8, 0))  # 8:00 AM
        
        # Buscar consultas do dia
        appointments = AppointmentRepository(db).get_by_date_range(
            start_date=today,
            end_date=today
        )
        
        for appointment in appointments:
            # Notificação para paciente
            schedule_notification(
                user_id=appointment.patient_id,
                channel="push",
                title=f"📅 Suas Consultas de Hoje",
                message=f"Você tem {len([a for a in appointments if a.patient_id == appointment.patient_id])} consulta(s) hoje",
                schedule_at_iso=start_time.isoformat()
            )
            
            # Notificação para profissional
            schedule_notification(
                user_id=appointment.professional_id,
                channel="push",
                title=f"👨‍⚕️ Agenda de Hoje",
                message=f"Você tem {len([a for a in appointments if a.professional_id == appointment.professional_id])} consulta(s) hoje",
                schedule_at_iso=start_time.isoformat()
            )
```

---

### **📋 Passo 4 - Frontend - Receber notificações**

**Arquivo:** `front/Esc--Maya-Yoshiko-Yamamoto/testbackend/app/src/main/java/com/example/testbackend/services/NotificationService.java`

```java
public class NotificationService {
    
    public static void showAppointmentNotification(Context context, String title, String message) {
        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                "appointments",
                "Consultas e Agendamentos",
                NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "appointments")
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true);
        
        notificationManager.notify(1, builder.build());
    }
}
```

---

### **📋 Passo 5 - Frontend - Activity Principal

**Na MainActivity ou ProfessionalMainActivity:**

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // Verificar notificações de consultas do dia
    checkTodayAppointments();
}

private void checkTodayAppointments() {
    // Chamar API para buscar consultas de hoje
    // Mostrar notificação se houver consultas
}
```

---

## 🎮 **COMO TESTAR**

### **✅ Teste 1 - Criar consulta:**
1. **Criar uma consulta** para amanhã
2. **Verificar se** notificações foram agendadas
3. **Checar tabela** `notifications`

### **✅ Teste 2 - Notificação diária:**
1. **Rodar job** às 8:00 AM
2. **Verificar se** pacientes recebem "Suas Consultas de Hoje"
3. **Verificar se** profissionais recebem "Agenda de Hoje"

### **✅ Teste 3 - Lembretes:**
1. **1 dia antes:** "Lembrete de Consulta"
2. **1 hora antes:** "Consulta em 1 hora"
3. **Profissional:** "Consulta Agendada"

---

## 🚨 **IMPORTANTE**

### **🎯 Pré-requisitos:**
1. ✅ **notification-service** rodando (porta 8070)
2. ✅ **Tabela notifications** criada
3. ✅ **Permissões** no AndroidManifest.xml
4. ✅ **Job scheduler** configurado

### **🎯 Permissões Android:**
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

---

## 🎯 **RESULTADO ESPERADO**

**Paciente recebe:**
- 📅 **8:00 AM:** "Suas Consultas de Hoje"
- ⏰ **1 dia antes:** "Lembrete de Consulta"
- 🩺 **1 hora antes:** "Consulta em 1 hora"

**Profissional recebe:**
- 👨‍⚕️ **8:00 AM:** "Agenda de Hoje"
- 🩺 **1 hora antes:** "Consulta Agendada"

**Sistema completo de notificações para calendário! 🎯**
