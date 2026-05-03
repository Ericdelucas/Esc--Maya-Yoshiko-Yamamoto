import os
import requests


def _env(name: str, default: str) -> str:
    v = os.getenv(name)
    return v.strip() if v and v.strip() else default


NOTIF_BASE_URL = _env("NOTIF_BASE_URL", "http://notification-service:8070")


def schedule_notification(user_id: int, title: str, message: str, schedule_at_iso: str | None) -> None:
    payload = {
        "user_id": user_id,
        "channel": "push",
        "title": title,
        "message": message,
        "schedule_at_iso": schedule_at_iso,
    }
    requests.post(f"{NOTIF_BASE_URL}/notifications", json=payload, timeout=5)
