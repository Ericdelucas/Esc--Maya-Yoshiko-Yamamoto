from fastapi import APIRouter

router = APIRouter()

@router.get("/analytics")
async def get_analytics():
    """Get analytics data"""
    return {
        "total_users": 100,
        "active_sessions": 25,
        "exercises_completed": 150,
        "training_plans_active": 10
    }
