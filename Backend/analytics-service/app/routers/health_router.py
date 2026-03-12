from fastapi import APIRouter

router = APIRouter()

@router.get("/health")
async def health_check():
    return {"status": "healthy", "service": "analytics-service"}

@router.get("/")
async def root():
    return {"message": "SmartSaude Analytics Service", "status": "running"}
