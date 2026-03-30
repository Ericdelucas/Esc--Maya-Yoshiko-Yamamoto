from dataclasses import dataclass
from app.agents.ollama_agent import OllamaAgent


@dataclass(frozen=True)
class TranslateRequest:
    text: str
    source_lang: str
    target_lang: str


@dataclass(frozen=True)
class TranslateResult:
    translated_text: str


class TranslateService:
    def __init__(self) -> None:
        self._agent = OllamaAgent()

    def translate(self, req: TranslateRequest) -> TranslateResult:
        text = (req.text or "").strip()
        if not text:
            return TranslateResult(translated_text="")

        src = (req.source_lang or "").strip().lower()
        tgt = (req.target_lang or "").strip().lower()
        if src == tgt:
            return TranslateResult(translated_text=text)

        prompt = (
            "You are a professional medical translator.\n"
            f"Translate from {src} to {tgt}.\n"
            "Rules:\n"
            "- Return ONLY translated text.\n"
            "- Preserve meaning and tone.\n"
            "- Keep numbers, abbreviations, and names.\n\n"
            f"TEXT:\n{text}"
        )

        out = self._agent.generate(prompt).text
        if not out:
            return TranslateResult(translated_text=text)  # fallback controlado

        return TranslateResult(translated_text=out)
