from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, List, Optional

from app.services.pushup_coach_service import PushupCoachService, CoachResult


@dataclass(frozen=True)
class RealtimeCoachResult:
    level: str
    instruction: str
    reasons: List[str]


class RealtimeCoachService:
    def __init__(self) -> None:
        self._pushup = PushupCoachService()

    def coach(
        self,
        phase: str,
        joint_angles: Dict[str, float],
        ideal_angles: Dict[str, Dict[str, float]],
        mapping: Dict[str, List[str]],
    ) -> RealtimeCoachResult:
        base: CoachResult = self._pushup.coach(joint_angles, ideal_angles, mapping)

        # phase-aware instructions
        if phase == "DOWN" and "Suba" in base.instruction:
            return RealtimeCoachResult(level="OK", instruction="Continue descendo.", reasons=base.reasons)

        if phase == "UP" and "Desça" in base.instruction:
            return RealtimeCoachResult(level="OK", instruction="Continue subindo.", reasons=base.reasons)

        return RealtimeCoachResult(level=base.level, instruction=base.instruction, reasons=base.reasons)
