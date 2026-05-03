package com.example.testbackend.utils;

import com.example.testbackend.MainActivity;
import com.example.testbackend.ProfessionalMainActivity;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AccessControl {
    private static final Map<String, List<String>> ACCESS_MAP = Map.of(
        "professional", Arrays.asList("dashboard", "pacientes", "exercicios", "monitoramento", "relatorios", "analytics", "perfil"),
        "patient", Arrays.asList("dashboard", "exercicios", "progresso", "relatorios", "perfil")
    );
    
    public static boolean hasAccess(String userRole, String section) {
        if (userRole == null) return false;
        return ACCESS_MAP.getOrDefault(userRole.toLowerCase(), Arrays.asList()).contains(section);
    }
    
    public static boolean isProfessional(String userRole) {
        if (userRole == null) return false;
        String role = userRole.toLowerCase();
        return role.equals("professional") || role.equals("doctor") || role.equals("admin");
    }
    
    public static boolean isPatient(String userRole) {
        if (userRole == null) return true;
        String role = userRole.toLowerCase();
        return role.equals("patient") || role.equals("user");
    }
    
    public static List<String> getAllowedSections(String userRole) {
        return ACCESS_MAP.getOrDefault(userRole != null ? userRole.toLowerCase() : "", Arrays.asList());
    }
    
    public static Class<?> getMainActivityForRole(String userRole) {
        if (isProfessional(userRole)) {
            return ProfessionalMainActivity.class;
        }
        return MainActivity.class;
    }
}
