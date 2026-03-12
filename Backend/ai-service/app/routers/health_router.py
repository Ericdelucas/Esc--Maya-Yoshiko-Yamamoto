from fastapi import APIRouter

router = APIRouter()

@router.get("/health")
async def health_check():
    return {"status": "healthy", "service": "ai-service"}

@router.get("/")
async def root():
    return {"message": "SmartSaude AI Service", "status": "running"}
