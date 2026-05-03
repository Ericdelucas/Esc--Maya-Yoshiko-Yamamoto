# __init__.py

from sqlalchemy.orm import DeclarativeBase

# Create a shared Base class for all ORM models
class Base(DeclarativeBase):
    pass

# Import all ORM models to ensure they are registered with SQLAlchemy
from .user_orm import UserORM
from .appointment_orm import AppointmentORM
from .health_tools_orm import HealthToolsORM
from .patient_evaluation_orm import PatientEvaluationORM
from .patient_report_orm import PatientReportORM
from .task_orm import TaskORM

__all__ = [
    "Base",
    "UserORM", 
    "AppointmentORM",
    "HealthToolsORM",
    "PatientEvaluationORM", 
    "PatientReportORM",
    "TaskORM"
]
