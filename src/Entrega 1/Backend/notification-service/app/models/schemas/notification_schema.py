from pydantic import BaseModel, Field


class NotificationCreate(BaseModel):
    user_id: int = Field(ge=1)
    channel: str = Field(min_length=2, max_length=32)  # email, push, whatsapp (futuro)
    title: str = Field(min_length=1, max_length=120)
    message: str = Field(min_length=1, max_length=2000)
    schedule_at_iso: str | None = None  # ISO string (agendamento futuro)


class NotificationOut(BaseModel):
    status: str
    notification_id: str
