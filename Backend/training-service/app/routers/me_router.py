from fastapi import APIRouter

router = APIRouter()

@router.get("/me")
async def get_my_plans():
    """Get current user's training plans"""
    return [
        {
            "id": 1,
            "title": "My Training Plan",
            "progress": 50
        }
    ]
