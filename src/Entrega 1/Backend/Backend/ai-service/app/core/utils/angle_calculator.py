from __future__ import annotations

from typing import Tuple

import numpy as np

Point2D = Tuple[float, float]


def calculate_angle(a: Point2D, b: Point2D, c: Point2D) -> float:
    """
    Angle ABC in degrees, where points are 2D normalized coords (x,y).
    Uses stable arctan2-based method.
    """
    a_v = np.array(a, dtype=np.float32)
    b_v = np.array(b, dtype=np.float32)
    c_v = np.array(c, dtype=np.float32)

    ba = a_v - b_v
    bc = c_v - b_v

    if _is_near_zero(ba) or _is_near_zero(bc):
        return 0.0

    ang = np.degrees(
        np.arctan2(_cross_2d(ba, bc), np.dot(ba, bc))
    )
    ang = abs(float(ang))
    return 360.0 - ang if ang > 180.0 else ang


def _cross_2d(u: np.ndarray, v: np.ndarray) -> float:
    return float(u[0] * v[1] - u[1] * v[0])


def _is_near_zero(v: np.ndarray, eps: float = 1e-8) -> bool:
    return float(np.linalg.norm(v)) < eps
