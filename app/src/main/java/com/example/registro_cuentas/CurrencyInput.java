package com.example.registro_cuentas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionValues;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("RestrictedApi")
public class CurrencyInput implements View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener, View.OnTouchListener {
    private Context mContext;
    private List<View> mViewList = new ArrayList<>();
    private String sing = "";
    private int mOpt = 0;
    private boolean allSelec = false;

    public static boolean isTouch = true;
    @SuppressLint("StaticFieldLeak")
    public static EditText mInput;


    //Opt 0 = Input Precio Dolar
    //Opt 1 = Input Monto
    public CurrencyInput(Context mContex, EditText mInput, List<View> mViewList, String sing, int mOpt ){
        this.mContext = mContex;
        CurrencyInput.mInput = mInput;
        this.mViewList = mViewList;
        this.sing = sing;
        this.mOpt = mOpt;
    }

    public EditText get() {
        return mInput;
    }

        @SuppressLint("ClickableViewAccessibility")
    public void set() {
        mInput.setOnClickListener(this);
        mInput.setOnFocusChangeListener(this);
        mInput.setOnKeyListener(this);
        mInput.setOnTouchListener(this);
        mInput.setSelectAllOnFocus(false);

        mInput.setSelection(0); // After initialization keep cursor on right side
        mInput.setCursorVisible(true);// Disable the cursor.
        mInput.addTextChangedListener(new TextWatcher() {
            @SuppressLint("SetTextI18n")
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s != null && s.length() > 2) {
                    int siz = s.length();
                    if (s.toString().startsWith(".")) {
                        StringBuilder sb = new StringBuilder(s);
                        sb.insert(0, "0");
                        mInput.setText(sb.toString());
                    }
                    if (s.toString().endsWith(" " + sing)) {
                        int opt = 2; //mInput.setSelection( (siz - 3),0);
                        setInputSelec(siz, opt);
                    }
    //                if(i2 >= (siz-(sing.length()+1))){
    //                    mInput.setCursorVisible(false);
    //                }
                }
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                String value = mInput.getText().toString();
                if(value.isEmpty()){
                    mInput.setText(Basic.setMask("0", sing));
                }
                else if (!value.endsWith(" "+sing)){
                    mInput.setText(Basic.setMask(value, sing));
                    mInput.setSelection(0);
                }

                if (mOpt == 1) {
                    // Actualiza y guarda el Precio del dolar ------------------------
                    StartVar.appDBcuenta.daoUser().updateDollar(StartVar.saveDataName, value);
                    StartVar mVars = new StartVar(mContext);
                    mVars.setDollar(value);
                    //----------------------------------------------------------------------------------
                }
            }
        });
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        //Toast.makeText(mContext, "Siz is "+b, Toast.LENGTH_LONG).show();
        TransitionValues MaskedEditText;
        if(b) {
            String value = mInput.getText().toString();
            if (!value.endsWith(" "+sing)) {
                mInput.setText(Basic.setMask(value, sing));
            }
//            mInput.setCursorVisible(false);

            for (View mView : mViewList) {
                mView.setVisibility(View.INVISIBLE);
            }
        }
        else{
            //Toast.makeText(mContext, "Aqui hayyyyyyyy?  " , Toast.LENGTH_LONG).show();
            if (Basic.isDow){
                mInput.setSelection(0);
                isTouch = true;
            }
            for (View mView : mViewList) {
                mView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        String value = mInput.getText().toString();
        int siz = value.length();
        //Toast.makeText(mContext, "Aqui hayyyyyyyy  " + keyEvent.getKeyCode() , Toast.LENGTH_LONG).show();

        if (value.endsWith(" "+sing) &&  mInput.getSelectionEnd() !=0) {
            if(allSelec){
                mInput.clearFocus();
                mInput.requestFocus();
                allSelec = false;
                int opt = 2; //mInput.setSelection( (siz - 3),0);
                setInputSelec(siz, opt);
                mInput.setCursorVisible(true);
            }
            else {
                int opt = 0; //mInput.setSelection( (siz - 3),0);
                setInputSelec(siz, opt);
                mInput.setCursorVisible(true);
            }
        }
        if(keyEvent.getKeyCode() == 67) {
            if(siz<=3){
                mInput.setText(Basic.setMask("", sing));
                mInput.setSelection(0);
            }
        }
        else if(keyEvent.getKeyCode() == 67) {

        }
        return false;
    }

    @Override
    public void onClick(View view) {
        String value = mInput.getText().toString().trim();
        if (value.endsWith(" "+sing)) {
            int siz = value.length();
            int opt = 2; //mInput.setSelection( (siz - 3),0);
            setInputSelec(siz, opt);
            value = value.replaceAll("([^.;^0-9]+)", "");
            if(value.isEmpty()){
                mInput.setSelection(0);
            }
            else {
//                mInput.setCursorVisible(false);
                //int opt = 2; //mInput.setSelection( (siz - 3),0);
                setInputSelec(siz, opt);
            }
        }
        else  if (value.endsWith(sing)) {
            int opt = 1; //mInput.setSelection(value.length() - 2);
            setInputSelec(value.length(), opt);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
            String value = mInput.getText().toString().trim();
            if (mInput.hasSelection()) {
                allSelec = true;
                return false;
            }

            int start =mInput.getSelectionStart();
            int end = mInput.getSelectionEnd();

            if(start != end){
                mInput.clearFocus();
                mInput.requestFocus();
//                mInput.setCursorVisible(false);
            }
            //Toast.makeText(mContext, "Aqui hayyyyyyyy  " + Basic.isDow+ "  " + isTouch, Toast.LENGTH_LONG).show();

            if (isTouch){
                //Toast.makeText(mContext, "Aqui hayyyyyyyy  " + isDow+ "  " + start, Toast.LENGTH_LONG).show();

                if (value.endsWith(" " + sing)) {
                    mInput.clearFocus();
                    mInput.requestFocus();
                    int opt = 2; //mInput.setSelection((value.length() - 3), 0);
                    setInputSelec(value.length(), opt);
                    mInput.setCursorVisible(false);
                }
                isTouch = false;
            }
        }
        return false;
    }

    void setInputSelec(int siz, int opt) {
        if (opt == 0) {
            mInput.setSelection(siz - (sing.length() + 1));
        }
        else if (opt == 1) {
            mInput.setSelection(siz - sing.length());
        }
        else if (opt == 2) {
            mInput.setSelection((siz - (sing.length() + 1)), 0);
        }
    }
}