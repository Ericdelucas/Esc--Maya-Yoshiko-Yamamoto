from __future__ import annotations

from collections import deque
from dataclasses import dataclass
from typing import Deque, Dict


@dataclass(frozen=True)
class SmootherConfig:
    window: int = 5  # frames


class AngleSmoother:
    def __init__(self, cfg: SmootherConfig = SmootherConfig()) -> None:
        self._cfg = cfg
        self._buf: Dict[str, Deque[float]] = {}

    def push(self, angles: Dict[str, float]) -> Dict[str, float]:
        out: Dict[str, float] = {}
        for k, v in angles.items():
            if k not in self._buf:
                self._buf[k] = deque(maxlen=self._cfg.window)
            self._buf[k].append(float(v))
            out[k] = round(sum(self._buf[k]) / len(self._buf[k]), 2)
        return out
