from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, Tuple

from app.core.utils.angle_calculator import calculate_angle

Point2D = Tuple[float, float]


@dataclass(frozen=True)
class MovementAnalysis:
    joint_angles: Dict[str, float]
    detected: bool


class MovementValidator:
    """
    Converts pose landmarks into joint angles (degrees).
    This does NOT compare with ideal ranges yet (Phase 3 integration step).
    """

    def analyze(self, landmarks: Dict[str, Point2D]) -> MovementAnalysis:
        if not landmarks:
            return MovementAnalysis(joint_angles={}, detected=False)

        a: Dict[str, float] = {}

        # Knees: hip-knee-ankle
        self._add_angle(a, "left_knee", landmarks, "left_hip", "left_knee", "left_ankle")
        self._add_angle(a, "right_knee", landmarks, "right_hip", "right_knee", "right_ankle")

        # Hips: shoulder-hip-knee (proxy)
        self._add_angle(a, "left_hip", landmarks, "left_shoulder", "left_hip", "left_knee")
        self._add_angle(a, "right_hip", landmarks, "right_shoulder", "right_hip", "right_knee")

        # Elbows: shoulder-elbow-wrist
        self._add_angle(a, "left_elbow", landmarks, "left_shoulder", "left_elbow", "left_wrist")
        self._add_angle(a, "right_elbow", landmarks, "right_shoulder", "right_elbow", "right_wrist")

        # Shoulders: elbow-shoulder-hip (proxy)
        self._add_angle(a, "left_shoulder", landmarks, "left_elbow", "left_shoulder", "left_hip")
        self._add_angle(a, "right_shoulder", landmarks, "right_elbow", "right_shoulder", "right_hip")

        return MovementAnalysis(joint_angles=a, detected=True)

    @staticmethod
    def _add_angle(
        out: Dict[str, float],
        key: str,
        lm: Dict[str, Point2D],
        p1: str,
        p2: str,
        p3: str,
    ) -> None:
        if p1 in lm and p2 in lm and p3 in lm:
            out[key] = round(calculate_angle(lm[p1], lm[p2], lm[p3]), 2)
