package com.example.registro_cuentas;

import android.app.Application;
import android.content.Context;

import androidx.work.WorkManager;

public class AppContextProvider extends Application {
    private static AppContextProvider sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static AppContextProvider getInstance() {
        return sInstance;
    }

    public static Context getContext() {
        if (sInstance == null) {
            throw new IllegalStateException("AppContextProvider no ha sido inicializado. " +
                    "¿Olvidaste registrarlo en el AndroidManifest.xml?");
        }
        return sInstance.getApplicationContext();
    }
}