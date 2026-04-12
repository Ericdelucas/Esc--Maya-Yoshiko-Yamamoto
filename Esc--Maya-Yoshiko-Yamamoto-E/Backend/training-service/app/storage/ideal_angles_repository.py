from __future__ import annotations

from typing import Dict, Optional

from app.models.ideal_angles_models import IdealAnglesResponse


class IdealAnglesRepository:
    """
    Baseline hardcoded repository - future: migrate to MySQL
    """
    
    _BASELINE_DATA = {
        1: {  # Alongamento Lombar
            "knee": {"min": 160.0, "max": 180.0},
            "shoulder": {"min": 90.0, "max": 180.0},
            "elbow": {"min": 160.0, "max": 180.0},
            "hip": {"min": 45.0, "max": 90.0}
        },
        2: {  # Agachamento
            "knee": {"min": 90.0, "max": 120.0},
            "shoulder": {"min": 45.0, "max": 180.0},
            "elbow": {"min": 160.0, "max": 180.0},
            "hip": {"min": 45.0, "max": 90.0}
        },
        3: {  # Flexão de braço
            "knee": {"min": 160.0, "max": 180.0},
            "shoulder": {"min": 45.0, "max": 90.0},
            "elbow": {"min": 45.0, "max": 90.0},
            "hip": {"min": 160.0, "max": 180.0}
        }
    }
    
    def get_by_exercise_id(self, exercise_id: int) -> Optional[IdealAnglesResponse]:
        data = self._BASELINE_DATA.get(exercise_id)
        if not data:
            return None
            
        return IdealAnglesResponse(
            exercise_id=exercise_id,
            ideal_angles=data
        )
