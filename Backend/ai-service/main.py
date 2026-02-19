from fastapi import FastAPI
from app.routers.translate_router import router as translate_router


def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-ai", version="1.0.0")
    app.include_router(translate_router, prefix="/ai", tags=["ai"])
    return app


app = create_app()
