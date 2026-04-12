from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, List, Tuple


@dataclass(frozen=True)
class AngleRange:
    min: float
    max: float


@dataclass(frozen=True)
class AngleFeedback:
    ok: bool
    messages: List[str]
    deviations: Dict[str, float]


class AngleFeedbackService:
    def build_feedback(
        self,
        joint_angles: Dict[str, float],
        ideal_angles: Dict[str, Dict[str, float]],
        mapping: Dict[str, List[str]],
    ) -> AngleFeedback:
        msgs: List[str] = []
        devs: Dict[str, float] = {}
        ok = True

        for ideal_key, joints in mapping.items():
            rng = self._get_range(ideal_angles, ideal_key)
            if not rng:
                continue

            measured = self._avg_present(joint_angles, joints)
            if measured is None:
                ok = False
                msgs.append(f"Não consegui medir ângulo para {ideal_key}.")
                continue

            if measured < rng.min:
                ok = False
                devs[ideal_key] = round(rng.min - measured, 2)
                msgs.append(f"{ideal_key}: aumente o ângulo (~+{devs[ideal_key]}°).")
            elif measured > rng.max:
                ok = False
                devs[ideal_key] = round(measured - rng.max, 2)
                msgs.append(f"{ideal_key}: reduza o ângulo (~-{devs[ideal_key]}°).")
            else:
                msgs.append(f"{ideal_key}: OK.")

        return AngleFeedback(ok=ok, messages=msgs, deviations=devs)

    @staticmethod
    def _get_range(src: Dict[str, Dict[str, float]], key: str) -> AngleRange | None:
        if key not in src:
            return None
        return AngleRange(min=float(src[key]["min"]), max=float(src[key]["max"]))

    @staticmethod
    def _avg_present(joint_angles: Dict[str, float], keys: List[str]) -> float | None:
        vals = [joint_angles[k] for k in keys if k in joint_angles]
        if not vals:
            return None
        return sum(vals) / float(len(vals))
