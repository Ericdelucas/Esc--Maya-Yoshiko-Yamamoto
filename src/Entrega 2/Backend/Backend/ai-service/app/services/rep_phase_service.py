from __future__ import annotations

from dataclasses import dataclass
from typing import Optional


@dataclass(frozen=True)
class PhaseResult:
    phase: str  # DOWN|UP|HOLD
    elbow_avg: Optional[float]


class RepPhaseService:
    def __init__(self, down_threshold: float = 110.0, up_threshold: float = 155.0) -> None:
        self._down_th = down_threshold
        self._up_th = up_threshold
        self._last_phase = "HOLD"

    def update(self, elbow_avg: Optional[float]) -> PhaseResult:
        if elbow_avg is None:
            return PhaseResult(phase="HOLD", elbow_avg=None)

        # hysteresis: only switch when crossing thresholds
        if self._last_phase in ("HOLD", "UP") and elbow_avg < self._down_th:
            self._last_phase = "DOWN"
        elif self._last_phase in ("HOLD", "DOWN") and elbow_avg > self._up_th:
            self._last_phase = "UP"

        return PhaseResult(phase=self._last_phase, elbow_avg=elbow_avg)
