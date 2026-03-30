# ai-service/app/routers/translate_router.py
from pydantic import BaseModel, Field
from fastapi import APIRouter
from app.services.translate_service import TranslateRequest, TranslateService, TranslateResult

router = APIRouter()
_service = TranslateService()


class TranslateIn(BaseModel):
    text: str = Field(min_length=0)
    source_lang: str = Field(min_length=2, max_length=5)
    target_lang: str = Field(min_length=2, max_length=5)


class TranslateOut(BaseModel):
    translated_text: str


@router.post("/translate", response_model=TranslateOut)
def translate(payload: TranslateIn) -> TranslateOut:
    req = TranslateRequest(**payload.model_dump())
    result: TranslateResult = _service.translate(req)
    return TranslateOut(translated_text=result.translated_text)
