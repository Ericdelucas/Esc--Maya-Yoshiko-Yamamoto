from datetime import date
from sqlalchemy.orm import Session
from app.models.schemas.training_schema import (
    TrainingPlanCreate, TrainingPlanOut,
    TrainingPlanItemCreate,
    TrainingLogCreate, TrainingLogOut,
)
from app.storage.database.training_plan_repository import TrainingPlanRepository
from app.storage.database.training_plan_item_repository import TrainingPlanItemRepository
from app.storage.database.training_log_repository import TrainingLogRepository
from app.core.notification_client import schedule_notification
from app.services.reminder_scheduler_service import ReminderSchedulerService


def _parse_date(value: str) -> date:
    y, m, d = value.split("-")
    return date(int(y), int(m), int(d))


class TrainingService:
    def create_plan(self, payload: TrainingPlanCreate, db: Session) -> TrainingPlanOut:
        start = _parse_date(payload.start_date_iso)
        end = _parse_date(payload.end_date_iso) if payload.end_date_iso else None
        row = TrainingPlanRepository(db).create(payload.patient_id, payload.professional_id, payload.title, start, end)
        
        # Envia notificação para o paciente
        schedule_notification(
            user_id=payload.patient_id,
            title="Novo plano de exercícios",
            message=f"Plano '{payload.title}' criado. Comece em {payload.start_date_iso}.",
            schedule_at_iso=None,
        )
        
        return TrainingPlanOut(
            id=row.id, patient_id=row.patient_id, professional_id=row.professional_id,
            title=row.title, start_date_iso=row.start_date.isoformat(),
            end_date_iso=row.end_date.isoformat() if row.end_date else None,
            created_at_iso=row.created_at.isoformat(),
        )

    def add_item(self, plan_id: int, payload: TrainingPlanItemCreate, db: Session) -> dict:
        row = TrainingPlanItemRepository(db).add_item(plan_id, payload.exercise_id, payload.sets, payload.reps, payload.frequency_per_week, payload.notes)
        
        # Agenda lembretes para o exercício
        plan = TrainingPlanRepository(db).get_by_id(plan_id)
        if plan:
            created = ReminderSchedulerService().schedule_for_plan_item(
                patient_id=plan.patient_id,
                plan_title=plan.title,
                exercise_id=payload.exercise_id,
                frequency_per_week=payload.frequency_per_week,
                days_ahead=14,
            )
        
        return {"status": "ok", "item_id": row.id, "reminders_created": created}

    def list_plans_by_patient(self, patient_id: int, db: Session) -> list[TrainingPlanOut]:
        rows = TrainingPlanRepository(db).list_by_patient(patient_id)
        return [
            TrainingPlanOut(
                id=r.id, patient_id=r.patient_id, professional_id=r.professional_id,
                title=r.title, start_date_iso=r.start_date.isoformat(),
                end_date_iso=r.end_date.isoformat() if r.end_date else None,
                created_at_iso=r.created_at.isoformat(),
            )
            for r in rows
        ]

    def create_log(self, payload: TrainingLogCreate, db: Session) -> TrainingLogOut:
        row = TrainingLogRepository(db).create(payload.patient_id, payload.plan_id, payload.exercise_id, payload.perceived_effort, payload.pain_level, payload.notes)
        return TrainingLogOut(
            id=row.id, patient_id=row.patient_id, plan_id=row.plan_id, exercise_id=row.exercise_id,
            performed_at_iso=row.performed_at.isoformat(),
            perceived_effort=row.perceived_effort, pain_level=row.pain_level, notes=row.notes,
        )

    def list_logs_by_patient(self, patient_id: int, db: Session) -> list[TrainingLogOut]:
        rows = TrainingLogRepository(db).list_by_patient(patient_id)
        return [
            TrainingLogOut(
                id=r.id, patient_id=r.patient_id, plan_id=r.plan_id, exercise_id=r.exercise_id,
                performed_at_iso=r.performed_at.isoformat(),
                perceived_effort=r.perceived_effort, pain_level=r.pain_level, notes=r.notes,
            )
            for r in rows
        ]
