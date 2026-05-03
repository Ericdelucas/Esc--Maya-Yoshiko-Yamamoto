
from dataclasses import dataclass


@dataclass(frozen=True)
class ApiError(Exception):
	code: str
	message: str


class BadRequest(ApiError):
	def __init__(self, message: str = "invalid payload"):
		super().__init__(code="BAD_REQUEST", message=message)


class Unauthorized(ApiError):
	def __init__(self, message: str = "unauthorized"):
		super().__init__(code="UNAUTHORIZED", message=message)


class Conflict(ApiError):
	def __init__(self, message: str = "conflict"):
		super().__init__(code="CONFLICT", message=message)
