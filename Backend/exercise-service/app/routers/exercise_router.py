from fastapi import APIRouter

router = APIRouter()

@router.get("/")
async def get_exercises():
    """Get all exercises"""
    return [
        {
            "id": 1,
            "title": "Exercise 1",
            "description": "Test exercise",
            "tags": ["strength", "legs"]
        }
    ]

@router.get("/{exercise_id}")
async def get_exercise(exercise_id: int):
    """Get specific exercise"""
    return {
        "id": exercise_id,
        "title": f"Exercise {exercise_id}",
        "description": "Test exercise description",
        "tags": ["strength", "legs"]
    }
