from app.services.translate_service import TranslateRequest, TranslateService


class _FakeAgent:
    def __init__(self, out: str) -> None:
        self._out = out

    def generate(self, prompt: str):
        class _R:
            def __init__(self, text: str) -> None:
                self.text = text
        return _R(self._out)


def test_translate_fallback_when_agent_empty():
    svc = TranslateService()
    svc._agent = _FakeAgent(out="")  # monkeypatch simples
    req = TranslateRequest(text="Olá", source_lang="pt", target_lang="en")
    res = svc.translate(req)
    assert res.translated_text == "Olá"


def test_translate_returns_agent_output():
    svc = TranslateService()
    svc._agent = _FakeAgent(out="Hello")
    req = TranslateRequest(text="Olá", source_lang="pt", target_lang="en")
    res = svc.translate(req)
    assert res.translated_text == "Hello"
