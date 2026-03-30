from pydantic import BaseModel
from typing import Optional


class ProgressResponse(BaseModel):
    """Dados de progresso do paciente"""
    total_sessions: int
    weekly_sessions: int
    streak_days: int
    progress_percentage: float
    total_points: int
    level: int
    
    class Config:
        from_attributes = True
