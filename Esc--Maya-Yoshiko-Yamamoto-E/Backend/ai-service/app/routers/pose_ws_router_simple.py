from __future__ import annotations

import json
import time
from typing import Dict, List

from fastapi import APIRouter, WebSocket, WebSocketDisconnect

router = APIRouter(tags=["AI - Pose WS"])

@router.websocket("/ai/pose/ws")
async def pose_ws(ws: WebSocket) -> None:
    await ws.accept()
    await ws.send_json({"hello": "ws_connected"})
    
    try:
        # Handshake simplificado
        first = await ws.receive_text()
        meta = json.loads(first)
        
        exercise_id = int(meta.get("exercise_id", 1))
        token = str(meta.get("token", ""))
        send_audio = bool(meta.get("send_audio", False))
        
        await ws.send_json({"status": "ready", "exercise_id": exercise_id})
        
        # Loop simplificado
        while True:
            try:
                msg = await ws.receive()
                frame_bytes = msg.get("bytes")
                if not frame_bytes:
                    continue
                
                # TESTE: apenas responder que recebeu o frame
                await ws.send_json({
                    "ping": "frame_received", 
                    "bytes": len(frame_bytes),
                    "detected": True,
                    "landmarks": {
                        "left_shoulder": [0.4, 0.3],
                        "right_shoulder": [0.6, 0.3],
                    },
                    "phase": "TEST",
                    "feedback_level": "OK",
                    "instruction": "Sistema funcionando!"
                })
                
            except Exception as e:
                await ws.send_json({"error": "FRAME_PROCESSING_ERROR", "detail": str(e)})
                continue
                
    except WebSocketDisconnect:
        return
