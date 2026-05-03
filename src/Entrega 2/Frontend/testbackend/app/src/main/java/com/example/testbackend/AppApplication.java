package com.example.testbackend;

import android.app.Application;
import android.util.Log;
import com.squareup.picasso.Picasso;

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Inicializar Picasso com configurações de debug
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.loggingEnabled(true); 
        try {
            Picasso.setSingletonInstance(builder.build());
            Log.d("APP_INIT", "Picasso inicializado com logging");
        } catch (IllegalStateException e) {
            Log.w("APP_INIT", "Picasso já inicializado");
        }
    }
}
