from pydantic import BaseModel, Field


class ConsentCreate(BaseModel):
    user_id: int = Field(ge=1)
    consent_type: str = Field(min_length=3, max_length=64)
    granted: bool
