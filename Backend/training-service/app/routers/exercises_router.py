from fastapi import APIRouter

router = APIRouter()

@router.get("/")
async def get_exercises():
    """Get exercises for training"""
    return [
        {
            "id": 1,
            "title": "Exercise 1",
            "description": "Test exercise for training"
        }
    ]
