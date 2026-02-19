from dataclasses import dataclass


@dataclass(frozen=True)
class User:
    id: int
    email: str
    password_hash: str
    role: str
