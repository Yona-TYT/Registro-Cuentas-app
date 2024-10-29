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
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.List;

public class Basic {
    private Context mContex;
    public static boolean isDow = true;
    public static boolean isUp = false;

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
                    isDow = false;
                    isUp = true;
                    //Toast.makeText(MainActivity.this, "Keyboard is +", Toast.LENGTH_LONG).show();
                }
                else {
                    isDow = true;
                    isUp = false;
                    //Toast.makeText(mContex, "Keyboard is -", Toast.LENGTH_LONG).show();

                    if(opt == 0) {
                        elm.clearFocus();
                    }
                }
            }
        });
    }
    public void  steAllKeyEvent(ConstraintLayout mConstrain, List<EditText> mInputList){
        for(int i = 0; i < mInputList.size(); i++) {
            // Para eventos al mostrar o ocultar el teclado
            keyboardEvent(mConstrain, mInputList.get(i), 0); //opt = 0 is clear elm focus
            //-------------------------------------------------------------------------------------
        }
    }

    public void setAllfocusEvent(View elm, List<EditText> mInputList){
        for(int i = 0; i < mInputList.size(); i++) {
            mInputList.get(i).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    //Toast.makeText(mContext, "Siz is "+b, Toast.LENGTH_LONG).show();
                    if (b) {
                        elm.setVisibility(View.INVISIBLE);
                    }
                    else {
                        elm.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public static float getValue(String value){
        float precDoll = 0;//floatFormat(value);
        float number = 0;//Float.parseFloat(value);

        if(SatrtVar.mCurrency == 1){    //Selector en Bs
            number = number*precDoll;
        }
        return number;
    }

    public static String setValue(String value){
        float precDoll = 0;//floatFormat(value);
        float number = 0;//Float.parseFloat(value);

        if(SatrtVar.mCurrency == 1){    //Selector en Bs
            number = number/precDoll;
        }
        return Float.toString(number);
    }

    public static Float floatFormat(String value){
        return Float.parseFloat(SatrtVar.mDollar.replaceAll("([^.;^0-9]+)", ""));
    }


    public static String setMask(String value){

        return value.endsWith(" Bs")? value : value+" Bs";
    }
}
