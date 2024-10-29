package com.example.registro_cuentas;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.AttributeSet;
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

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PayAdapter extends BaseAdapter implements Filterable, View.OnClickListener{
    //Test------------------------------------------------------------
    private Context mContex;
    private CalcCalendar cale = new CalcCalendar();
    private Basic mBasic;

    private List<String[]> textList = new ArrayList<>();
    private List<String[]>  currList = new ArrayList<>(); // Original Values
    private List<String> mCurrencyList= Arrays.asList("$", "Bs");
    private int mCindex = SatrtVar.mCurrency;

    private ArrayList<Integer> newList = new ArrayList<>();    // Values to be displayed

    public  PayAdapter(Context mContex, List<String[]> textList){
        this.mContex = mContex;
        this.textList = textList;
        this.currList = textList;

        mBasic = new Basic(mContex);
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
        TextView text1 = new TextView(mContex);
        TextView text2 = new TextView(mContex);

        int buttonStyle = R.style.Theme_RegistroCuentas;

        Button butt = new Button(new ContextThemeWrapper(mContex, buttonStyle));
        LinearLayout layout = new LinearLayout(mContex);
        int idx = newList.get(pos);

        // Se ajustan los parametros del Boton ----------------------------------
        butt.setId(R.id.butt_paylist);
        butt.setTag(idx);
        butt.setText("+");
        butt.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams buttParams = new LinearLayout.LayoutParams(mBasic.getPixelSiz(R.dimen.button_wss), mBasic.getPixelSiz(R.dimen.button_h1));
        buttParams.gravity = Gravity.CENTER;
        butt.setLayoutParams(buttParams);
        butt.setTextSize(mBasic.getFloatSiz(R.dimen.inner_text_2));
        butt.setPadding(1, 1, 1, 1);
        butt.setOnClickListener(this);
        layout.addView(butt);
        //-----------------------------------------------------------------------
        // Se ajustan los parametros del Texto ----------------------------------
        String txName = textList.get(idx)[1];
        String txMont = Basic.getValue(textList.get(idx)[2]) + " "+ mCurrencyList.get(mCindex);
        String txFech = textList.get(idx)[3];
        String txHora = cale.getTime(textList.get(idx)[4]);
        text1.setText( " "+txMont  +" " +txName  );
        text1 = setTextView(text1);
        layout.addView(text1);

        text2.setText( txFech  );
        text2 = setTextView(text2);
        layout.addView(text2);
        //-----------------------------------------------------------------------

        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setVisibility(View.VISIBLE);
        layout.setPadding(2,2,2,2);

        return layout;
    }

    public TextView setTextView(TextView view){
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setTextColor(ContextCompat.getColor(view.getContext(), R.color.text_color1));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(mBasic.getPixelSiz(R.dimen.txview_ws), mBasic.getPixelSiz(R.dimen.button_h1));
        textParams.gravity = Gravity.CENTER;
        view.setLayoutParams(textParams);
        view.setTextSize(mBasic.getFloatSiz(R.dimen.inner_text_2));
        view.setMaxLines(1);
        view.setPadding(2, 15, 2, 2);
        return view;
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
                else{
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < currList.size(); i++) {
                        String data = currList.get(i)[1];
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
            SatrtVar satrtVar = new SatrtVar(mContex);
            satrtVar.setPayIndex( (int)view.getTag());

            Application application = (Application) mContex.getApplicationContext();
            Intent mIntent = new Intent(mContex, DetailsActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(mIntent);
//            int idx = (int)view.getTag();
//            Toast.makeText(mContex, "Siz is " + idx, Toast.LENGTH_LONG).show();



        }
    }
}