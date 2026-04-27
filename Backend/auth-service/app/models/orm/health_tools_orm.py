from sqlalchemy import Integer, String, Text, DateTime, Float, JSON, func
from sqlalchemy.orm import Mapped, mapped_column
from . import Base


class HealthToolsORM(Base):
    __tablename__ = "health_tools"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    user_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    record_type: Mapped[str] = mapped_column(String(50), nullable=False, index=True)  # bmi, body_fat, questionnaire
    value: Mapped[JSON] = mapped_column(JSON, nullable=False)  # Armazena dados estruturados
    record_date: Mapped[DateTime] = mapped_column(DateTime, nullable=False, index=True)
    created_at: Mapped[DateTime] = mapped_column(DateTime, server_default=func.now(), index=True)


class HealthQuestionnaireORM(Base):
    __tablename__ = "health_questionnaires"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    user_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    questionnaire_date: Mapped[DateTime] = mapped_column(DateTime, nullable=False, index=True)
    total_score: Mapped[int] = mapped_column(Integer, nullable=False)
    max_score: Mapped[int] = mapped_column(Integer, nullable=False)
    risk_level: Mapped[str] = mapped_column(String(20), nullable=False)  # Baixo, Médio, Alto
    answers: Mapped[JSON] = mapped_column(JSON, nullable=False)  # Respostas em JSON
    created_at: Mapped[DateTime] = mapped_column(DateTime, server_default=func.now(), index=True)
