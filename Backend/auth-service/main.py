import uvicorn
from fastapi import FastAPI

from app.core.config import get_settings
from app.core.error_handler import register_error_handlers
from app.routers.auth_router import router as auth_router
from app.routers.me_router import router as me_router
from app.routers.admin_router import router as admin_router
from app.routers.health_router import router as health_router
from app.routers.ai_proxy_router import router as ai_proxy_router


def create_app() -> FastAPI:
    app = FastAPI(title="SmartSaúde Auth Service", version="0.0.1")

    register_error_handlers(app)

    app.include_router(health_router)
    app.include_router(auth_router, tags=["auth"])
    app.include_router(me_router)
    app.include_router(admin_router, prefix="/auth")
    app.include_router(ai_proxy_router)
    return app


app = create_app()

if __name__ == "__main__":
    settings = get_settings()
    uvicorn.run("main:app", host="0.0.0.0", port=settings.auth_port, reload=False)
