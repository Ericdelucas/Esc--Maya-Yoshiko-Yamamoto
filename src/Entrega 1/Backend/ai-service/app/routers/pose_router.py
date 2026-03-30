from __future__ import annotations

from fastapi import APIRouter, File, HTTPException, Header, Query, UploadFile

from app.services.pose_detector import PoseDetector
from app.services.movement_validator import MovementValidator
from app.services.training_client import TrainingClient, extract_bearer_token
from app.services.angle_feedback_service import AngleFeedbackService

router = APIRouter(prefix="/ai/pose", tags=["AI - Pose"])


@router.post("/analyze", summary="Detecta pose e retorna feedback de movimento")
async def analyze_pose(
    file: UploadFile = File(...),
    exercise_id: int = Query(..., description="ID do exercício para comparação"),
    authorization: str | None = Header(None)
) -> dict:
    frame_bytes = await file.read()
    ok, landmarks = PoseDetector().detect_landmarks(frame_bytes)

    if not ok:
        raise HTTPException(status_code=400, detail="INVALID_FRAME_BYTES")

    analysis = MovementValidator().analyze(landmarks or {})
    
    # Get ideal angles and feedback
    token = extract_bearer_token(authorization)
    training_client = TrainingClient()
    ideal_data = await training_client.get_ideal_angles(exercise_id, token)
    
    # Mapping for feedback comparison
    mapping = {
        "knee": ["left_knee", "right_knee"],
        "shoulder": ["left_shoulder", "right_shoulder"],
        "elbow": ["left_elbow", "right_elbow"],
        "hip": ["left_hip", "right_hip"]
    }
    
    feedback_service = AngleFeedbackService()
    feedback = feedback_service.build_feedback(
        analysis.joint_angles,
        ideal_data.get("ideal_angles", {}),
        mapping
    )
    
    return {
        "detected": analysis.detected,
        "joint_angles": analysis.joint_angles,
        "ideal_angles": ideal_data.get("ideal_angles", {}),
        "feedback": {
            "ok": feedback.ok,
            "messages": feedback.messages,
            "deviations": feedback.deviations
        }
    }
