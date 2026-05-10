package com.example.testbackend.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

public class LocaleHelper {

    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context);
        return setLocale(context, lang);
    }

    public static Context setLocale(Context context, String language) {
        Log.d("LocaleHelper", "Setting locale to: " + language);
        persist(context, language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }
    
    public static String getCurrentLanguage(Context context) {
        return getPersistedData(context);
    }

    private static String getPersistedData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String savedLanguage = preferences.getString(SELECTED_LANGUAGE, null);
        if (savedLanguage != null) {
            return savedLanguage;
        }
        // Se não houver idioma salvo, usar português como padrão
        return "pt";
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Log.d("LocaleHelper", "Setting locale to: " + language);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        Context updatedContext = context.createConfigurationContext(configuration);
        
        // Forçar atualização dos recursos
        Resources resources = updatedContext.getResources();
        Log.d("LocaleHelper", "Updated context locale to: " + resources.getConfiguration().locale.getLanguage());
        Log.d("LocaleHelper", "Test string (app_name): " + resources.getString(resources.getIdentifier("app_name", "string", context.getPackageName())));
        
        return updatedContext;
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Log.d("LocaleHelper", "Setting legacy locale to: " + language);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        
        Log.d("LocaleHelper", "Updated legacy context locale to: " + resources.getConfiguration().locale.getLanguage());
        Log.d("LocaleHelper", "Test string (app_name): " + resources.getString(resources.getIdentifier("app_name", "string", context.getPackageName())));

        return context;
    }
}