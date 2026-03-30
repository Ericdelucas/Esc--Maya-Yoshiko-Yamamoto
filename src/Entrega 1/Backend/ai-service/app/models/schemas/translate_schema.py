from pydantic import BaseModel, Field


class TranslateIn(BaseModel):
    text: str = Field(min_length=1, max_length=10_000)
    source_lang: str = Field(pattern="^(pt|en)$")
    target_lang: str = Field(pattern="^(pt|en)$")


class TranslateOut(BaseModel):
    translated_text: str
