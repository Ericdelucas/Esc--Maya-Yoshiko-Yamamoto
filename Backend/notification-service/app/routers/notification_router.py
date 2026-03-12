from fastapi import APIRouter

router = APIRouter()

@router.get("/")
async def get_notifications():
    """Get all notifications"""
    return [
        {
            "id": 1,
            "title": "Test Notification",
            "message": "This is a test notification",
            "status": "sent"
        }
    ]

@router.post("/")
async def create_notification(title: str, message: str):
    """Create a new notification"""
    return {
        "id": 1,
        "title": title,
        "message": message,
        "status": "queued"
    }
