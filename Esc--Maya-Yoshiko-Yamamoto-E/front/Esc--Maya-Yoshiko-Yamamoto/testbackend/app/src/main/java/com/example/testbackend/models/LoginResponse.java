package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("user_role")
    private String userRole;

    @SerializedName("role")
    private String role;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    // 🔥 NOVOS CAMPOS - Backend controla o direcionamento
    @SerializedName("target_activity")
    private String targetActivity;

    @SerializedName("is_professional")
    private boolean isProfessional;

    public String getToken() {
        if (token != null && !token.isEmpty()) return token;
        return accessToken;
    }

    public String getUserRole() {
        if (userRole != null && !userRole.isEmpty()) return userRole;
        if (role != null && !role.isEmpty()) return role;
        return "patient"; // Default consistente
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    // 🔥 NOVOS GETTERS
    public String getTargetActivity() {
        return targetActivity;
    }

    public boolean isProfessional() {
        return isProfessional;
    }
}
