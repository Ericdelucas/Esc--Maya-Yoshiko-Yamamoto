from fastapi import FastAPI
import os
from app.routers.health_router import router as health_router
from app.routers.ehr_router import router as ehr_router
from app.routers.consent_router import router as consent_router
from app.routers.patient_documents_router import router as patient_documents_router


def create_app() -> FastAPI:
    app = FastAPI(title="smartsaude-ehr", version="1.0.0")
    app.include_router(health_router)
    app.include_router(ehr_router, prefix="/ehr", tags=["ehr"])
    app.include_router(consent_router, prefix="/ehr", tags=["consents"])
    app.include_router(patient_documents_router)
    
    # Criar diretório de documentos se não existir
    os.makedirs("/app/storage/patient_documents", exist_ok=True)
    
    # REMOVIDO: Servir arquivos estáticos de documentos (AGORA É INSEGURO)
    # app.mount("/media/patient_documents", StaticFiles(directory="/app/storage/patient_documents"), name="patient_documents")
    
    return app


app = create_app()
