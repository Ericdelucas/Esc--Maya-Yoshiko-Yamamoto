from app.core.config import settings


def exercise_media_dir() -> str:
    return f"{settings.upload_dir}/exercises"
