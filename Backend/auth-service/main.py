import uvicorn
from fastapi import FastAPI

from app.core.config import get_settings
from app.core.error_handler import register_error_handlers
from app.routers.auth_router import router as auth_router


def create_app() -> FastAPI:
    app = FastAPI(title="SmartSaúde Auth Service", version="0.0.1")

    register_error_handlers(app)

    app.include_router(auth_router, tags=["auth"])
    return app


app = create_app()

if __name__ == "__main__":
    settings = get_settings()
    uvicorn.run("main:app", host="0.0.0.0", port=settings.auth_port, reload=False)
