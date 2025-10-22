package com.example.registro_cuentas.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.BitsOper;
import com.example.registro_cuentas.adapters.BoxAdapter;
import com.example.registro_cuentas.db.AllDao;
import com.example.registro_cuentas.db.Cliente;
import com.example.registro_cuentas.db.Cuenta;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.db.dao.DaoClt;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class CltEditActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext = BaseContext.getContext();

    // DB
    private AllDao appDBcuenta = StartVar.appDBall;
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
    public int accIndex = StartVar.accSelect;

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
                StartVar.bitList.clear();
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

        DaoClt daoClt = StartVar.appDBall.daoClt();
        List<Cliente> mCltList = daoClt.getUsers();
        mClt = mCltList.get(cltIndex);

        mInput1.setText(mClt.nombre);
        mInput2.setText(mClt.alias);

        //------------------------------------------------------------------------------------------
        //Para la lista de Cuentas Activas para el cliente------------------------------------------
        StartVar startVar = new StartVar(mContext);
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
            StartVar.bitList.clear();
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setCheckBoxes() {
        List<Cuenta> mAcc = StartVar.appDBall.daoAcc().getUsers();
        List<Object[]> maccList = new ArrayList<>();

        List<Integer> bitList = BitsOper.getBits(mClt.bits);
        if (bitList.isEmpty()) {
            bitList.add(0);  // Caso base: al menos un grupo de 0
        }

        for (int i = 0; i < mAcc.size(); i++) {
            // Cálculo correcto: grupo y offset para bit i
            int group = i / 32;
            int offset = i % 32;

            // Padda con 0 si grupo no existe
            while (group >= bitList.size()) {
                bitList.add(0);
            }

            int mByte = bitList.get(group);

            // Extracción del bit (usa tu bitR)
            boolean isChecked = BitsOper.bitR(mByte, offset) == 1;

            Object[] stList = new Object[3];
            stList[0] = i;
            stList[1] = isChecked;
            // Removido: List<Integer> list = BitsOper.getBits(i);  // No usado, eliminar
            stList[2] = mAcc.get(i).nombre + " (" + mAcc.get(i).desc + ")";
            maccList.add(stList);

            // Debug opcional
            // Log.d("setCheckBoxes", String.format("Cuenta %d: grupo=%d, offset=%d, bit=%b", i, group, offset, isChecked));
        }

        BoxAdapter adapt2 = new BoxAdapter(mContext, maccList, mSpin1);
        mSpin1.setAdapter(adapt2);
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

            if(monto.isEmpty() || Basic.parseFloat(monto) < 0.0){
                //MSG Para entrada de monto
                Basic.msg("Ingrese un MONTO Valido!.");
                return;
            }

            Integer mInt = 0;
            List<Integer> intList = new ArrayList<>();
            for (Object[] objL : StartVar.bitList){
                if((boolean)objL[1]) {
                    intList.add((int) objL[0]);
                    //Basic.msg("Si hay --> "+(int)objL[0]+ " "+(boolean)objL[1]);
                }
            }

            //Basic.msg(BitsOper.mergeBitString(intList)+ " siz: "+intList.size());
            DaoClt mDao = StartVar.appDBall.daoClt();
            mDao.updateBits(mClt.cliente, BitsOper.mergeBitString(intList));

            StartVar.bitList.clear();

            //Esto inicia las actividad Reload
            startActivity(new Intent(mContext, ReloadActivity.class));
            this.finish(); //Finaliza la actividad y ya no se accede mas
        }
    }
    //------------------------------------------------------------
}