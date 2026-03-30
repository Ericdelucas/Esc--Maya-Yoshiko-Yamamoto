import os
from dataclasses import dataclass

import requests


@dataclass(frozen=True)
class OllamaTranslatorAgent:
    base_url: str
    model: str

    def translate(self, text: str, source_lang: str, target_lang: str) -> str:
        prompt = _build_prompt(text=text, source_lang=source_lang, target_lang=target_lang)

        payload = {
            "model": self.model,
            "prompt": prompt,
            "stream": False,
            "options": {"temperature": 0.0},
        }

        r = requests.post(f"{self.base_url}/api/generate", json=payload, timeout=60)
        r.raise_for_status()

        data = r.json()
        return (data.get("response") or "").strip()


def _build_prompt(text: str, source_lang: str, target_lang: str) -> str:
    src = "Portuguese" if source_lang == "pt" else "English"
    tgt = "English" if target_lang == "en" else "Portuguese"
    return (
        "You are a professional translator.\n"
        f"Translate from {src} to {tgt}.\n"
        "Rules:\n"
        "- Output ONLY the translated text.\n"
        "- Preserve meaning and tone.\n"
        "- Do not add explanations.\n\n"
        f"Text:\n{text}\n"
    )
