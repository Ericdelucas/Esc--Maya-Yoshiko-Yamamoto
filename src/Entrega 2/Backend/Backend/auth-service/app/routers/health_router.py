"""Health check router para auth-service."""

from fastapi import APIRouter

router = APIRouter(tags=["health"])


@router.get("/health")
def health_check() -> dict:
    """Health check endpoint."""
    return {"status": "ok"}
