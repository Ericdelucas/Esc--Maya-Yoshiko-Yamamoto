from dataclasses import dataclass
from typing import Optional


@dataclass(frozen=True)
class User:
    id: int
    email: str
    password_hash: str
    role: str
    full_name: Optional[str] = None
    profile_photo_url: Optional[str] = None
