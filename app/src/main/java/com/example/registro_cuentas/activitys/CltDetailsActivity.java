package com.example.registro_cuentas.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.db.Cliente;
import com.example.registro_cuentas.db.Cuenta;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.db.Pagos;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.db.AllDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class CltDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    // DB ----------------------------------------------------------------
    private AllDao appDBcuenta = StartVar.appDBall;
    private List<Cuenta> listCuenta;
    private List<Pagos> appDBregistro = StartVar.appDBall.daoPay().getUsers();
    private List<Pagos> listRegistro;
    //--------------------------------------------------------------------

    //Todos los View
    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private TextView mText4;
    private TextView mText5;
    private TextView mText6;
    private List<TextView> mTextList = new ArrayList<>();
    //---------------------------------------------------------------------

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch mSw;
    private boolean swDel = false;
    private String mUser = "";
    private Button mBtton1;
    private Button mBtton2;

    public int cltIndex = StartVar.cltIndex;
    public int accIndex = StartVar.mCurrAcc;

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
                CltDetailsActivity.this.finish();
            }
        };
        onBackPressedDispatcher.addCallback(this, callback);
        //---------------------------------------------------------------------------------

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clt_details);

        //Activate ToolBar ----------------------------------------------------------------
        Toolbar myToolbar = findViewById(R.id.toolbar_cdts);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Datos del Cliente");
        actionBar.setDisplayShowHomeEnabled(true);

        myToolbar.setTitleTextColor(ContextCompat.getColor(myToolbar.getContext(), R.color.inner_button));
        //------------------------------------------------------------------------------------------

        mText1 = findViewById(R.id.txview_cdts1);
        mText2 = findViewById(R.id.txview_cdts2);
        mText3 = findViewById(R.id.txview_cdts3);
        mText4 = findViewById(R.id.txview_cdts4);
        mText5 = findViewById(R.id.txview_cdts5);
        mText6 = findViewById(R.id.txview_cdts6);

        mBtton1 = findViewById(R.id.butt_cdts1);
        mBtton2  = findViewById(R.id.butt_cdts2);

        //mBtton1.setOnClickListener(this);
        mBtton2.setOnClickListener(this);

        mTextList.add(mText1);
        mTextList.add(mText2);
        mTextList.add(mText3);
        mTextList.add(mText4);
        mTextList.add(mText5);
        mTextList.add(mText6);

        // Se llenan los textView
        setTextViewList();

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
    public void setTextViewList(){

        String mAccName = "";
        List<Cuenta> mAccList = StartVar.appDBall.daoAcc().getUsers();
        if (!mAccList.isEmpty()) {
            Cuenta mAcc = mAccList.get(accIndex);
            mAccName = mAcc.nombre + (mAcc.desc.replaceAll("[^a-zA-Z0-9]", "").isEmpty()? "" : " ("+mAcc.desc+")");
        }

        List<Cliente> mCltList = StartVar.appDBall.daoClt().getUsers();
        Cliente mClt = mCltList.get(cltIndex);

        listRegistro = StartVar.appDBall.daoPay().getUsers();

        float cred = 0;
        float debi = 0;
        int totalPay = 0;
        for (Pagos r : listRegistro){
            if(r.cltid.equals(mClt.cliente)){
                totalPay++;
                if(r.oper == 0){
                    cred += Basic.getConverteValue(r.monto);
                }
                else {
                    debi -= Basic.getConverteValue(r.monto);
                }
            }
        }

        String txName = mClt.nombre;
        String txAlias = mClt.alias;


        String txOpt = (mClt.oper==0?"+ ":"- ");
        String txFech = mClt.ulfech;

        int i = 0;
        mTextList.get(i).setText("Cuenta Actual: "+mAccName);
        i++;
        mTextList.get(i).setText("Cliente: " + txName + (txAlias.replaceAll("[^a-zA-Z0-9]", "").isEmpty()? "" : " ("+txAlias+")"));
        i++;
        mTextList.get(i).setText("Ultimo Pago: " +txFech);
        i++;
        mTextList.get(i).setText("Pagos Totales: "+ totalPay);
        i++;
        mTextList.get(i).setText("Total Credito: "+ cred+mCurrencyList.get(mCindex));
        i++;
        mTextList.get(i).setText("Total Debito: "+ debi+mCurrencyList.get(mCindex));
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
            //appDBregistro.get(accIndex).removerUser(mUser);
            StartVar mVars = new StartVar(this);
            //Recarga La lista de la DB ----------------------------
            mVars.getCltListDB();
            //-------------------------------------------------------

            Intent mIntent = new Intent(this, MainActivity.class);
            startActivity(mIntent);
            finish(); //Finaliza la actividad y ya no se accede mas
        }
        if (itemId == R.id.butt_cdts2){
            Intent mIntent = new Intent(this, CltEditActivity.class);
            this.finish();
            startActivity(mIntent);
        }
        if(itemId == R.id.image_dts1) {
            Intent mIntent = new Intent(this, ImageActivity.class);
            startActivity(mIntent);
        }
    }
}