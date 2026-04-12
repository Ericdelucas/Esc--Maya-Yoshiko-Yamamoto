"""
Serviço de notificações do SmartSaúde.
Este módulo configura e inicia o serviço de notificações usando FastAPI.
"""

from fastapi import FastAPI
from app.routers.health_router import router as health_router
from app.routers.notification_router import router as notification_router
from app.routers.dispatch_router import router as dispatch_router
from app.routers.notification_query_router import router as notification_query_router


def create_app() -> FastAPI:
    """
    Cria e configura a aplicação FastAPI para o serviço de notificações.

    Returns:
        FastAPI: Instância configurada da aplicação.
    """
    app = FastAPI(title="smartsaude-notification", version="1.0.0")
    app.include_router(health_router)
    app.include_router(notification_router, prefix="/notifications", tags=["notifications"])
    app.include_router(dispatch_router, prefix="/notifications", tags=["notifications"])
    app.include_router(notification_query_router, prefix="/notifications", tags=["notifications"])
    return app


app = create_app()
