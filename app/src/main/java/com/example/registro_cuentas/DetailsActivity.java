package com.example.registro_cuentas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private  Context mContext = BaseContext.getContext();

    // DB ----------------------------------------------------------------
    private AppDBacc appDBcuenta = SatrtVar.appDBcuenta;
    private AppDBclt appDBcliente = SatrtVar.appDBcliente;
    private List<Cuenta> listCuenta;
    private List<AppDBreg> appDBregistro = SatrtVar.appDBregistro;
    private List<Registro> listRegistro;
    //--------------------------------------------------------------------

    //Todos los View
    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private TextView mText4;
    private TextView mText5;
    private List<TextView> mTextList = new ArrayList<>();
    //---------------------------------------------------------------------

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch mSw;
    private boolean swDel = false;
    private String mUser = "";
    private Button mBtton1;
    private Button mBtton2;

    public int payIndex = SatrtVar.payIndex;
    public int accIndex = SatrtVar.mCurrenrAcc;

    private List<String> mCurrencyList= Arrays.asList("$", "Bs");
    private int mCindex = SatrtVar.mCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Se configura el Boton nav Back -----------------------------------------------
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                DetailsActivity.this.finish();
            }
        };
        onBackPressedDispatcher.addCallback(this, callback);
        //---------------------------------------------------------------------------------

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);

        //Activate ToolBar ----------------------------------------------------------------
        Toolbar myToolbar = findViewById(R.id.toolbar_dts);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Detalles de Pago");
        actionBar.setDisplayShowHomeEnabled(true);

        myToolbar.setTitleTextColor(ContextCompat.getColor(myToolbar.getContext(), R.color.inner_button));
        //------------------------------------------------------------------------------------------

        mText1 = findViewById(R.id.txview_dts1);
        mText2 = findViewById(R.id.txview_dts2);
        mText3 = findViewById(R.id.txview_dts3);
        mText4 = findViewById(R.id.txview_dts4);
        mText5 = findViewById(R.id.txview_dts5);

        mBtton1 = findViewById(R.id.butt_dts1);
        mBtton2  = findViewById(R.id.butt_dts2);
        mSw = findViewById(R.id.sw_dts1);

        mBtton1.setOnClickListener(this);
        mBtton2.setOnClickListener(this);
        mSw.setOnClickListener(this);

        mTextList.add(mText1);
        mTextList.add(mText2);
        mTextList.add(mText3);
        mTextList.add(mText4);
        mTextList.add(mText5);

        // Se llenan los textView
        setInputList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mSw.setFocusedByDefault(false);
        }


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
    //------------------------------------------------------------

    @SuppressLint("SetTextI18n")
    public void setInputList(){
        if(!appDBregistro.isEmpty()) {
            CalcCalendar cale = new CalcCalendar();
            listRegistro = appDBregistro.get(accIndex).daoUser().getUsers();
            Registro reg = listRegistro.get(payIndex);
            mUser = reg.registro;
            String txName = reg.nombre;
            String txAlias = appDBcliente.daoUser().getSaveAlias(reg.cltid);
            String txConc = reg.concep;
            String txMont = reg.monto;
            String txOpt = (reg.oper==0?"+ ":"- ");
            String txFech = reg.fecha;
            String txHora = cale.getTime(reg.time);

            int i = 0;
            mTextList.get(i).setText("Cliente: " + txName + " ("+txAlias+")");
            i++;
            mTextList.get(i).setText("Concepto: " + txConc);
            i++;
            mTextList.get(i).setText("Monto: "+txOpt+ Basic.getValue(txMont)+ " "+mCurrencyList.get(mCindex));
            i++;
            mTextList.get(i).setText("Fecha: "+ txFech);
            i++;
            mTextList.get(i).setText("Hora: "+ txHora);
        }
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.sw_dts1){
            swDel = !swDel;
            if(swDel) {
                mBtton1.setEnabled(true);
            }
            else{
                mBtton1.setEnabled(false);
            }
        }
        if (itemId == R.id.butt_dts1){
            //fmang.RemoveFile(saveImage, this.getContentResolver());

            //Elimina el registro selecionado
            appDBregistro.get(accIndex).daoUser().removerUser(mUser);
            SatrtVar mVars = new SatrtVar(mContext);
            //Recarga La lista de la DB ----------------------------
            mVars.getCltListDB();
            //-------------------------------------------------------

            Intent mIntent = new Intent(this, MainActivity.class);
            startActivity(mIntent);
            finish(); //Finaliza la actividad y ya no se accede mas

        }
    }
}