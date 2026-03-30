from dataclasses import dataclass
import requests
from app.core.config import settings


@dataclass(frozen=True)
class OllamaResult:
    text: str


class OllamaAgent:
    def generate(self, prompt: str) -> OllamaResult:
        payload = {"model": settings.ollama_model, "prompt": prompt, "stream": False}

        try:
            resp = requests.post(
                f"{settings.ollama_host}/api/generate",
                json=payload,
                timeout=settings.ollama_timeout_sec,
            )
            resp.raise_for_status()
            data = resp.json()
            return OllamaResult(text=str(data.get("response", "")).strip())
        except Exception:
            return OllamaResult(text="")
