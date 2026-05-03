from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from app.core.exceptions import ApiError


def register_error_handlers(app: FastAPI) -> None:
    @app.exception_handler(ApiError)
    async def _api_error(_: Request, exc: ApiError):
        status = _status_for(exc.code)
        return JSONResponse(
            status_code=status,
            content={"error": {"code": exc.code, "message": exc.message}},
        )

    @app.exception_handler(Exception)
    async def _unexpected(_: Request, __: Exception):
        return JSONResponse(
            status_code=500,
            content={"error": {"code": "INTERNAL_ERROR", "message": "unexpected error"}},
        )


def _status_for(code: str) -> int:
    return {
        "BAD_REQUEST": 400,
        "UNAUTHORIZED": 401,
        "CONFLICT": 409,
    }.get(code, 400)
