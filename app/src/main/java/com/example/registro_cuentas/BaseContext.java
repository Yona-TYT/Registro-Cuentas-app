package com.example.registro_cuentas;

import android.content.Context;

public final class BaseContext {

    private static Context CONTEXT;

    public static void initialise(Context context) {
        CONTEXT = context;
    }

    public static Context getContext() {
        synchronized (BaseContext.class) {
            if (BaseContext.CONTEXT == null)
                throw new NullPointerException("Base Context not initialised. Call BaseContext.initialise()");
            else
                return BaseContext.CONTEXT.getApplicationContext();
        }
    }
}