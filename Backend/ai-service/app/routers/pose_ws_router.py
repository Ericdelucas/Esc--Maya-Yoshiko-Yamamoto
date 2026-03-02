from __future__ import annotations

import json
import time
from typing import Dict, List

from fastapi import APIRouter, WebSocket, WebSocketDisconnect

from app.services.pose_detector import PoseDetector
from app.services.movement_validator import MovementValidator
from app.services.angle_smoother import AngleSmoother
from app.services.training_client import TrainingClient
from app.services.rep_phase_service import RepPhaseService
from app.services.realtime_coach_service import RealtimeCoachService
from app.services.tts_service import TtsService

router = APIRouter(prefix="/ai/pose", tags=["AI - Pose WS"])


@router.websocket("/ws")
async def pose_ws(ws: WebSocket) -> None:
    await ws.accept()
    await ws.send_json({"hello": "ws_connected"})

    detector = PoseDetector()
    validator = MovementValidator()
    smoother = AngleSmoother()
    phase_service = RepPhaseService()
    coach = RealtimeCoachService()
    tts = TtsService()
    client = TrainingClient()

    last_said = ""
    last_said_ts = 0.0

    try:
        # Handshake com validação
        try:
            first = await ws.receive_text()
            meta = json.loads(first)
        except Exception:
            await ws.send_json({"error": "INVALID_HANDSHAKE_JSON"})
            await ws.close()
            return

        exercise_id = int(meta.get("exercise_id", 1))
        token = str(meta.get("token", ""))
        send_audio = bool(meta.get("send_audio", False))

        # TrainingClient com fallback
        ideal_angles = {}
        try:
            ideal = await client.get_ideal_angles(exercise_id, token)
            ideal_angles = ideal.get("ideal_angles", {}) if isinstance(ideal, dict) else {}
        except Exception:
            ideal_angles = {}
            await ws.send_json({"warn": "IDEAL_ANGLES_UNAVAILABLE"})

        mapping: Dict[str, List[str]] = {
            "elbow": ["left_elbow", "right_elbow"],
            "hip": ["left_hip", "right_hip"],
            "shoulder": ["left_shoulder", "right_shoulder"],
        }

        # Loop principal com proteção
        while True:
            try:
                msg = await ws.receive()
                frame_bytes = msg.get("bytes")
                if not frame_bytes:
                    continue

                # TESTE DE VIDA - verificar se frame chega
                await ws.send_json({"ping": "frame_received", "bytes": len(frame_bytes)})

                ok, landmarks = detector.detect_landmarks(frame_bytes)
                if not ok:
                    await ws.send_json({"error": "INVALID_FRAME_BYTES"})
                    continue

                analysis = validator.analyze(landmarks or {})
                smoothed = smoother.push(analysis.joint_angles) if analysis.detected else {}

                # Phase detection
                elbow_avg = None
                if "left_elbow" in smoothed and "right_elbow" in smoothed:
                    elbow_avg = (smoothed["left_elbow"] + smoothed["right_elbow"]) / 2.0
                
                phase_result = phase_service.update(elbow_avg)
                
                # Phase-aware coaching
                result = coach.coach(phase_result.phase, smoothed, ideal_angles, mapping)

                # TTS synthesis com fallback
                audio_base64 = None
                now = time.time()
                say = result.instruction
                can_say = (say != last_said) or (now - last_said_ts > 2.0)
                if can_say and send_audio:
                    last_said, last_said_ts = say, now
                    try:
                        audio_base64 = tts.synth_base64(say)
                    except Exception:
                        audio_base64 = None

                await ws.send_json(
                    {
                        "detected": analysis.detected,
                        "landmarks": landmarks or {},
                        "joint_angles": smoothed,
                        "phase": phase_result.phase,
                        "elbow_avg": phase_result.elbow_avg,
                        "feedback_level": result.level,
                        "instruction": say,
                        "reasons": result.reasons,
                        "audio_base64": audio_base64,
                    }
                )

            except Exception as e:
                await ws.send_json({"error": "FRAME_PROCESSING_ERROR", "detail": str(e)})
                continue

    except WebSocketDisconnect:
        return
