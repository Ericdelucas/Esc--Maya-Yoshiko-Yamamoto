from typing import Iterable, Set
from fastapi import Depends, HTTPException, status
from app.core.dependencies import get_current_user


def require_roles(allowed: Iterable[str]):
    allowed_set: Set[str] = set(allowed)

    def _guard(user=Depends(get_current_user)):
        role = getattr(user, "role", None)
        if role not in allowed_set:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Insufficient permissions",
            )
        return True

    return _guard
