from fastapi import FastAPI
from app.routers.health_router import router as health_router
from app.routers.training_router import router as training_router
from app.routers.me_router import router as me_router
from app.routers.exercises_router import router as exercises_router
import uvicorn


def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-training", version="1.0.0")
    app.include_router(health_router)
    app.include_router(training_router, prefix="/training", tags=["training"])
    app.include_router(me_router, prefix="/training", tags=["training"])
    app.include_router(exercises_router)
    return app


app = create_app()

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8030, reload=False)
