package com.example.registro_cuentas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class CltEditActivity extends AppCompatActivity implements View.OnClickListener{

    // DB
    private AppDBacc appDBcuenta = StartVar.appDBcuenta;
    private AppDBclt appDBcliente = StartVar.appDBcliente;
    private Cuenta mAcc;
    private Cliente mClt;

    private ConstraintLayout mLayout;

    //Todos los Inputs
    private EditText mInput1;
    private EditText mInput2;
    private EditText mInput3;
    private List<EditText> mInputList = new ArrayList<>();

    //Todos los Spinner
    private Spinner mSpin1;
    private Spinner mSpin2;
    private Spinner mSpin3;

    private Button mBtton1;

    public int cltIndex = StartVar.cltIndex;
    public int accIndex = StartVar.mCurrAcc;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clt_edit);

        //Se configura el Boton nav Back -----------------------------------------------
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                CltEditActivity.this.finish();
            }
        };
        onBackPressedDispatcher.addCallback(this, callback);
        //---------------------------------------------------------------------------------
        //Activate ToolBar ----------------------------------------------------------------
        Toolbar myToolbar = findViewById(R.id.toolbar_cltedit1);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Editar Cliente");
        actionBar.setDisplayShowHomeEnabled(true);

        myToolbar.setTitleTextColor(ContextCompat.getColor(myToolbar.getContext(), R.color.inner_button));
        //------------------------------------------------------------------------------------------

        mLayout = findViewById(R.id.main);

        mInput1 = findViewById(R.id.input_cltedit1);
        mInput2 = findViewById(R.id.input_cltedit2);
        mInput3 = findViewById(R.id.input_cltedit3);

        mSpin1 = findViewById(R.id.spin_cltedit1);

        mBtton1 = findViewById(R.id.butt_cltedit1);

        mBtton1.setOnClickListener(this);

        DaoClt daoClt = appDBcliente.daoUser();
        List<Cliente> mCltList = daoClt.getUsers();
        mClt = mCltList.get(cltIndex);

        mInput1.setText(mClt.nombre);
        mInput2.setText(mClt.alias);

        //------------------------------------------------------------------------------------------
        //Para la lista de Cuentas Activas para el cliente------------------------------------------
        StartVar startVar = new StartVar(this);
        startVar.setCltBit(mClt.bits);

        setCheckBoxes();

        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //mSpin1.requestFocus();
                //mSpin1.clearFocus();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //--------------------------------------------------------------------------------------------

        //mButt1.setOnClickListener(this);

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

    void setCheckBoxes(){
        List<Cuenta> mAcc = appDBcuenta.daoUser().getUsers();
        List<Object[]> maccList = new ArrayList<>();
        List<Integer> bitList = Basic.getBits(mClt.bits);//0x0;//Basic.toHex(testBit[0]);
        //Basic.msg(String.format("- %s ", mClt.bits));

        //daoClt.updateBits(mClt.cliente, "0x0");

        int x = 0;
        int siz = 0;
        int mByte = bitList.get(0);

        for (int i = 1; i < mAcc.size(); i++){

            if(x == 32){
                x = 0;
                siz ++;
                if(siz < bitList.size()){
                    mByte = bitList.get(siz);
                }
                else{
                    mByte = 0x0;
                }
            }
            //Basic.msg(String.format("%x - %s ",bitList.size(), Basic.bitR(mByte, x) == 1 ));
            Object[] stList= new Object[3];
            stList[0] = i;
            stList[1] = Basic.bitR(mByte, x) == 1;
            List<Integer> list = Basic.getBits(i);

            stList[2] = mAcc.get(i).nombre+" ("+mAcc.get(i).desc+")";
            maccList.add(stList);

            x++;
        }

        BoxAdapter adapt2 = new BoxAdapter(this, maccList, mSpin1);
        mSpin1.setAdapter(adapt2);

        //mSpin1.setSelection(); //Set default ingreso
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.butt_cltedit1) {
            String nombre = mInput1.getText().toString().toLowerCase();
            nombre = Basic.inputProcessor(nombre); //Elimina caracteres que afectan a los csv
            nombre = Basic.nameProcessor(nombre); //Filtra los caracteres del nombre
            if(nombre.isEmpty()){
                //MSG Para entrada de nombre
                Basic.msg("Ingrese un NOMBRE Valido!.");
                return;
            }
            String alias = mInput2.getText().toString().toLowerCase();
            alias = Basic.inputProcessor(alias); //Elimina caracteres que afectan a los csv

            String monto = Basic.setValue(mInput3.getText().toString());
            if(monto.isEmpty() || Basic.parseFloat(monto) <= 0.0){
                //MSG Para entrada de monto
                Basic.msg("Ingrese un MONTO Valido!.");
                return;
            }

            DaoClt mDao = StartVar.appDBcliente.daoUser();
            //mDao.updateBits(mAcc.cuenta, nombre, desc, monto, currSel1);

            //Recarga La lista de la DB ----------------------------
            StartVar mVars = new StartVar(BaseContext.getContext());
            mVars.getRegListDB();
            //-------------------------------------------------------

            //Esto inicia las actividad Main despues de tiempo de espera del preloder
            startActivity(new Intent(this, MainActivity.class));
            this.finish(); //Finaliza la actividad y ya no se accede mas
        }

    }
    //------------------------------------------------------------
}