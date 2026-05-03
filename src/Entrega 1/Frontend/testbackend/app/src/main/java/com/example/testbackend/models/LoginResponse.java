package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    // Mapping for "token" or "access_token"
    @SerializedName(value = "token", alternate = {"access_token"})
    private String token;

    // Mapping for "type" or "token_type"
    @SerializedName(value = "type", alternate = {"token_type"})
    private String type;
    
    // Mapping for "user_role" or "role"
    @SerializedName(value = "user_role", alternate = {"role"})
    private String userRole;

    public String getToken() { return token; }
    public String getType() { return type; }
    public String getUserRole() { return userRole; }
}