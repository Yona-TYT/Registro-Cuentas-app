package com.example.registro_cuentas;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

public class Basic {
    private Context mContex;
    public static String currText = "";
    public Basic(Context mContex){
        this.mContex = mContex;
    }

    public int getPixelSiz(int id){
        return mContex.getResources().getDimensionPixelSize(id);
    }

    public float getFloatSiz(int id){
        DisplayMetrics metrics = new DisplayMetrics();
        float scaledDensity = mContex.getResources().getDisplayMetrics().scaledDensity;
        return getPixelSiz(id) / scaledDensity;
    }

    public void keyboardEvent(ConstraintLayout mConstrain, View elm, int opt) {
        // Para eventos al mostrar o ocultar el teclado
        mConstrain.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // on below line we are creating a variable for rect
                Rect rect = new Rect();

                ConstraintLayout contain = mConstrain;

                // on below line getting frame for our relative layout.
                contain.getWindowVisibleDisplayFrame(rect);

                // on below line getting screen height for relative layout.
                int screenHeight = contain.getRootView().getHeight();

                // on below line getting keypad height.
                int keypadHeight = screenHeight - rect.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    //Toast.makeText(MainActivity.this, "Keyboard is +", Toast.LENGTH_LONG).show();
                } else {
                    if(opt == 0) {
                        //Toast.makeText(MainActivity.this, "Keyboard is -", Toast.LENGTH_LONG).show();
                        elm.clearFocus();
                    }
                }
            }
        });
    }
}
