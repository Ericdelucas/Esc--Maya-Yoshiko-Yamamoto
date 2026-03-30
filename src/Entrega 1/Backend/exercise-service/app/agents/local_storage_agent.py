import os
import uuid


class LocalStorageAgent:
    def ensure_dir(self, path: str) -> None:
        os.makedirs(path, exist_ok=True)

    def save(self, directory: str, filename: str, content: bytes) -> str:
        self.ensure_dir(directory)
        safe_name = f"{uuid.uuid4()}_{filename}"
        full_path = os.path.join(directory, safe_name)
        with open(full_path, "wb") as f:
            f.write(content)
        return full_path
