"""Shared security utilities for SmartSaúde AI services."""

from .permissions import *
from .rbac import has_permission, get_role_permissions, validate_role
from .dependencies import require_permission, require_any_permission, require_all_permissions

__all__ = [
    # Permission constants
    "EXERCISE_CREATE", "EXERCISE_UPDATE", "EXERCISE_DELETE", "EXERCISE_UPLOAD_MEDIA", "EXERCISE_READ",
    "EHR_CREATE", "EHR_READ_OWN", "EHR_READ_ANY", "EHR_UPDATE_OWN", "EHR_UPDATE_ANY",
    "TRAINING_CREATE", "TRAINING_READ_OWN", "TRAINING_READ_ASSIGNED",
    "CONSENT_MANAGE_OWN", "CONSENT_MANAGE_ANY",
    "ANALYTICS_READ", "USER_MANAGE", "SYSTEM_CONFIG", "AI_CHAT", "NOTIFICATION_SEND",
    
    # RBAC functions
    "has_permission", "get_role_permissions", "validate_role",
    
    # Dependencies
    "require_permission", "require_any_permission", "require_all_permissions"
]
