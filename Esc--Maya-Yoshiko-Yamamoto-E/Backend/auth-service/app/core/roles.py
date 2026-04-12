from enum import Enum


class UserRole(str, Enum):
    Admin = "Admin"
    Professional = "Professional"
    Patient = "Patient"
