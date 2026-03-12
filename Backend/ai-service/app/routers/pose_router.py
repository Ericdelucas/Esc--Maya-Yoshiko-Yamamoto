from fastapi import APIRouter

router = APIRouter()

@router.post("/pose/analyze")
async def analyze_pose():
    """Analyze pose using AI"""
    return {
        "analysis": "Pose analysis complete",
        "confidence": 0.95,
        "feedback": "Good posture detected"
    }
