from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
from app.routers.health_router import router as health_router
from app.routers.exercise_router import router as exercise_router


def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-exercise", version="1.0.0")
    
    # Expor pasta de storage como arquivos estáticos
    app.mount("/media", StaticFiles(directory="storage"), name="media")
    
    app.include_router(health_router)
    app.include_router(exercise_router, prefix="/exercises", tags=["exercises"])
    return app


app = create_app()
