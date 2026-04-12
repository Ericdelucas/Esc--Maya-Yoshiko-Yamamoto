import os


def _get_env(name: str, default: str) -> str:
    value = os.getenv(name)
    return value.strip() if value and value.strip() else default


class Settings:
    def __init__(self) -> None:
        self.ollama_host = _get_env("OLLAMA_HOST", "http://host.docker.internal:11434")
        self.ollama_model = _get_env("OLLAMA_MODEL", "llama3")
        self.ollama_timeout_sec = int(_get_env("OLLAMA_TIMEOUT_SEC", "30"))


settings = Settings()
