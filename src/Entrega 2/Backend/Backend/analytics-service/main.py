from fastapi import FastAPI
from app.routers.health_router import router as health_router
from app.routers.analytics_router import router as analytics_router


def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-analytics", version="1.0.0")
    app.include_router(health_router)
    app.include_router(analytics_router, prefix="/analytics", tags=["analytics"])
    return app


app = create_app()
