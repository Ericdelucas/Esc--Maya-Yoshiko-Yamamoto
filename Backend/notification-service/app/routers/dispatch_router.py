from fastapi import APIRouter

router = APIRouter()

@router.post("/dispatch")
async def dispatch_notification(notification_id: int):
    """Dispatch a notification"""
    return {
        "notification_id": notification_id,
        "status": "dispatched",
        "message": "Notification dispatched successfully"
    }
