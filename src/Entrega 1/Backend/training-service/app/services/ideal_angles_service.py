from __future__ import annotations

from typing import Optional

from app.models.ideal_angles_models import IdealAnglesResponse
from app.storage.ideal_angles_repository import IdealAnglesRepository


class IdealAnglesService:
    def __init__(self) -> None:
        self._repo = IdealAnglesRepository()
    
    def get_ideal_angles(self, exercise_id: int) -> Optional[IdealAnglesResponse]:
        return self._repo.get_by_exercise_id(exercise_id)
