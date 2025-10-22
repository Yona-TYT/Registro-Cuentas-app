package com.example.registro_cuentas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        mInput.clearFocus();
        mInput.setSelection(0); // After initialization keep cursor on right side
        mInput.setCursorVisible(true);// Disable the cursor.
        mInput.addTextChangedListener(new TextWatcher() {
            @SuppressLint("SetTextI18n")
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s != null && s.length() > 2) {
//                    int siz = s.length();
//                    if (s.toString().startsWith(".")) {
//                        StringBuilder sb = new StringBuilder(s);
//                        sb.insert(0, "0");
//                        mInput.setText(sb.toString());
//                    }
//                    if (s.toString().endsWith(" " + sing)) {
//                        int opt = 2; //mInput.setSelection( (siz - 3),0);
//                        setInputSelec(siz, opt);
//                    }
    //                if(i2 >= (siz-(sing.length()+1))){
    //                    mInput.setCursorVisible(false);
    //                }
                }
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {

               if (s != null) {
                   String texCopy = s.toString();


                   //Se eliminan caracteres de simbolos y el espacio
                   texCopy = texCopy.replaceAll("[^\\d,.]","");

                   String text = s.toString().replaceAll("[^\\d,.]","");
                   int txSiz = text.length();

                   int start = mInput.getSelectionStart();
                   int end = mInput.getSelectionEnd();


                   if(start > txSiz){
                       start = txSiz;
                   }

                   int max = Math.max(start, end);
                   int min = Math.min(start, end);

                   // Determina si se ingresa un . o ,
                   int siz2 = min - 1;
                   //Basic.msg("decimal? " + siz2+ " " + min + " " + max);

                   if (siz2 > -1){
                       int siz = text.replaceAll("([.,]\\d+$)", "").length();

                       char[] charsList = texCopy.toCharArray();
                       char mCh = charsList[siz2];
                       boolean isDecimal = false;
                       if(mCh == '.' || mCh == ',') {
                           if(siz != siz2 && max != (siz+1)) {
                               charsList[siz2] = '\0';
                           }
                           if(min < siz) {
                               isDecimal = true;
                           }
                           //texCopy = String.valueOf(charsList);
                       }
                       Basic.msg("decimal? " + siz+ " " + siz2 + " " + max);

                       if (isDecimal) {
                           text = String.valueOf(charsList);
                           mInput.setText(text);

                           siz = Math.min(siz+1, text.length());

                           Log.d("Test Siz---------------------", " Siz1: "+ siz+" Siz2: "+text.length());


                           mInput.setSelection(0);
                           mInput.setSelection(0, text.length()-1);
                       }

                  }
                   //------------------------------------------------------
                   text = text.replaceAll("(\\.+)",",");
                   text = text.replaceAll("(,{2,})",",");
                   text = text.replaceAll("((^,+)|(,+$))","");

                   if(texCopy.isEmpty()) {

                       text = "0,00";

                       mInput.setText(text);

                       //mInput.setSelection(1,0);

                   }
                   else {
                       Pattern patt = Pattern.compile("([.,]\\d+$)");
                       Matcher m = patt.matcher(text);
                       String txSave = "";
                       boolean isTx = false;
                       if (m.find()) {
                           String gr = m.group(1);
                           assert gr != null;
                           if (gr.length() > 2) {
                               txSave = gr.substring(0, 3);
                           } else {
                               txSave = gr;
                           }

                           text = text.replaceAll(gr, "");

                          // Basic.msg("gr" + gr+" "+text);

                       }


                       patt = Pattern.compile("(,+)");
                       m = patt.matcher(text);
                       if (m.find()) {
                           text = text.replaceAll(",+", "");

                       }
                       text = text.replaceAll("^0{2,}", "0");

                       text += (txSave.isEmpty() ? ",00" : txSave);


                       //Basic.msg("?" + txSave+" "+text);

                       if (!texCopy.equals(text)) {

                           //texCopy = texCopy.replaceAll("\\D", "");
                           if(!texCopy.equals("00")) {
                               //Basic.msg("*?" + text +"-"+ txSave+" -> "+texCopy);

                               int siz = txSave.replaceAll("\\D", "").length();


                               mInput.setText(text);
                               if (txSave.isEmpty()){
                                   mInput.setSelection(text.length() - 3);
                               }
                               else {
                                   mInput.setSelection(text.length() - siz, text.length());
                               }
                           }

                       }
                   }

               }
//
//
//                    Pattern patt = Pattern.compile("(\\d,\\d{1,2}$)");
//                    Matcher m = patt.matcher(s);
//                    String txSave = "";
//                    if(m.find()){
//                        txSave = m.group(1);
//                    }
//                    patt = Pattern.compile("([,.]{2,})|(\\d{4,}[,.])|([,.]\\d{1,2}[,.])");
//                    m = patt.matcher(s);
//                    if(m.find()){
//                        String text = s.toString();
//                        text = text.replaceAll("\\D","");
//                        assert txSave != null;
//                        text = text.replaceAll("(\\d{3})$",txSave);
//                        mInput.setText(Basic.setMask(text, sing));
//                    }
//                }
//                String value = mInput.getText().toString();
////                if(value.isEmpty()){
////                    mInput.setText(Basic.setMask("0", sing));
////                }
////                else if (!value.endsWith(" "+sing)){
////                    mInput.setText(Basic.setMask(value, sing));
////                    mInput.setSelection(0);
////                }
//
//                if (mOpt == 1) {
//                    // Actualiza y guarda el Precio del dolar ------------------------
//                    StartVar.appDBcuenta.daoUser().updateDollar(StartVar.saveDataName, value);
//                    StartVar mVars = new StartVar(mContext);
//                    mVars.setDollar(value);
//                    //----------------------------------------------------------------------------------
//                }
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
            String value = mInput.getText().toString().trim();
            if (mInput.hasSelection() && !Basic.isDow) {
                allSelec = true;
                return false;
            }

            int start =mInput.getSelectionStart();
            int end = mInput.getSelectionEnd();


            if(start != end){
                mInput.requestFocus();
                //mInput.setSelection(0);
                int opt = 2; //mInput.setSelection((value.length() - 3), 0);
                setInputSelec(value.length(), opt);
                return false;

//                mInput.setCursorVisible(false);
            }
            //Toast.makeText(mContext, "Aqui hayyyyyyyy  " + Basic.isDow+ "  " + isTouch, Toast.LENGTH_LONG).show();

            if (isTouch){
                //Basic.msg(""+start+" "+end);
                //Toast.makeText(mContext, "Aqui hayyyyyyyy  " + isDow+ "  " + start, Toast.LENGTH_LONG).show();
                if (value.endsWith(" " + sing)) {
                    mInput.clearFocus();
                    mInput.requestFocus();
                    int opt = 2; //mInput.setSelection((value.length() - 3), 0);
                    setInputSelec(value.length(), opt);
                }
                isTouch = false;
            }
        }
        else {
            String value = mInput.getText().toString().trim();



            int start =mInput.getSelectionStart();
            int end = mInput.getSelectionEnd();



            //Basic.msg(""+start+" "+end);


            mInput.requestFocus();
            if(start != end){
                return false;
            }
        }
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        //Toast.makeText(mContext, "Siz is "+b, Toast.LENGTH_LONG).show();
        if(b) {
            //mInput.setCursorVisible(false);

            String value = mInput.getText().toString();




            //Basic.msg("??1");

//            if (!value.endsWith(" "+sing)) {
//                mInput.setText(Basic.setMask(value, sing));
//            }
            for (View mView : mViewList) {
                mView.setVisibility(View.INVISIBLE);
            }
        }
        else{
            //Toast.makeText(mContext, "Aqui hayyyyyyyy?  " , Toast.LENGTH_LONG).show();
            if (Basic.isDow){
                //mInput.setSelection(0);
            }
            isTouch = true;

            for (View mView : mViewList) {
                mView.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        String value = mInput.getText().toString();
        int siz = value.length();
        //Toast.makeText(mContext, "Aqui hayyyyyyyy  " + keyEvent.getKeyCode() , Toast.LENGTH_LONG).show();
//        if (value.endsWith(" "+sing) &&  mInput.getSelectionEnd() !=0) {
//            if(allSelec){
//                mInput.clearFocus();
//                mInput.requestFocus();
//                allSelec = false;
//                int opt = 2; //mInput.setSelection( (siz - 3),0);
//                setInputSelec(siz, opt);
//                mInput.setCursorVisible(true);
//            }
//            else {
//                int opt = 0; //mInput.setSelection( (siz - 3),0);
//                setInputSelec(siz, opt);
//                mInput.setCursorVisible(true);
//            }
//        }
        int start =mInput.getSelectionStart();
        int end = mInput.getSelectionEnd();

        int max = Math.max(start, end);
        int min = Math.min(start, end);

        //Delete Key
        if(keyEvent.getKeyCode() == 67) {

           // Basic.msg("v "+value);

            if( start == end ) {
                if(start == 1){
                    mInput.setText("0,00");
                }
                if(start == (siz-2)) {
                    mInput.setSelection((siz),(siz-2));
                }
            }
            else {
                if(max == (siz-3)) {
                    mInput.setText("");
                    //mInput.setSelection(0, (siz - 2));
                    //Basic.msg("" + keyEvent.getKeyCode() + " " + min + " " + max + " " + (siz - 2));
                }


                mInput.setSelection(0);

            }



            if(siz<=3){
                //mInput.setText(Basic.setMask("", sing));
                mInput.setSelection(0);
            }
        }
        else{
                //Basic.msg("" + keyEvent.getKeyCode() + " " + min + " " + max + " " + (siz - 2));

                if(min <= (siz-2) && max == (siz-2)) {
                    mInput.setSelection(0);
                }

        }

        return false;
    }

    @Override
    public void onClick(View view) {
        //Basic.msg("??2");

        String value = mInput.getText().toString().trim();


        Pattern patt = Pattern.compile("([.,]\\d{1,2}$)");
        Matcher m = patt.matcher(value);
        int siz = 0;
        if(m.find()){
            String gr = m.group(1);
            assert gr != null;
            String txSave = gr.replaceAll("\\D","");
            siz = value.length() - txSave.length() - 1;
        }

        //mInput.setSelection(0);
        mInput.setSelection(0, siz);


        if (value.endsWith(" "+sing)) {
            //int siz = value.length();
            int opt = 2; //mInput.setSelection( (siz - 3),0);
            //setInputSelec(siz, opt);
            value = value.replaceAll("([^.;^0-9]+)", "");
            if(value.isEmpty()){
                //mInput.setSelection(0);
            }
            else {
//                mInput.setCursorVisible(false);
                //int opt = 2; //mInput.setSelection( (siz - 3),0);
                //setInputSelec(siz, opt);
            }
        }
        else  if (value.endsWith(sing)) {
            int opt = 1; //mInput.setSelection(value.length() - 2);
            //setInputSelec(value.length(), opt);
        }
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

