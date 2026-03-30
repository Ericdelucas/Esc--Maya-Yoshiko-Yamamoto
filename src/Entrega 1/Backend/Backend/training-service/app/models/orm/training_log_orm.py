from sqlalchemy import Integer, String, DateTime, func
from sqlalchemy.orm import Mapped, mapped_column
from app.storage.database.base import Base


class TrainingLogORM(Base):
    __tablename__ = "training_logs"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    patient_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    plan_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    exercise_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    performed_at: Mapped[DateTime] = mapped_column(DateTime, server_default=func.now())
    perceived_effort: Mapped[int | None] = mapped_column(Integer, nullable=True)
    pain_level: Mapped[int | None] = mapped_column(Integer, nullable=True)
    notes: Mapped[str | None] = mapped_column(String(512), nullable=True)
