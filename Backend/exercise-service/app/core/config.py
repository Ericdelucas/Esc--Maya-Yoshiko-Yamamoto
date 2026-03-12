import os


def _env(name: str, default: str) -> str:
    v = os.getenv(name)
    return v.strip() if v and v.strip() else default


class Settings:
    def __init__(self) -> None:
        self.db_url = _env("DB_URL", "sqlite:///smartsaude.db")
        self.auth_base_url = _env("AUTH_BASE_URL", "http://localhost:8085")
        self.upload_dir = _env("UPLOAD_DIR", "./uploads")


settings = Settings()
