from fastapi import HTTPException, status
import requests
from app.core.config import settings


def _token_from_auth_header(authorization: str | None) -> str:
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Missing token")
    return authorization.split(" ", 1)[1].strip()


def verify_token_and_role(authorization: str | None, allowed_roles: list[str]) -> dict:
    token = _token_from_auth_header(authorization)
    try:
        resp = requests.get(
            f"{settings.auth_base_url}/auth/verify",
            headers={"Authorization": f"Bearer {token}"},
            timeout=5,
        )
        if resp.status_code != 200:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")

        payload = resp.json()
        role = payload.get("role")
        if role not in allowed_roles:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Insufficient permissions")
        return payload
    except HTTPException:
        raise
    except Exception:
        raise HTTPException(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, detail="Auth service unavailable")
