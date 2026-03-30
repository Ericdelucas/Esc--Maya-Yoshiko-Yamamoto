from sqlalchemy import Integer, String, DateTime, func
from sqlalchemy.orm import Mapped, mapped_column
from app.storage.database.base import Base


class TrainingPlanItemORM(Base):
    __tablename__ = "training_plan_items"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    plan_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    exercise_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    sets: Mapped[int] = mapped_column(Integer, nullable=False, default=0)
    reps: Mapped[int] = mapped_column(Integer, nullable=False, default=0)
    frequency_per_week: Mapped[int] = mapped_column(Integer, nullable=False, default=0)
    notes: Mapped[str | None] = mapped_column(String(512), nullable=True)
    created_at: Mapped[DateTime] = mapped_column(DateTime, server_default=func.now())
