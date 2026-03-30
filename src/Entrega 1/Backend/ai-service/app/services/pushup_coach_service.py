from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, List, Optional, Tuple


@dataclass(frozen=True)
class CoachResult:
    level: str  # OK|WARN|URGENT
    instruction: str
    reasons: List[str]


class PushupCoachService:
    def coach(
        self,
        joint_angles: Dict[str, float],
        ideal_angles: Dict[str, Dict[str, float]],
        mapping: Dict[str, List[str]],
    ) -> CoachResult:
        elbow = self._avg(joint_angles, mapping.get("elbow", []))
        hip = self._avg(joint_angles, mapping.get("hip", []))

        reasons: List[str] = []
        level = "OK"
        instruction = "OK. Mantenha o ritmo."

        if elbow is not None:
            e_min, e_max = self._rng(ideal_angles, "elbow", 70.0, 170.0)
            if elbow > e_max:
                level, instruction = "WARN", "Desça mais."
                reasons.append(f"Elbow alto ({elbow}° > {e_max}°).")
            elif elbow < e_min:
                level, instruction = "WARN", "Suba um pouco."
                reasons.append(f"Elbow baixo ({elbow}° < {e_min}°).")

        if hip is not None:
            h_min, h_max = self._rng(ideal_angles, "hip", 150.0, 180.0)
            if hip < h_min:
                level, instruction = "URGENT", "Alinhe o corpo: contraia o abdômen e eleve o quadril."
                reasons.append(f"Hip desalinhado ({hip}° < {h_min}°).")
            elif hip > h_max and level != "URGENT":
                level, instruction = "WARN", "Evite arquear: abaixe levemente o quadril."
                reasons.append(f"Hip alto ({hip}° > {h_max}°).")

        return CoachResult(level=level, instruction=instruction, reasons=reasons)

    @staticmethod
    def _avg(angles: Dict[str, float], keys: List[str]) -> Optional[float]:
        vals = [angles[k] for k in keys if k in angles]
        return (sum(vals) / len(vals)) if vals else None

    @staticmethod
    def _rng(src: Dict[str, Dict[str, float]], key: str, dmin: float, dmax: float) -> Tuple[float, float]:
        if key not in src:
            return dmin, dmax
        return float(src[key]["min"]), float(src[key]["max"])
