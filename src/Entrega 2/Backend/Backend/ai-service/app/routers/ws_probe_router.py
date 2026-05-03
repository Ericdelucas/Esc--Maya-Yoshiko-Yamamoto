from fastapi import APIRouter, WebSocket

router = APIRouter()

@router.websocket("/ai/ws/probe")
async def ws_probe(ws: WebSocket) -> None:
    await ws.accept()
    await ws.send_json({"hello": "probe_ok"})
    while True:
        msg = await ws.receive_text()
        await ws.send_json({"echo": msg})
