package com.example.testbackend.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREFS_NAME = "SmartSaudePrefs";
    private static final String TOKEN_KEY = "jwt_token";
    private static final String USER_ROLE_KEY = "user_role";
    private static final String USER_EMAIL_KEY = "user_email";
    
    private SharedPreferences prefs;
    
    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveSession(String token, String role, String email) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN_KEY, token);
        editor.putString(USER_ROLE_KEY, role);
        editor.putString(USER_EMAIL_KEY, email);
        editor.apply();
    }
    
    public String getAuthToken() {
        String token = prefs.getString(TOKEN_KEY, null);
        return (token != null && !token.isEmpty()) ? "Bearer " + token : null;
    }
    
    public String getRawToken() {
        return prefs.getString(TOKEN_KEY, null);
    }
    
    public boolean isLoggedIn() {
        String token = prefs.getString(TOKEN_KEY, null);
        return token != null && !token.isEmpty();
    }
    
    public void clearToken() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(TOKEN_KEY);
        editor.remove(USER_ROLE_KEY);
        editor.remove(USER_EMAIL_KEY);
        editor.apply();
    }
    
    public String getUserRole() {
        return prefs.getString(USER_ROLE_KEY, "Patient");
    }
    
    public String getUserEmail() {
        return prefs.getString(USER_EMAIL_KEY, "");
    }
}