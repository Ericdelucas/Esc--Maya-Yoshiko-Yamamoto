import os


def _env(name: str, default: str) -> str:
    v = os.getenv(name)
    return v.strip() if v and v.strip() else default


class Settings:
    def __init__(self) -> None:
        self.db_url = _env("DB_URL", "mysql+pymysql://smartuser:smartpass@mysql:3306/smartsaude")


settings = Settings()
