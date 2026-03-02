from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers.translate_router import router as translate_router
from app.routers.pose_router import router as pose_router
from app.routers.pose_ws_router import router as pose_ws_router
from app.routers.ws_probe_router import router as ws_probe_router


def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-ai", version="1.0.0")
    
    # Adicionar endpoint de teste WebSocket direto no app
    @app.websocket("/test-ws")
    async def test_websocket(websocket):
        await websocket.accept()
        await websocket.send_json({"hello": "direct_test"})
    
    app.include_router(translate_router, prefix="/ai", tags=["ai"])
    app.include_router(pose_router)
    app.include_router(pose_ws_router, tags=["websocket"])
    app.include_router(ws_probe_router, tags=["websocket"])
    return app


app = create_app()
