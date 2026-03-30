from sqlalchemy.orm import Session
from app.storage.database.notification_repository import NotificationRepository


class DispatcherService:
    def dispatch_once(self, db: Session) -> dict:
        repo = NotificationRepository(db)
        due = repo.next_due(limit=20)

        sent = 0
        failed = 0

        for n in due:
            try:
                # Entrega MVP real: loga envio, marca como sent
                print(f"[NOTIFICATION] to={n.user_id} channel={n.channel} title={n.title} msg={n.message}")
                repo.mark_sent(n.id)
                sent += 1
            except Exception:
                repo.mark_failed(n.id)
                failed += 1

        return {"sent": sent, "failed": failed, "checked": len(due)}
