from fastapi import APIRouter

router = APIRouter()

@router.get("/plans")
async def get_training_plans():
    """Get training plans"""
    return [
        {
            "id": 1,
            "patient_id": 1,
            "title": "Basic Training Plan",
            "start_date": "2026-03-12",
            "exercises": []
        }
    ]

@router.post("/plans")
async def create_training_plan(patient_id: int, title: str):
    """Create training plan"""
    return {
        "id": 1,
        "patient_id": patient_id,
        "title": title,
        "start_date": "2026-03-12",
        "message": "Training plan created"
    }
