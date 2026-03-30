from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, Any, Optional

import httpx


@dataclass(frozen=True)
class TrainingClientConfig:
    base_url: str = "http://training-service:8030"
    timeout_s: float = 5.0


class TrainingClient:
    def __init__(self, cfg: TrainingClientConfig = TrainingClientConfig()) -> None:
        self._cfg = cfg

    async def get_ideal_angles(self, exercise_id: int, token: str) -> Dict[str, Any]:
        url = f"{self._cfg.base_url}/training/exercises/{exercise_id}/ideal-angles"
        headers = {"Authorization": f"Bearer {token}"}

        async with httpx.AsyncClient(timeout=self._cfg.timeout_s) as client:
            r = await client.get(url, headers=headers)

        if r.status_code == 404:
            return {"exercise_id": exercise_id, "ideal_angles": {}}

        r.raise_for_status()
        return r.json()


def extract_bearer_token(auth_header: Optional[str]) -> str:
    if not auth_header:
        return ""
    parts = auth_header.split(" ", 1)
    return parts[1].strip() if len(parts) == 2 else ""
