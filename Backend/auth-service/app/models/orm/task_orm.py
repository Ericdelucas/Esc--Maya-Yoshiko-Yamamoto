from sqlalchemy import Integer, String, Text, Date, DateTime, Boolean, func
from sqlalchemy.orm import Mapped, mapped_column
from . import Base


class TaskORM(Base):
    __tablename__ = "tasks"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    professional_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    patient_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    title: Mapped[str] = mapped_column(String(120), nullable=False)
    description: Mapped[str] = mapped_column(Text, nullable=False)
    points_value: Mapped[int] = mapped_column(Integer, nullable=False, default=10)
    exercise_id: Mapped[int | None] = mapped_column(Integer, nullable=True, index=True)
    frequency_per_week: Mapped[int] = mapped_column(Integer, nullable=False, default=1)
    is_active: Mapped[bool] = mapped_column(Boolean, nullable=False, default=True)
    start_date: Mapped[Date] = mapped_column(Date, nullable=False)
    end_date: Mapped[Date | None] = mapped_column(Date, nullable=True)
    exercise_image_url: Mapped[str | None] = mapped_column(Text, nullable=True)
    exercise_video_url: Mapped[str | None] = mapped_column(Text, nullable=True)
    created_at: Mapped[DateTime] = mapped_column(DateTime, server_default=func.now())
    updated_at: Mapped[DateTime | None] = mapped_column(DateTime, nullable=True)


class TaskCompletionORM(Base):
    __tablename__ = "task_completions"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    task_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    patient_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    completed_at: Mapped[DateTime] = mapped_column(DateTime, server_default=func.now())
    points_earned: Mapped[int] = mapped_column(Integer, nullable=False)
    completion_notes: Mapped[str | None] = mapped_column(String(512), nullable=True)
    verified_by_professional: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)
    verified_at: Mapped[DateTime | None] = mapped_column(DateTime, nullable=True)


class UserPointsORM(Base):
    __tablename__ = "user_points"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    user_id: Mapped[int] = mapped_column(Integer, nullable=False, unique=True, index=True)
    total_points: Mapped[int] = mapped_column(Integer, nullable=False, default=0)
    weekly_points: Mapped[int] = mapped_column(Integer, nullable=False, default=0)
    monthly_points: Mapped[int] = mapped_column(Integer, nullable=False, default=0)
    current_streak: Mapped[int] = mapped_column(Integer, nullable=False, default=0)
    longest_streak: Mapped[int] = mapped_column(Integer, nullable=False, default=0)
    last_completion_date: Mapped[Date | None] = mapped_column(Date, nullable=True)
    rank_position: Mapped[int | None] = mapped_column(Integer, nullable=True)
    updated_at: Mapped[DateTime] = mapped_column(DateTime, server_default=func.now())


class PointsHistoryORM(Base):
    __tablename__ = "points_history"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    user_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    points_change: Mapped[int] = mapped_column(Integer, nullable=False)
    change_type: Mapped[str] = mapped_column(String(32), nullable=False, index=True)
    reference_id: Mapped[int | None] = mapped_column(Integer, nullable=True)
    description: Mapped[str] = mapped_column(String(255), nullable=False)
    created_at: Mapped[DateTime] = mapped_column(DateTime, server_default=func.now())


class GlobalChallengeORM(Base):
    __tablename__ = "global_challenges"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    title: Mapped[str] = mapped_column(String(120), nullable=False)
    description: Mapped[str] = mapped_column(Text, nullable=False)
    points_reward: Mapped[int] = mapped_column(Integer, nullable=False, default=100)
    requirement_type: Mapped[str] = mapped_column(String(64), nullable=False)
    requirement_value: Mapped[int] = mapped_column(Integer, nullable=False)
    start_date: Mapped[Date] = mapped_column(Date, nullable=False)
    end_date: Mapped[Date] = mapped_column(Date, nullable=False)
    is_active: Mapped[bool] = mapped_column(Boolean, nullable=False, default=True)
    created_by: Mapped[int] = mapped_column(Integer, nullable=False)
    created_at: Mapped[DateTime] = mapped_column(DateTime, server_default=func.now())


class ChallengeParticipationORM(Base):
    __tablename__ = "challenge_participations"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    challenge_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    user_id: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    current_progress: Mapped[int] = mapped_column(Integer, nullable=False, default=0)
    is_completed: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)
    completed_at: Mapped[DateTime | None] = mapped_column(DateTime, nullable=True)
    reward_claimed: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)
    claimed_at: Mapped[DateTime | None] = mapped_column(DateTime, nullable=True)
    joined_at: Mapped[DateTime] = mapped_column(DateTime, server_default=func.now())
