from fastapi import FastAPI
from app.routers.health_router import router as health_router
from app.routers.auth_router import router as auth_router
from app.core.config import settings
import uvicorn


def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-auth", version="1.0.0")
    app.include_router(health_router)
    app.include_router(auth_router, prefix="/auth", tags=["auth"])
    return app


app = create_app()

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=settings.auth_port, reload=False)
