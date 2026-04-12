from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, Optional


@dataclass(frozen=True)
class IdealAnglesResponse:
    exercise_id: int
    ideal_angles: Dict[str, Dict[str, float]]


@dataclass(frozen=True)
class IdealAngleRange:
    min: float
    max: float


@dataclass(frozen=True)
class IdealAngles:
    exercise_id: int
    knee: Optional[IdealAngleRange] = None
    shoulder: Optional[IdealAngleRange] = None
    elbow: Optional[IdealAngleRange] = None
    hip: Optional[IdealAngleRange] = None
