from pydantic import BaseModel, Field


class ExerciseCreate(BaseModel):
    title: str = Field(min_length=1, max_length=120)
    description: str = Field(min_length=1, max_length=8000)
    tags: list[str] = Field(default_factory=list)


class ExerciseOut(BaseModel):
    id: int
    title: str
    description: str
    tags: list[str]
    media_path: str | None
    created_at_iso: str
