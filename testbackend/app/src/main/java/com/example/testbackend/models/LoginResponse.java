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

    public String getToken() {
        if (token != null && !token.isEmpty()) return token;
        return accessToken;
    }

    public String getUserRole() {
        if (userRole != null && !userRole.isEmpty()) return userRole;
        if (role != null && !role.isEmpty()) return role;
        return "Patient"; // Default seguro
    }
}