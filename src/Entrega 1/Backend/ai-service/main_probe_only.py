from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers.translate_router import router as translate_router
from app.routers.ws_probe_router import router as ws_probe_router
from app.routers.pose_ws_router_simple import router as pose_ws_router_simple

def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-ai-probe", version="1.0.0")
    
    # CORS middleware
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )
    
    # Probe + pose simplificado
    app.include_router(translate_router, prefix="/ai", tags=["ai"])
    app.include_router(ws_probe_router, tags=["websocket"])
    app.include_router(pose_ws_router_simple, tags=["websocket"])
    
    @app.on_event("startup")
    async def _dump_routes() -> None:
        for r in app.routes:
            path = getattr(r, "path", "")
            name = getattr(r, "name", "")
            methods = getattr(r, "methods", None)
            if "/ai/pose/ws" in path:
                print("FOUND ROUTE:", path, name, methods)
    
    return app

app = create_app()
