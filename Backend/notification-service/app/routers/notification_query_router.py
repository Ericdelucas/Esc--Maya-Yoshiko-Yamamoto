from fastapi import APIRouter

router = APIRouter()

@router.get("/user/{user_id}")
async def get_user_notifications(user_id: int):
    """Get notifications for a specific user"""
    return [
        {
            "id": 1,
            "user_id": user_id,
            "title": "Test Notification",
            "message": "This is a test notification",
            "status": "sent"
        }
    ]
