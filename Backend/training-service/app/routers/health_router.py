from fastapi import APIRouter

router = APIRouter()

@router.get("/health")
async def health_check():
    return {"status": "healthy", "service": "training-service"}

@router.get("/")
async def root():
    return {"message": "SmartSaude Training Service", "status": "running"}
