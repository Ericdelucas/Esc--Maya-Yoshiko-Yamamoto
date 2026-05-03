from fastapi import FastAPI, WebSocket
import uvicorn

app = FastAPI()

@app.websocket("/test-ws")
async def websocket_endpoint(websocket: WebSocket):
    await websocket.accept()
    await websocket.send_json({"hello": "minimal_test"})
    try:
        while True:
            data = await websocket.receive_bytes()
            await websocket.send_json({"ping": "received", "bytes": len(data)})
    except Exception as e:
        await websocket.send_json({"error": str(e)})

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8091)
