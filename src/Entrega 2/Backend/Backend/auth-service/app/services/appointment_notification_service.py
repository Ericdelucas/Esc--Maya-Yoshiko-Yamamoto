from datetime import datetime, timedelta
from app.core.notification_client import schedule_notification


class AppointmentNotificationService:
    
    def schedule_appointment_reminders(self, appointment_id: int, professional_id: int, patient_id: int, 
                                     title: str, appointment_date: datetime):
        """Agendar notificações para consulta"""
        
        try:
            # Notificação 1 dia antes (9:00 AM)
            day_before = appointment_date - timedelta(days=1, hours=appointment_date.hour - 9)
            if day_before.hour < 9:
                day_before = day_before.replace(hour=9, minute=0)
            
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
            
            # Notificação para o profissional (1 hora antes)
            schedule_notification(
                user_id=professional_id,
                channel="push",
                title=f"👨‍⚕️ Consulta Agendada",
                message=f"Paciente agendado para {appointment_date.strftime('%H:%M')}: {title}",
                schedule_at_iso=hour_before.isoformat()
            )
            
            return True
            
        except Exception as e:
            print(f"❌ Erro ao agendar notificações: {e}")
            return False
    
    def send_daily_appointment_summary(self, db_session, target_date: datetime = None):
        """Enviar resumo diário de consultas"""
        
        if target_date is None:
            target_date = datetime.now().date()
        
        try:
            from app.storage.database.appointment_repository import AppointmentRepository
            
            # Buscar consultas do dia
            start_datetime = datetime.combine(target_date, datetime.min.time())
            end_datetime = datetime.combine(target_date, datetime.max.time())
            
            appointments = AppointmentRepository(db_session).get_by_date_range(start_datetime, end_datetime)
            
            if not appointments:
                return 0
            
            # Agrupar por paciente
            patient_appointments = {}
            professional_appointments = {}
            
            for appointment in appointments:
                # Agrupar por paciente
                if appointment.patient_id not in patient_appointments:
                    patient_appointments[appointment.patient_id] = []
                patient_appointments[appointment.patient_id].append(appointment)
                
                # Agrupar por profissional
                if appointment.professional_id not in professional_appointments:
                    professional_appointments[appointment.professional_id] = []
                professional_appointments[appointment.professional_id].append(appointment)
            
            # Enviar notificações para pacientes
            for patient_id, patient_appts in patient_appointments.items():
                count = len(patient_appts)
                first_time = patient_appts[0].appointment_date.strftime('%H:%M')
                
                schedule_notification(
                    user_id=patient_id,
                    channel="push",
                    title=f"📅 Suas Consultas de Hoje",
                    message=f"Você tem {count} consulta(s) hoje. Primeira às {first_time}",
                    schedule_at_iso=datetime.now().isoformat()
                )
            
            # Enviar notificações para profissionais
            for professional_id, prof_appts in professional_appointments.items():
                count = len(prof_appts)
                first_time = prof_appts[0].appointment_date.strftime('%H:%M')
                
                schedule_notification(
                    user_id=professional_id,
                    channel="push",
                    title=f"👨‍⚕️ Agenda de Hoje",
                    message=f"Você tem {count} consulta(s) hoje. Primeira às {first_time}",
                    schedule_at_iso=datetime.now().isoformat()
                )
            
            return len(patient_appointments) + len(professional_appointments)
            
        except Exception as e:
            print(f"❌ Erro ao enviar resumo diário: {e}")
            return 0
