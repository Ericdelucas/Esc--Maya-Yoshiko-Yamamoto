from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import date, datetime


class TaskCreate(BaseModel):
    patient_id: int = Field(..., description="ID do paciente")
    title: str = Field(..., min_length=1, max_length=120, description="Título da tarefa")
    description: str = Field(..., min_length=1, max_length=2000, description="Descrição da tarefa")
    points_value: int = Field(default=10, ge=1, le=1000, description="Pontos da tarefa")
    exercise_id: Optional[int] = Field(None, description="ID do exercício associado (opcional)")
    frequency_per_week: int = Field(default=1, ge=1, le=7, description="Frequência semanal")
    start_date: date = Field(..., description="Data de início")
    end_date: Optional[date] = Field(None, description="Data de fim (opcional)")
    exercise_image_url: Optional[str] = Field(None, description="URL da imagem do exercício (opcional)")
    exercise_video_url: Optional[str] = Field(None, description="URL do vídeo do exercício (opcional)")


class TaskUpdate(BaseModel):
    title: Optional[str] = Field(None, min_length=1, max_length=120)
    description: Optional[str] = Field(None, min_length=1, max_length=2000)
    points_value: Optional[int] = Field(None, ge=1, le=1000)
    exercise_id: Optional[int] = Field(None)
    frequency_per_week: Optional[int] = Field(None, ge=1, le=7)
    is_active: Optional[bool] = Field(None)
    end_date: Optional[date] = Field(None)


class TaskOut(BaseModel):
    id: int
    professional_id: int
    patient_id: int
    title: str
    description: str
    points_value: int
    exercise_id: Optional[int]
    frequency_per_week: int
    is_active: bool
    start_date: date
    end_date: Optional[date]
    exercise_image_url: Optional[str]
    exercise_video_url: Optional[str]
    created_at: datetime
    updated_at: Optional[datetime]

    class Config:
        from_attributes = True


class TaskCompletionCreate(BaseModel):
    task_id: int = Field(..., description="ID da tarefa")
    completion_notes: Optional[str] = Field(None, max_length=512, description="Notas sobre a conclusão")


class TaskCompletionOut(BaseModel):
    id: int
    task_id: int
    patient_id: int
    completed_at: datetime
    points_earned: int
    completion_notes: Optional[str]
    verified_by_professional: bool
    verified_at: Optional[datetime]

    class Config:
        from_attributes = True


class UserPointsOut(BaseModel):
    user_id: int
    total_points: int
    weekly_points: int
    monthly_points: int
    current_streak: int
    longest_streak: int
    last_completion_date: Optional[date]
    rank_position: Optional[int]
    updated_at: datetime

    class Config:
        from_attributes = True


class LeaderboardEntry(BaseModel):
    user_id: int
    user_name: str
    user_email: str
    total_points: int
    rank_position: int
    current_streak: int

    class Config:
        from_attributes = True


class PointsHistoryOut(BaseModel):
    id: int
    user_id: int
    points_change: int
    change_type: str
    reference_id: Optional[int]
    description: str
    created_at: datetime

    class Config:
        from_attributes = True


class GlobalChallengeOut(BaseModel):
    id: int
    title: str
    description: str
    points_reward: int
    requirement_type: str
    requirement_value: int
    start_date: date
    end_date: date
    is_active: bool
    created_by: int
    created_at: datetime

    class Config:
        from_attributes = True


class ChallengeParticipationOut(BaseModel):
    id: int
    challenge_id: int
    user_id: int
    current_progress: int
    is_completed: bool
    completed_at: Optional[datetime]
    reward_claimed: bool
    claimed_at: Optional[datetime]
    joined_at: datetime

    class Config:
        from_attributes = True


class ChallengeParticipationCreate(BaseModel):
    challenge_id: int = Field(..., description="ID do desafio")


class PatientTaskList(BaseModel):
    tasks: List[TaskOut]
    total_points_available: int
    completed_today: int
    weekly_progress: int
    monthly_progress: int


class TaskStats(BaseModel):
    total_tasks: int
    completed_tasks: int
    pending_tasks: int
    total_points_earned: int
    weekly_completion_rate: float
    monthly_completion_rate: float
