from sqlalchemy.orm import Session
from sqlalchemy import and_, or_
from datetime import datetime, timedelta
from app.models.orm.appointment_orm import AppointmentORM
from app.storage.database.base_repository import SessionLocal

class AppointmentRepository:
    
    def create(self, title: str, description: str, appointment_date: datetime, 
                 time: str, professional_id: int, patient_id: int = None, 
                 status: str = "scheduled") -> dict:
        """Cria novo agendamento"""
        with SessionLocal() as session:
            appointment = AppointmentORM(
                title=title,
                description=description,
                appointment_date=appointment_date,
                time=time,
                professional_id=professional_id,
                patient_id=patient_id,
                status=status
            )
            session.add(appointment)
            session.commit()
            session.refresh(appointment)
            
            return {
                "id": appointment.id,
                "title": appointment.title,
                "description": appointment.description,
                "appointment_date": appointment.appointment_date,
                "time": appointment.time,
                "professional_id": appointment.professional_id,
                "patient_id": appointment.patient_id,
                "status": appointment.status
            }
    
    def get_by_professional_and_month(self, professional_id: int, year: int, month: int) -> list:
        """Busca agendamentos de um profissional por mês"""
        with SessionLocal() as session:
            start_date = datetime(year, month, 1)
            if month == 12:
                end_date = datetime(year + 1, 1, 1) - timedelta(days=1)
            else:
                end_date = datetime(year, month + 1, 1)
            
            appointments = session.query(AppointmentORM).filter(
                and_(
                    AppointmentORM.professional_id == professional_id,
                    AppointmentORM.appointment_date >= start_date,
                    AppointmentORM.appointment_date < end_date,
                    AppointmentORM.status.in_(["scheduled", "completed"])
                )
            ).order_by(AppointmentORM.appointment_date, AppointmentORM.time).all()
            
            return [
                {
                    "id": apt.id,
                    "title": apt.title,
                    "description": apt.description,
                    "appointment_date": apt.appointment_date,
                    "time": apt.time,
                    "status": apt.status
                }
                for apt in appointments
            ]
    
    def get_by_date(self, professional_id: int, date: datetime) -> list:
        """Busca agendamentos de um profissional em uma data específica"""
        with SessionLocal() as session:
            start_date = date.replace(hour=0, minute=0, second=0, microsecond=0)
            end_date = date.replace(hour=23, minute=59, second=59, microsecond=999999)
            
            appointments = session.query(AppointmentORM).filter(
                and_(
                    AppointmentORM.professional_id == professional_id,
                    AppointmentORM.appointment_date >= start_date,
                    AppointmentORM.appointment_date <= end_date,
                    AppointmentORM.status.in_(["scheduled", "completed"])
                )
            ).order_by(AppointmentORM.time).all()
            
            return [
                {
                    "id": apt.id,
                    "title": apt.title,
                    "description": apt.description,
                    "appointment_date": apt.appointment_date,
                    "time": apt.time,
                    "status": apt.status
                }
                for apt in appointments
            ]
    
    def update_status(self, appointment_id: int, status: str) -> bool:
        """Atualiza status do agendamento"""
        with SessionLocal() as session:
            appointment = session.query(AppointmentORM).filter(
                AppointmentORM.id == appointment_id
            ).first()
            
            if not appointment:
                return False
            
            appointment.status = status
            appointment.updated_at = datetime.utcnow()
            session.commit()
            return True
    
    def delete(self, appointment_id: int) -> bool:
        """Exclui agendamento"""
        with SessionLocal() as session:
            appointment = session.query(AppointmentORM).filter(
                AppointmentORM.id == appointment_id
            ).first()
            
            if not appointment:
                return False
            
            session.delete(appointment)
            session.commit()
            return True
