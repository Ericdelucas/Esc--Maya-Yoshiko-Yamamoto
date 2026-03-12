from functools import lru_cache

from pydantic_settings import BaseSettings


class Settings(BaseSettings):
	auth_port: int = 8085

	db_url: str = "sqlite:///smartsaude.db"
	jwt_secret: str = "change_me"
	pepper_key: str = "change_me"

	class Config:
		env_prefix = ""
		case_sensitive = False


@lru_cache
def get_settings() -> Settings:
	return Settings()


settings = get_settings()
