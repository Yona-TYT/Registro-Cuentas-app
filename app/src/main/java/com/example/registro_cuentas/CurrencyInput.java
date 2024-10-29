package com.example.registro_cuentas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionValues;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.ArrayList;
import java.util.List;

public class CurrencyInput implements View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener{
    private Context mContext;
    private MaskedEditText mInput;
    private List<View> mViewList = new ArrayList<>();
    private int mOpt = 0;

    //Opt 0 = Input Precio Dolar
    //Opt 1 = Input Monto
    public CurrencyInput(Context mContex, MaskedEditText mInput, List<View> mViewList, int mOpt){
        this.mContext = mContex;
        this.mInput = mInput;
        this.mViewList = mViewList;
        this.mOpt = mOpt;
    }

    public void set() {
        mInput.setOnClickListener(this);
        mInput.setOnFocusChangeListener(this);
        mInput.setOnKeyListener(this);

        mInput.setSelection(0); // After initialization keep cursor on right side
        mInput.setCursorVisible(true);// Disable the cursor.


        mInput.addTextChangedListener(new TextWatcher() {
            String setTextEdit = mInput.getText().toString().trim();
            @SuppressLint("SetTextI18n")
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                int len = s.length();

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String value = s.toString();
                int len = s.length();
                String maskValue = mInput.getMaskString();
                int maskSiz = 0;

                if (s.toString().endsWith(" Bs")) {
                    mInput.setSelection(len - 3);
                }

//                glValue = value.replaceAll("([^.;^0-9]+)", "");
//                glValue = glValue + " Bs";

                if(i2 >= (len-3)){
                    mInput.setCursorVisible(false);
                }
                //Log.d("PhotoPicker", " Aquiiiiiiiiii Hayyyyyy 11100------------------------: " + i +" "+i1+" "+i2 );
                //Toast.makeText(mContext, "S eses " + glValue +" "+value, Toast.LENGTH_LONG).show();


//                if(!s.toString().equals(setTextEdit)){
//                    input.removeTextChangedListener(this);
//                    String remplace = s.toString().replaceAll("[Rp. ]","");
//                    if(!remplace.isEmpty()){
//                        setTextEdit = Basic.currencyFormat(remplace);
//                    }
//                    else {
//                        setTextEdit = "";
//                    }
//                    input.setText(setTextEdit);
//                    input.addTextChangedListener(this);
//                }

            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                String value = mInput.getText().toString();
                if(value.isEmpty()){
                    mInput.setText(Basic.setMask("0"));
                }
//                if(!value1.equals(glValue) && value1.endsWith(" Bs")){
//                    input.setText(glValue);
//                }

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
    public void onClick(View view) {
        String value = mInput.getText().toString().trim();
        if (value.endsWith(" Bs")) {
            int siz = value.length();
            mInput.setSelection(value.length() - 3);
            value = value.replaceAll("([^.;^0-9]+)", "");
            if (value.equals("0")){
                mInput.setSelection( (siz - 3),0);
            }
            else if(value.isEmpty()){

                mInput.setSelection(0);
            }
            //Toast.makeText(mContext, "S keyyyyy " + value , Toast.LENGTH_LONG).show();

            //mInput1.setCursorVisible(true);
        }
        else  if (value.endsWith("Bs")) {
            mInput.setSelection(value.length() - 2);
        }

        if(mInput.hasFocus()){
            mInput.setCursorVisible(true);
        }
        //  mInput1.setSelection(0);
        // mInput1.setText("" + Basic.floatFormat(setTextEdit));

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

        //String maskValue = "";//input.getMaskString();
        // = maskValue.replaceAll("([^#]+)","");
        //Toast.makeText(mContext, "S keyyyyy " +      input.getSelectionEnd(), Toast.LENGTH_LONG).show();

        if (value.endsWith(" Bs") &&  mInput.getSelectionEnd() !=0) {
            mInput.setSelection(siz - 3);
            mInput.setCursorVisible(true);
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
            Toast.makeText(mContext, "S keyyyyy " + resut +" "+min+"  "+max, Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
