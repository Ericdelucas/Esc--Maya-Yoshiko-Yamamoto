import os
import requests


def _env(name: str, default: str) -> str:
    v = os.getenv(name)
    return v.strip() if v and v.strip() else default


NOTIF_BASE_URL = _env("NOTIF_BASE_URL", "http://notification-service:8070")


def schedule_notification(user_id: int, title: str, message: str, schedule_at_iso: str | None) -> None:
    try:
        payload = {
            "user_id": user_id,
            "channel": "push",
            "title": title,
            "message": message,
            "schedule_at_iso": schedule_at_iso,
        }
        
        print(f"🔔 Enviando notificação: {title} para usuário {user_id}")
        
        response = requests.post(f"{NOTIF_BASE_URL}/notifications", json=payload, timeout=5)
        
        if response.status_code == 200:
            print(f"✅ Notificação agendada com sucesso: {title}")
        else:
            print(f"❌ Erro ao agendar notificação: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ Erro ao conectar com notification-service: {e}")
