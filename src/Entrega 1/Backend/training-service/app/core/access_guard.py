from fastapi import HTTPException, status


def assert_patient_scope(payload: dict, patient_id: int) -> None:
    role = payload.get("role")
    sub = payload.get("sub")

    if role in ("Admin", "Professional"):
        return

    if role == "Patient":
        if sub is None or str(sub) != str(patient_id):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Patient can only access own data",
            )
        return

    raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Insufficient permissions")
