package com.example.testbackend.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TokenManager {
    private static final String TAG = "TokenManager_DEBUG";
    private static final String PREFS_NAME = "SmartSaudePrefs";
    private static final String TOKEN_KEY = "jwt_token";
    private static final String USER_ROLE_KEY = "user_role";
    private static final String USER_EMAIL_KEY = "user_email";
    private static final String USER_ID_KEY = "user_id";
    
    private SharedPreferences prefs;
    
    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveSession(String token, String role, String email) {
        saveSession(token, role, email, -1);
    }

    public void saveSession(String token, String role, String email, int userId) {
        Log.d(TAG, "Salvando sessão - Token: " + (token != null ? "OK" : "NULL") + ", Role: '" + role + "', Email: '" + email + "', ID: " + userId);
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN_KEY, token);
        editor.putString(USER_ROLE_KEY, role);
        editor.putString(USER_EMAIL_KEY, email);
        editor.putInt(USER_ID_KEY, userId);
        editor.commit(); 
    }
    
    public String getAuthToken() {
        String token = prefs.getString(TOKEN_KEY, null);
        return (token != null && !token.isEmpty()) ? "Bearer " + token : null;
    }
    
    public String getUserRole() {
        String role = prefs.getString(USER_ROLE_KEY, "patient");
        if (role != null) {
            role = role.trim().toLowerCase();
        }
        return role;
    }

    public String getUserEmail() {
        return prefs.getString(USER_EMAIL_KEY, "");
    }

    public int getUserId() {
        return prefs.getInt(USER_ID_KEY, -1);
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
        editor.remove(USER_ID_KEY);
        editor.apply();
        Log.d(TAG, "Sessão limpa (Logout)");
    }
}
