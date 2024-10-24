package com.example.registro_cuentas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PayAdapter extends BaseAdapter implements Filterable, View.OnClickListener{
    //Test------------------------------------------------------------
    private Context mContex;

    private List<String[]> textList = new ArrayList<>();
    private List<String[]>  currList = new ArrayList<>(); // Original Values

    private ArrayList<Integer> newList = new ArrayList<>();    // Values to be displayed

    public  PayAdapter(Context mContex, List<String[]> textList){
        this.mContex = mContex;
        this.textList = textList;
        this.currList = textList;
    }

    @Override
    public int getCount(){
        return newList.size();
    }

    @Override
    public Object getItem(int pos){
        return newList;
    }

    @Override
    public long getItemId(int i) {  return newList.get(i);  }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int pos, View convertView, ViewGroup parent){

        Log.d("PhotoPicker", "Ya hay ? 11111------------------------: "+ newList.size() + " ::" + pos);
        TextView text = new TextView(mContex);
        Button butt = new Button(mContex);
        LinearLayout layout = new LinearLayout(mContex);
        Basic mBasic = new Basic(mContex);
        int idx = newList.get(pos);

        // Se ajustan los parametros del Boton ----------------------------------
        butt.setId(R.id.butt_paylist);
        butt.setTag(idx);
        butt.setText("Detalles");
        butt.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams buttParams = new LinearLayout.LayoutParams(mBasic.getPixelSiz(R.dimen.button_ws), mBasic.getPixelSiz(R.dimen.button_h1));
        buttParams.gravity = Gravity.CENTER;
        butt.setLayoutParams(buttParams);
        butt.setTextSize(mBasic.getFloatSiz(R.dimen.inner_text_2));
        butt.setPadding(1, 1, 1, 1);
        butt.setOnClickListener(this);
        layout.addView(butt);
        //-----------------------------------------------------------------------
        // Se ajustan los parametros del Texto ----------------------------------
        text.setText(textList.get(idx)[0] + " " + textList.get(idx)[1]);
        text.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(mBasic.getPixelSiz(R.dimen.button_ws), mBasic.getPixelSiz(R.dimen.button_h1));
        textParams.gravity = Gravity.CENTER;
        text.setLayoutParams(textParams);

        text.setTextSize(mBasic.getFloatSiz(R.dimen.inner_text_2));
        text.setPadding(2, 10, 2, 2);
        layout.addView(text);

        //-----------------------------------------------------------------------

        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setVisibility(View.VISIBLE);
        layout.setPadding(2,2,2,2);

        return layout;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Integer> FilteredArrList = new ArrayList<Integer>();
                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                //Log.d("PhotoPicker", "Constrain ------------------------: " + constraint);
                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    for (int i = 0; i < currList.size(); i++) {
                        FilteredArrList.add(i);
                    }
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < currList.size(); i++) {
                        String data = currList.get(i)[0];
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(i);
                            //Log.d("PhotoPicker", "Constrain ------------------------: " + i);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                // Log.d("PhotoPicker", "11111------------------------: " + FilteredArrList.size());
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //Log.d("PhotoPicker", "2222------------------------: " +constraint);
                newList = (ArrayList<Integer>) results.values;   // has the filtered values
                notifyDataSetChanged();                         // notifies the data with new filtered values
            }
        };
        return filter;
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();

        if(itemId == R.id.butt_paylist) {
            int idx = (int)view.getTag();
            Toast.makeText(mContex, "Siz is " + idx, Toast.LENGTH_LONG).show();
        }
    }
}