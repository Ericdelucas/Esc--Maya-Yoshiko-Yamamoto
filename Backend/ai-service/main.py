from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers.translate_router import router as translate_router
from app.routers.pose_router import router as pose_router
from app.routers.pose_ws_router import router as pose_ws_router


def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-ai", version="1.0.0")
    
    # CORS middleware para permitir WebSocket e requisições do frontend
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],  # Em produção, especifique os domínios permitidos
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )
    
    app.include_router(translate_router, prefix="/ai", tags=["ai"])
    app.include_router(pose_router)
    app.include_router(pose_ws_router)
    return app


app = create_app()
