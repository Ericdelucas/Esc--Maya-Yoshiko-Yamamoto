"""
Serviço de treinamento do SmartSaúde.
Este módulo configura e inicia o serviço de treinamento usando FastAPI.
"""

from fastapi import FastAPI
from app.routers.health_router import router as health_router
from app.routers.training_router import router as training_router
from app.routers.me_router import router as me_router
from app.routers.exercises_router import router as exercises_router
from app.routers.progress_router import router as progress_router
from app.routers.leaderboard_router import router as leaderboard_router
from app.routers.challenges_router import router as challenges_router
from app.routers.goals_router import router as goals_router


def create_app() -> FastAPI:
    """
    Cria e configura a aplicação FastAPI para o serviço de treinamento.

    Returns:
        FastAPI: Instância configurada da aplicação.
    """
    app = FastAPI(title="smartsaude-training", version="1.0.0")
    app.include_router(health_router)
    app.include_router(training_router, prefix="/training", tags=["training"])
    app.include_router(me_router, prefix="/training", tags=["training"])
    app.include_router(exercises_router)

    # Novos endpoints de gamificação e progresso
    app.include_router(progress_router)
    app.include_router(leaderboard_router)
    app.include_router(challenges_router)
    app.include_router(goals_router)

    return app


app = create_app()
