from app.agents.local_storage_agent import LocalStorageAgent
from app.core.paths import exercise_media_dir


class MediaService:
    def __init__(self) -> None:
        self._agent = LocalStorageAgent()

    def save_exercise_media(self, filename: str, content: bytes) -> str:
        directory = exercise_media_dir()
        return self._agent.save(directory=directory, filename=filename, content=content)
