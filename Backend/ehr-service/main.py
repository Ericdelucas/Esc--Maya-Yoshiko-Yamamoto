from fastapi import FastAPI
from app.routers.health_router import router as health_router
from app.routers.ehr_router import router as ehr_router
from app.routers.consent_router import router as consent_router
from app.core.config import settings
# from shared.security.validator import validate_crypto_config
import uvicorn


def create_app() -> FastAPI:
    # Validate encryption configuration on startup
    # validate_crypto_config()  # Temporarily disabled
    
    app = FastAPI(title="smartsaude-ehr", version="1.0.0")
    app.include_router(health_router)
    app.include_router(ehr_router, prefix="/ehr", tags=["ehr"])
    app.include_router(consent_router, prefix="/ehr", tags=["consents"])
    return app


app = create_app()

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=settings.ehr_port, reload=False)
