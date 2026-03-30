from sqlalchemy import Integer, String, Date, DateTime, func
from sqlalchemy.orm import Mapped, mapped_column
from app.storage.database.base import Base


class TrainingPlanORM(Base):
    __tablename__ = "training_plans"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    patient_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    professional_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    title: Mapped[str] = mapped_column(String(120), nullable=False)
    start_date: Mapped[Date] = mapped_column(Date, nullable=False)
    end_date: Mapped[Date | None] = mapped_column(Date, nullable=True)
    created_at: Mapped[DateTime] = mapped_column(DateTime, server_default=func.now())
