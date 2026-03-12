from fastapi import FastAPI
from app.routers.health_router import router as health_router
from app.routers.exercise_router import router as exercise_router
import uvicorn


def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-exercise", version="1.0.0")
    app.include_router(health_router)
    app.include_router(exercise_router, prefix="/exercises", tags=["exercises"])
    return app


app = create_app()

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8082, reload=False)
