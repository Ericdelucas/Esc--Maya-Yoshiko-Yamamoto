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
    
    # Configurar arquivos estáticos para fotos de perfil (usar diretório persistente)
    # Render permite escrita em /tmp, então usamos isso para persistência
    profile_photos_dir = "/tmp/profile_photos"
    os.makedirs(profile_photos_dir, exist_ok=True)
    
    print(f"📁 Profile photos directory: {profile_photos_dir}")
    
    app.mount("/media/profiles", StaticFiles(directory=profile_photos_dir), name="profile_photos")
    
    # Endpoint para debug de fotos de perfil
    @app.get("/media/profiles/debug")
    def debug_profile_photos():
        try:
            import os
            files = os.listdir(profile_photos_dir)
            return {
                "directory": profile_photos_dir,
                "exists": os.path.exists(profile_photos_dir),
                "files": files,
                "count": len(files)
            }
        except Exception as e:
            return {
                "error": str(e),
                "directory": profile_photos_dir
            }
    
    # Endpoint fallback para fotos ausentes
    @app.get("/media/profiles/{filename}")
    def get_profile_photo_fallback(filename: str):
        from fastapi import HTTPException
        import os
        from fastapi.responses import Response
        
        file_path = os.path.join(profile_photos_dir, filename)
        
        if not os.path.exists(file_path):
            # Retornar imagem padrão (1x1 pixel transparente)
            transparent_pixel = b'\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x06\x00\x00\x00\x1f\x15\xc4\x89\x00\x00\x00\rIDATx\x9cc\xf8\x00\x00\x00\x01\x00\x01\x00\x00\x00\x00IEND\xaeB`\x82'
            return Response(content=transparent_pixel, media_type="image/png")
        
        # Se o arquivo existe, deixar o StaticFiles lidar com isso
        raise HTTPException(status_code=404, detail="File not found")
    
    return app


app = create_app()

if __name__ == "__main__":
    settings = get_settings()
    port = int(os.getenv("PORT", settings.auth_port))
    uvicorn.run("main:app", host="0.0.0.0", port=port, reload=False)
