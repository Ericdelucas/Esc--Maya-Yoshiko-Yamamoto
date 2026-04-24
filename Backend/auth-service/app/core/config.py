
from functools import lru_cache

from pydantic_settings import BaseSettings


class Settings(BaseSettings):
	auth_port: int = 8080

	db_url: str = "mysql+pymysql://smartuser:smartpass@mysql:3306/smartsaude"
	jwt_secret: str = "change_me"
	pepper_key: str = "change_me"

	class Config:
		env_prefix = ""
		case_sensitive = False


@lru_cache(maxsize=1)
def get_settings() -> Settings:
	return Settings()
