from pydantic import BaseModel, Field
from typing import List, Optional
from datetime import date
from enum import Enum


class GoalType(str, Enum):
    weekly_sessions = "weekly_sessions"
    monthly_sessions = "monthly_sessions"
    total_exercises = "total_exercises"
    streak_days = "streak_days"


class GoalStatus(str, Enum):
    active = "active"
    completed = "completed"
    expired = "expired"


class GoalBase(BaseModel):
    goal_type: GoalType
    target_value: int
    deadline: date


class GoalCreate(GoalBase):
    pass


class GoalUpdate(BaseModel):
    target_value: Optional[int] = None
    deadline: Optional[date] = None


class GoalResponse(GoalBase):
    id: int
    current_value: int
    status: GoalStatus
    
    class Config:
        from_attributes = True
