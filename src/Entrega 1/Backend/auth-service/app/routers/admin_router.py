from fastapi import APIRouter, Depends
from app.core.rbac import require_roles

router = APIRouter()


@router.get("/admin/ping", dependencies=[Depends(require_roles(["Admin"]))])
def admin_ping():
    return {"status": "ok", "role": "Admin"}
