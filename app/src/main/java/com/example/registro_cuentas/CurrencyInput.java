package com.example.registro_cuentas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionValues;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("RestrictedApi")
public class CurrencyInput implements View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener, View.OnTouchListener {
    private Context mContext;
    private MaskedEditText mInput;
    private List<View> mViewList = new ArrayList<>();
    private int mOpt = 0;
    private boolean allSelec = false;

    //Opt 0 = Input Precio Dolar
    //Opt 1 = Input Monto
    public CurrencyInput(Context mContex, MaskedEditText mInput, List<View> mViewList, int mOpt){
        this.mContext = mContex;
        this.mInput = mInput;
        this.mViewList = mViewList;
        this.mOpt = mOpt;
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
            String setTextEdit = mInput.getText().toString().trim();
            @SuppressLint("SetTextI18n")
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                int len = s.length();
                if (s.toString().endsWith(" Bs")) {
                    mInput.setSelection(len - 3);
                }
                if(i2 >= (len-3)){
                    mInput.setCursorVisible(false);
                }
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                String value = mInput.getText().toString();
                if(value.isEmpty()){
                    mInput.setText(Basic.setMask("0"));
                }
                else if (!value.endsWith(" Bs")){
                    mInput.setText(Basic.setMask(value));
                    mInput.setSelection(0);
                }

                if (mOpt == 0) {
                    // Actualiza y guarda el Precio del dolar ------------------------
                    SatrtVar.appDBcuenta.daoUser().updateDollar(SatrtVar.saveDataName, value);
                }

                SatrtVar mVars = new SatrtVar(mContext);
                mVars.setDollar(value);

                //----------------------------------------------------------------------------------
            }
        });
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        //Toast.makeText(mContext, "Siz is "+b, Toast.LENGTH_LONG).show();
        TransitionValues MaskedEditText;
        if(b) {
            String value = mInput.getText().toString();
            if (!value.endsWith(" Bs")) {
                mInput.setText(Basic.setMask(value));
            }
            mInput.setCursorVisible(false);

            if (mOpt == 0) {
                for (View mView : mViewList) {
                    mView.setVisibility(View.INVISIBLE);
                }
            }
        }
        else{
            if (mOpt == 0) {
                for (View mView : mViewList) {
                    mView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        String value = mInput.getText().toString();
        int siz = value.length();

        if (value.endsWith(" Bs") &&  mInput.getSelectionEnd() !=0) {
            if(allSelec){
                mInput.clearFocus();
                mInput.requestFocus();
                allSelec = false;
                mInput.setSelection((siz - 3), 0);
                mInput.setCursorVisible(true);
            }
            else {
                mInput.setSelection(siz - 3);
                mInput.setCursorVisible(true);
            }
        }
        if(keyEvent.getKeyCode() == 67) {
            if(siz<=3){
                mInput.setText(Basic.setMask("0"));
                mInput.setSelection( (siz - 2),0);
            }

            //Para eliminar elementos seleccionados
            int start = mInput.getSelectionStart();
            int end = mInput.getSelectionEnd();

            int max = end;//Math.max(start, end);
            int min = start;

            value = value.replaceAll("([^.;^0-9]+)", "");
            char[] mlist = value.toCharArray();
            StringBuilder resut = new StringBuilder();
            for (int j = 0; j < mlist.length; j++){
                if(j > min && j < max ){
                    continue;
                }
                resut.append(mlist[j]);
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        String value = mInput.getText().toString().trim();
        if (value.endsWith(" Bs")) {
            int siz = value.length();
            mInput.setSelection(value.length() - 3);
            value = value.replaceAll("([^.;^0-9]+)", "");
            if(value.isEmpty()){
                mInput.setSelection(0);
            }
            else {
                mInput.setCursorVisible(false);
                mInput.setSelection( (siz - 3),0);
            }
        }
        else  if (value.endsWith("Bs")) {
            mInput.setSelection(value.length() - 2);
        }
        if(mInput.hasFocus()){
            mInput.setCursorVisible(true);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
            if (mInput.hasSelection()) {
                allSelec = true;
            }
        }
        return false;
    }
}