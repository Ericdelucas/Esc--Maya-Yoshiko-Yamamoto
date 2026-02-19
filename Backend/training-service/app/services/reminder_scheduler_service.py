from datetime import datetime, timedelta
from app.core.notification_client import schedule_notification


class ReminderSchedulerService:
    def schedule_for_plan_item(
        self,
        patient_id: int,
        plan_title: str,
        exercise_id: int,
        frequency_per_week: int,
        days_ahead: int = 14,
    ) -> int:
        if frequency_per_week <= 0:
            return 0

        # Simple deterministic spread: X reminders per week => interval days
        interval_days = max(1, 7 // min(7, frequency_per_week))
        now = datetime.utcnow().replace(minute=0, second=0, microsecond=0)

        created = 0
        when = now + timedelta(days=1)

        end = now + timedelta(days=days_ahead)
        while when <= end:
            schedule_notification(
                user_id=patient_id,
                title="Lembrete de exercícios",
                message=f"Plano '{plan_title}': execute o exercício #{exercise_id}.",
                schedule_at_iso=when.isoformat(),
            )
            created += 1
            when = when + timedelta(days=interval_days)

        return created
