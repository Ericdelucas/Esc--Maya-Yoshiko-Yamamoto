from pydantic import BaseModel, Field
from typing import Optional


class ExerciseCreate(BaseModel):
    title: str = Field(min_length=1, max_length=120)
    description: str = Field(min_length=1, max_length=8000)
    instructions: Optional[str] = Field(None, max_length=2000)
    tags: list[str] = Field(default_factory=list)
    image_path: Optional[str] = Field(None, max_length=512)
    video_path: Optional[str] = Field(None, max_length=512)


class ExerciseOut(BaseModel):
    id: int
    title: str
    description: str
    instructions: Optional[str]
    tags: list[str]
    image_url: Optional[str]
    video_url: Optional[str]
    created_by: Optional[int]
    created_at_iso: str


class FileUploadResponse(BaseModel):
    file_name: str
    file_url: str
    content_type: str
