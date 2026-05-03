from pydantic import BaseModel
from typing import List, Optional

class DashboardStatsOut(BaseModel):
    """Estatísticas do dashboard para profissionais"""
    total_patients: int
    appointments_today: int
    active_exercises: int
    recent_activities: List[str] = []
