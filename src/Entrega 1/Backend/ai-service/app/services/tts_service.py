from __future__ import annotations

import base64
from collections import OrderedDict
from dataclasses import dataclass
from typing import Optional

from gtts import gTTS


@dataclass(frozen=True)
class TtsConfig:
    lang: str = "pt"
    max_cache_items: int = 64


class TtsService:
    def __init__(self, cfg: TtsConfig = TtsConfig()) -> None:
        self._cfg = cfg
        self._cache: OrderedDict[str, str] = OrderedDict()

    def synth_base64(self, text: str) -> Optional[str]:
        text = (text or "").strip()
        if not text:
            return None

        if text in self._cache:
            self._cache.move_to_end(text)
            return self._cache[text]

        tts = gTTS(text=text, lang=self._cfg.lang)
        mp3_bytes = self._to_mp3_bytes(tts)
        b64 = base64.b64encode(mp3_bytes).decode("utf-8")

        self._cache[text] = b64
        self._evict_if_needed()
        return b64

    @staticmethod
    def _to_mp3_bytes(tts: gTTS) -> bytes:
        from io import BytesIO
        buf = BytesIO()
        tts.write_to_fp(buf)
        return buf.getvalue()

    def _evict_if_needed(self) -> None:
        while len(self._cache) > self._cfg.max_cache_items:
            self._cache.popitem(last=False)
