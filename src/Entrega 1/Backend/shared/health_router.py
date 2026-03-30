"""Health check router padrão para todos os serviços."""

from fastapi import APIRouter

router = APIRouter(tags=["health"])


@router.get("/health")
def health_check() -> dict:
    """Health check endpoint padrão."""
    return {"status": "ok"}
