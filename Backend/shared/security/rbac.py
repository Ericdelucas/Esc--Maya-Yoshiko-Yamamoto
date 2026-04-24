from typing import Set
from .permissions import *

# Role-based permission mapping
ROLE_PERMISSIONS = {
    "admin": {
        # Admin tem todas as permissões
        EXERCISE_CREATE,
        EXERCISE_UPDATE,
        EXERCISE_DELETE,
        EXERCISE_UPLOAD_MEDIA,
        EXERCISE_READ,
        
        EHR_CREATE,
        EHR_READ_OWN,
        EHR_READ_ANY,
        EHR_UPDATE_OWN,
        EHR_UPDATE_ANY,
        
        TRAINING_CREATE,
        TRAINING_READ_OWN,
        TRAINING_READ_ASSIGNED,
        
        CONSENT_MANAGE_OWN,
        CONSENT_MANAGE_ANY,
        
        ANALYTICS_READ,
        
        USER_MANAGE,
        
        SYSTEM_CONFIG,
        
        AI_CHAT,
        
        NOTIFICATION_SEND,
    },
    
    "professional": {
        # Exercise permissions
        EXERCISE_CREATE,
        EXERCISE_UPDATE,
        EXERCISE_UPLOAD_MEDIA,
        EXERCISE_READ,
        # Note: Professionals cannot delete exercises (only admins)
        
        # EHR permissions
        EHR_CREATE,
        EHR_READ_OWN,
        EHR_UPDATE_OWN,
        # Note: Professionals can only manage their own patients' EHR
        
        # Training permissions
        TRAINING_CREATE,
        TRAINING_READ_OWN,
        TRAINING_READ_ASSIGNED,
        
        # Consent permissions
        CONSENT_MANAGE_OWN,
        # Note: Only manage consents for their own patients
        
        # AI permissions
        AI_CHAT,
    },
    
    "patient": {
        # Exercise permissions
        EXERCISE_READ,
        
        # EHR permissions
        EHR_READ_OWN,
        
        # Training permissions
        TRAINING_READ_OWN,
        
        # Consent permissions
        CONSENT_MANAGE_OWN,
        
        # AI permissions
        AI_CHAT,
    },
}

def has_permission(role: str, permission: str) -> bool:
    """
    Check if a role has a specific permission
    
    Args:
        role: User role (Admin, Professional, Patient)
        permission: Permission string to check
        
    Returns:
        True if role has the permission, False otherwise
    """
    role_permissions = ROLE_PERMISSIONS.get(role, set())
    return permission in role_permissions

def get_role_permissions(role: str) -> Set[str]:
    """
    Get all permissions for a role
    
    Args:
        role: User role (admin, professional, patient)
        
    Returns:
        Set of all permissions for the role
    """
    return ROLE_PERMISSIONS.get(role.lower(), set())

def validate_role(role: str) -> bool:
    """
    Check if role is valid in the system
    
    Args:
        role: Role string to validate
        
    Returns:
        True if role is valid, False otherwise
    """
    return role.lower() in ROLE_PERMISSIONS
