"""Health check router para training-service."""

from fastapi import APIRouter

router = APIRouter(tags=["health"])


@router.get("/health")
def health_check() -> dict:
    """Health check endpoint."""
    return {"status": "ok"}
