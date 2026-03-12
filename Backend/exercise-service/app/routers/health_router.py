from fastapi import APIRouter

router = APIRouter()

@router.get("/health")
async def health_check():
    return {"status": "healthy", "service": "exercise-service"}

@router.get("/")
async def root():
    return {"message": "SmartSaude Exercise Service", "status": "running"}
