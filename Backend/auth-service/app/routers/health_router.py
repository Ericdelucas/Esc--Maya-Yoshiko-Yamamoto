from fastapi import APIRouter

router = APIRouter()

@router.get("/health")
async def health_check():
    return {"status": "healthy", "service": "auth-service"}

@router.get("/")
async def root():
    return {"message": "SmartSaude Auth Service", "status": "running"}
