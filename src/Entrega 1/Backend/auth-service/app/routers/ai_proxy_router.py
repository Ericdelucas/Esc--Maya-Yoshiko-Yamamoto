from fastapi import APIRouter, HTTPException, Request, Response
import httpx
import os

router = APIRouter(prefix="/ai", tags=["ai"])

AI_SERVICE_URL = os.getenv("AI_SERVICE_URL", "http://ai-service:8090")


@router.post("/chat")
async def proxy_ai_chat(request: Request):
    try:
        payload = await request.json()

        async with httpx.AsyncClient(timeout=30.0) as client:
            response = await client.post(
                f"{AI_SERVICE_URL}/ai/chat",
                json=payload
            )

        return Response(
            content=response.content,
            status_code=response.status_code,
            media_type="application/json"
        )

    except httpx.RequestError as e:
        raise HTTPException(status_code=502, detail=str(e))
