from fastapi import FastAPI
from app.routers.health_router import router as health_router
from app.routers.pose_router import router as pose_router
from app.routers.pose_ws_router import router as pose_ws_router
import uvicorn


def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-ai", version="1.0.0")
    app.include_router(health_router)
    app.include_router(pose_router, prefix="/ai", tags=["ai"])
    app.include_router(pose_ws_router, prefix="/ai", tags=["ai"])
    return app


app = create_app()

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8090, reload=False)
