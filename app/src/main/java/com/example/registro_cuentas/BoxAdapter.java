package com.example.registro_cuentas;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filterable;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class BoxAdapter extends BaseAdapter implements View.OnClickListener{
    private Context mContex;

    private List<Object[]> textList = new ArrayList<>();

    private ArrayList<Integer> newList = new ArrayList<>();    // Values to be displayed

    public BoxAdapter(Context mContex, List<Object[]> textList){
        this.mContex = mContex;
        this.textList = textList;
    }

    @Override
    public int getCount(){
        return textList.size();
    }

    @Override
    public Object getItem(int pos){
        return textList.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){

        Log.d("PhotoPicker", "Ya hay ? 11111------------------------: "+ textList.size());
        CheckBox check = new CheckBox(mContex);
        check.setChecked((Boolean)textList.get(pos)[1]);
        LinearLayout layout = new LinearLayout(mContex);

        check.setId(R.id.check_acclist);
        check.setTag(textList.get(pos)[0]);

        // Se ajustan los parametros del Texto ----------------------------------
        check.setText((String)textList.get(pos)[2]);
        check.setTypeface(Typeface.DEFAULT_BOLD);
        check.setGravity(Gravity.CENTER);
        check.setWidth(R.dimen.spinner_w1);
        check.setMaxLines(1);
        check.setTextColor(ContextCompat.getColor(check.getContext(), R.color.text_color1));
        check.setBackgroundColor(ContextCompat.getColor(check.getContext(), R.color.text_background2));
        check.setPadding(10,5,10,5);
        check.setOnClickListener(this);
        layout.addView(check);

        //-----------------------------------------------------------------------

        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setVisibility(View.VISIBLE);

        return layout;
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if(itemId == R.id.check_acclist) {
            CheckBox check =  (CheckBox)view;
            DaoClt daoClt = StartVar.appDBcliente.daoUser();
            int cltId = StartVar.cltIndex;
            Cliente mClt = daoClt.getUsers("cltID"+cltId);

            int idx = (int)view.getTag();

            int siz = 0;
            int add = 0;
            for (int i = 0; i < idx; i+=32){

                int mByte = 0x0;
                int currBit = Basic.bitL(0x1, idx-1);
                List<Integer> bitList = Basic.getBits(mClt.bits);

                if(siz < bitList.size()){
                    mByte = bitList.get(siz);
                    if(check.isChecked()) {
                        //Basic.msg(String.format("+ %x ", mByte | currBit));
                        bitList.set(siz, (mByte | currBit));
                    }
                    else {
                        //Basic.msg(String.format("- %x ", mByte ^ currBit));
                        bitList.set(siz, (mByte ^ currBit));
                    }
                    daoClt.updateBits(mClt.cliente, Basic.saveBits(bitList));
                }


//                if(x == 32){
//                    x = 0;
//                    siz ++;
//                    if(siz < testBit.length){
//                        mByte = Basic.toHex(testBit[siz]);
//                    }
//                    else{
//                        mByte = 0x0;
//                    }
//                }
//                Object[] stList= new Object[3];
//                stList[0] = i;
//                stList[1] = Basic.bitR(mByte, x) == 1;
//                List<Integer> list = Basic.getBits(i);
//
//                stList[2] = String.format("%s", Basic.bitR(mByte, x) == 1);//Basic.saveBits(list);//mAcc.get(i).nombre;
//                maccList.add(stList);

                add+=32;
                siz++;

            }
        }
    }
}
