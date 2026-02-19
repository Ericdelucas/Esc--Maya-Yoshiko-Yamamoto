from fastapi import Header, HTTPException, status
import requests
from app.core.config import settings


def get_token(authorization: str | None) -> str:
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Missing token")
    return authorization.split(" ", 1)[1].strip()


def verify_token_and_role(authorization: str | None, allowed_roles: list[str]) -> dict:
    token = get_token(authorization)
    try:
        resp = requests.get(
            f"{settings.auth_base_url}/auth/verify",
            headers={"Authorization": f"Bearer {token}"},
            timeout=5,
        )
        if resp.status_code != 200:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")

        payload = resp.json()
        # Handle the actual response format from auth-service
        if "claims" in payload:
            claims = payload["claims"]
        else:
            claims = payload
            
        role = claims.get("role")
        if role not in allowed_roles:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Insufficient permissions")
        return claims
    except HTTPException:
        raise
    except Exception:
        raise HTTPException(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, detail="Auth service unavailable")
