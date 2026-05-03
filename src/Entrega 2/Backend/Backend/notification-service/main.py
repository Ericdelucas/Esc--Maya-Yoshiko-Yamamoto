from fastapi import FastAPI
from app.routers.health_router import router as health_router
from app.routers.notification_router import router as notification_router
from app.routers.dispatch_router import router as dispatch_router
from app.routers.notification_query_router import router as notification_query_router


def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-notification", version="1.0.0")
    app.include_router(health_router)
    app.include_router(notification_router, prefix="/notifications", tags=["notifications"])
    app.include_router(dispatch_router, prefix="/notifications", tags=["notifications"])
    app.include_router(notification_query_router, prefix="/notifications", tags=["notifications"])
    return app


app = create_app()
