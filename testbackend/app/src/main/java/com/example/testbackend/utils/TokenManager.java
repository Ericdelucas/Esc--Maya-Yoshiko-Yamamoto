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
    
    private SharedPreferences prefs;
    
    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveSession(String token, String role, String email) {
        Log.d(TAG, "Salvando sessão - Token: " + (token != null ? "OK" : "NULL") + ", Role: '" + role + "', Email: '" + email + "'");
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN_KEY, token);
        editor.putString(USER_ROLE_KEY, role);
        editor.putString(USER_EMAIL_KEY, email);
        // Usamos commit() para garantir persistência imediata antes da navegação
        editor.commit(); 
    }
    
    public String getAuthToken() {
        String token = prefs.getString(TOKEN_KEY, null);
        return (token != null && !token.isEmpty()) ? "Bearer " + token : null;
    }
    
    public String getUserRole() {
        String role = prefs.getString(USER_ROLE_KEY, "patient");
        // 🔥 PADRONIZAÇÃO: Sempre retorna em minúsculo e sem espaços
        if (role != null) {
            role = role.trim().toLowerCase();
        }
        Log.d(TAG, "Lendo Role processado: '" + role + "'");
        return role;
    }

    public String getUserEmail() {
        return prefs.getString(USER_EMAIL_KEY, "");
    }
    
    public boolean isLoggedIn() {
        String token = prefs.getString(TOKEN_KEY, null);
        return token != null && !token.isEmpty();
    }
    
    public void clearToken() {
        // 🔥 LIMPEZA SELETIVA: Não apaga configurações globais do app
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(TOKEN_KEY);
        editor.remove(USER_ROLE_KEY);
        editor.remove(USER_EMAIL_KEY);
        editor.apply();
        Log.d(TAG, "Sessão limpa (Logout)");
    }
}
