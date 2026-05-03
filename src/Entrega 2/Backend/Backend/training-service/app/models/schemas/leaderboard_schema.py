from pydantic import BaseModel
from typing import List, Optional


class LeaderboardEntry(BaseModel):
    """Entrada do ranking"""
    position: int
    name: str
    points: int
    is_current_user: Optional[bool] = False


class LeaderboardResponse(BaseModel):
    """Lista do ranking"""
    rankings: List[LeaderboardEntry]
    total_users: int
