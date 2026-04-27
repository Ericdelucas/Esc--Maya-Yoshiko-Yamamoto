import uvicorn
from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
import os

from app.core.config import get_settings
from app.core.error_handler import register_error_handlers
from app.routers.auth_router import router as auth_router
from app.routers.me_router import router as me_router
from app.routers.admin_router import router as admin_router
from app.routers.health_router import router as health_router
from app.routers.ai_proxy_router import router as ai_proxy_router
from app.routers.professional_router import router as professional_router
from app.routers.appointment_router import router as appointment_router
from app.routers.patient_evaluation_router import router as patient_evaluation_router
from app.routers.patient_report_router import router as patient_report_router
from app.routers.task_router import router as task_router
from app.routers.notification_router import router as notification_router
from app.routers.health_tools_router import router as health_tools_router
from app.routers.patient_health_router import router as patient_health_router
from app.routers.patient_router import router as patient_router


def create_app() -> FastAPI:
    app = FastAPI(title="SmartSaúde Auth Service", version="0.0.1")

    register_error_handlers(app)

    # Configurar arquivos estáticos para fotos de perfil
    profile_photos_dir = os.path.join(os.getcwd(), "storage", "profile_photos")
    os.makedirs(profile_photos_dir, exist_ok=True)
    app.mount("/media/profiles", StaticFiles(directory=profile_photos_dir), name="profile_photos")

    app.include_router(health_router)
    app.include_router(auth_router, tags=["auth"])
    app.include_router(me_router)
    app.include_router(admin_router, prefix="/auth")
    app.include_router(ai_proxy_router)
    app.include_router(professional_router, tags=["professional"])
    app.include_router(appointment_router, tags=["appointments"])
    app.include_router(patient_evaluation_router, tags=["patient_evaluations"])
    app.include_router(patient_report_router, tags=["patient_reports"])
    app.include_router(task_router, tags=["tasks"])
    app.include_router(notification_router, tags=["notifications"])
    app.include_router(health_tools_router, prefix="/health-tools", tags=["health-tools"])
    app.include_router(patient_health_router, tags=["patient-health"])
    app.include_router(patient_router, tags=["patient"])
    return app


app = create_app()

if __name__ == "__main__":
    settings = get_settings()
    port = int(os.getenv("PORT", settings.auth_port))
    uvicorn.run("main:app", host="0.0.0.0", port=port, reload=False)
