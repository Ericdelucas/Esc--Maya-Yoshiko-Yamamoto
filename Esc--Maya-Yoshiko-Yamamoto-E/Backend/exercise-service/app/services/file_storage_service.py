import os
import uuid
from pathlib import Path
from fastapi import UploadFile, HTTPException
from typing import Tuple


class FileStorageService:
    ALLOWED_IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".webp"}
    ALLOWED_VIDEO_EXTENSIONS = {".mp4", ".webm", ".mov"}

    def __init__(self, base_storage_path: str = "storage"):
        self.base_storage_path = Path(base_storage_path)
        self.images_path = self.base_storage_path / "images"
        self.videos_path = self.base_storage_path / "videos"
        self.max_file_size = 50 * 1024 * 1024  # 50MB

        self.images_path.mkdir(parents=True, exist_ok=True)
        self.videos_path.mkdir(parents=True, exist_ok=True)

    def _get_extension(self, filename: str) -> str:
        return Path(filename).suffix.lower()

    def _validate_image_extension(self, file: UploadFile) -> str:
        ext = self._get_extension(file.filename or "")
        if ext not in self.ALLOWED_IMAGE_EXTENSIONS:
            raise HTTPException(
                status_code=400,
                detail=f"Image extension {ext} not allowed"
            )
        return ext

    def _validate_video_extension(self, file: UploadFile) -> str:
        ext = self._get_extension(file.filename or "")
        if ext not in self.ALLOWED_VIDEO_EXTENSIONS:
            raise HTTPException(
                status_code=400,
                detail=f"Video extension {ext} not allowed"
            )
        return ext

    def _generate_filename(self, ext: str) -> str:
        return f"{uuid.uuid4().hex}{ext}"

    async def save_image(self, file: UploadFile) -> Tuple[str, str, str]:
        ext = self._validate_image_extension(file)
        filename = self._generate_filename(ext)
        destination = self.images_path / filename

        content = await file.read()
        destination.write_bytes(content)

        return filename, f"/media/images/{filename}", file.content_type or "unknown"

    async def save_video(self, file: UploadFile) -> Tuple[str, str, str]:
        ext = self._validate_video_extension(file)
        filename = self._generate_filename(ext)
        destination = self.videos_path / filename

        content = await file.read()
        destination.write_bytes(content)

        return filename, f"/media/videos/{filename}", file.content_type or "unknown"


file_storage_service = FileStorageService()
