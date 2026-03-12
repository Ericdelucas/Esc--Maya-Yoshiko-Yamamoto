from fastapi import APIRouter

router = APIRouter()

@router.get("/health")
async def health_check():
    return {"status": "healthy", "service": "ehr-service"}

@router.get("/")
async def root():
    return {"message": "SmartSaude EHR Service", "status": "running"}
