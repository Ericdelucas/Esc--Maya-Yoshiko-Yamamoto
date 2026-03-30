from pydantic import BaseModel, Field
from typing import List, Optional
from datetime import date
from enum import Enum


class ChallengeStatus(str, Enum):
    active = "active"
    completed = "completed"
    expired = "expired"


class ChallengeResponse(BaseModel):
    """Desafio com progresso do paciente"""
    id: int
    title: str
    description: str
    reward_points: int
    start_date: date
    end_date: date
    joined: bool
    progress: int
    target_sessions: int
    status: ChallengeStatus
    
    class Config:
        from_attributes = True
