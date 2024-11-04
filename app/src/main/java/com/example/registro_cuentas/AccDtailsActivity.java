package com.example.registro_cuentas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class AccDtailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext = BaseContext.getContext();

    // DB ----------------------------------------------------------------
    private AppDBacc appDBcuenta = StartVar.appDBcuenta;
    private AppDBclt appDBcliente = StartVar.appDBcliente;
    private AppDBfec appDBfecha = StartVar.appDBfecha;

    private List<Cuenta> listCuenta;
    private List<AppDBreg> appDBregistro = StartVar.appDBregistro;
    private List<Registro> listRegistro;
    //--------------------------------------------------------------------

    //Todos los View
    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private TextView mText4;
    private List<TextView> mTextList = new ArrayList<>();
    //---------------------------------------------------------------------

    //Todos los Spinner
    private Spinner mSpin1;
    private Spinner mSpin2;
    private Spinner mSpin3;

    private int currSel1 = StartVar.mCurrency;
    private int currSel2 = StartVar.mCurrentAcc;
    private int currSel3 = StartVar.mCurreMes;

    //---------------------------------------------------------------------

    private Switch mSw;
    private boolean swDel = false;
    private String mUser = "";
    private Button mBtton1;
    private Button mBtton2;

    public int payIndex = StartVar.payIndex;
    public int accIndex = StartVar.mCurrentAcc;

    private List<String> mCurrencyList= Arrays.asList("$", "Bs");
    private int mCindex = StartVar.mCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Se configura el Boton nav Back -----------------------------------------------
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AccDtailsActivity.this.finish();
            }
        };
        onBackPressedDispatcher.addCallback(this, callback);
        //---------------------------------------------------------------------------------

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_acc_dts);

        //Activate ToolBar ----------------------------------------------------------------
        Toolbar myToolbar = findViewById(R.id.toolbar_adts);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Detalles de Cuenta");
        actionBar.setDisplayShowHomeEnabled(true);

        myToolbar.setTitleTextColor(ContextCompat.getColor(myToolbar.getContext(), R.color.inner_button));
        //------------------------------------------------------------------------------------------

        mSpin1 = findViewById(R.id.spin_adts1);
        mText1 = findViewById(R.id.txview_adts1);
        mText2 = findViewById(R.id.txview_adts2);
        mText3 = findViewById(R.id.txview_adts3);
        mText4 = findViewById(R.id.txview_adts4);

        mBtton1 = findViewById(R.id.butt_adts1);
        mBtton2  = findViewById(R.id.butt_adts2);
        mSw = findViewById(R.id.sw_adts1);

        mBtton1.setOnClickListener(this);
        mBtton2.setOnClickListener(this);
        mSw.setOnClickListener(this);

        mTextList.add(mText1);
        mTextList.add(mText2);
        mTextList.add(mText3);
        mTextList.add(mText4);

        // Se llenan los textView
        setInputList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mSw.setFocusedByDefault(false);
        }

        //Para la lista del selector Fechas ----------------------------------------------------------------------------------------------
        // Genera la lista de fechas ---------------------------------------------------------
        List<String> fechaList = new ArrayList<>();
        fechaList.add("Todos");
        List<Fecha> listFecha = appDBfecha.daoUser().getUsers();
        for (int i = 0; i < listFecha.size(); i++){
            String mes = listFecha.get(i).mes;
            String year = listFecha.get(i).year;
            fechaList.add(mes+" ("+year+")");
        }
        SelecAdapter adapt1 = new SelecAdapter(mContext, fechaList);
        mSpin1.setAdapter(adapt1);
        mSpin1.setSelection(currSel1); //Set La fecha default
        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel1 = i;
                appDBcuenta.daoUser().updateCurrentFec(StartVar.saveDataName, i);
                StartVar mVars = new StartVar(mContext);
                mVars.setCurrentMes(i);

                // Se llenan los textView
                setInputList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //--------------------------------------------------------------------------------------------

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    // MenuToolbar boton back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    public void setInputList(){
        if(!appDBregistro.isEmpty()) {
            CalcCalendar cale = new CalcCalendar();
            listRegistro = appDBregistro.get(accIndex).daoUser().getUsers();

            float totalCred = 0;
            float totalDeb = 0;

            listRegistro = appDBregistro.get(currSel2).daoUser().getUsers();
            List<Fecha> listFecha = appDBfecha.daoUser().getUsers();
            int idx = currSel1 - 1;
            Fecha selFecha = listFecha.get(Math.max(idx, 0));

            for(int i = 0; i<listRegistro.size(); i++){
                Registro reg = listRegistro.get(i);
                String fecha = reg.fecha;

                if(currSel1 == 0) {
                    if (reg.oper == 0) {
                        String mon = reg.monto;
                        totalCred += mon.isEmpty() ? 0 : Float.parseFloat(mon);
                    } else {
                        String mon = reg.monto;
                        totalDeb += mon.isEmpty() ? 0 : Float.parseFloat(mon);
                    }
                }
                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalDate date = LocalDate.parse(fecha);
                        if(date.getMonth().toString().equals(selFecha.mes)){
                            if (reg.oper == 0) {
                                String mon = reg.monto;
                                totalCred += mon.isEmpty() ? 0 : Float.parseFloat(mon);
                            } else {
                                String mon = reg.monto;
                                totalDeb += mon.isEmpty() ? 0 : Float.parseFloat(mon);
                            }
                        }
                    }
                }
            }
            Cuenta mCuenta = appDBcuenta.daoUser().getUsers().get(accIndex+1);

            String txName = mCuenta.nombre;
            String txDesc = mCuenta.desc;
            String txMont = mCuenta.monto;

            int i = 0;
            mTextList.get(i).setText("Cuenta: " + txName + " ("+txDesc+")");
            i++;
            mTextList.get(i).setText("Monto Estimado: " + txMont);
            i++;
            mTextList.get(i).setText("Total Credito: "+ totalCred);
            i++;
            mTextList.get(i).setText("Total Debito: " + (totalDeb!=0?"-":"") + totalDeb);

        }
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.sw_adts1){
            swDel = !swDel;
            if(swDel) {
                mBtton1.setEnabled(true);
            }
            else{
                mBtton1.setEnabled(false);
            }
        }
        if (itemId == R.id.butt_adts1){
//            //fmang.RemoveFile(saveImage, this.getContentResolver());
//
//            //Elimina el registro selecionado
//            appDBregistro.get(accIndex).daoUser().removerUser(mUser);
//            SatrtVar mVars = new SatrtVar(mContext);
//            //Recarga La lista de la DB ----------------------------
//            mVars.getCltListDB();
//            //-------------------------------------------------------
//
//            Intent mIntent = new Intent(this, MainActivity.class);
//            startActivity(mIntent);
//            finish(); //Finaliza la actividad y ya no se accede mas
        }
        if (itemId == R.id.butt_adts2){
            Intent mIntent = new Intent(this, AccEditActivity.class);
            startActivity(mIntent);
        }
    }
    //------------------------------------------------------------
}