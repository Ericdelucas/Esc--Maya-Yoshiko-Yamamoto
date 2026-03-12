from fastapi import APIRouter, WebSocket, WebSocketDisconnect
import json

router = APIRouter()

@router.websocket("/pose/ws")
async def websocket_endpoint(websocket: WebSocket):
    """WebSocket for real-time pose analysis"""
    await websocket.accept()
    try:
        while True:
            data = await websocket.receive_text()
            # Echo back for testing
            await websocket.send_text(f"Received: {data}")
    except WebSocketDisconnect:
        pass
