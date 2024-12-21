package com.example.registro_cuentas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class CltEditActivity extends AppCompatActivity {

    // DB
    private AppDBacc appDBcuenta = StartVar.appDBcuenta;
    private AppDBclt appDBcliente = StartVar.appDBcliente;
    private Cuenta mAcc;

    //Todos los Inputs
    private EditText mInput1;
    private EditText mInput2;
    private EditText mInput3;
    private List<EditText> mInputList = new ArrayList<>();

    //Todos los Spinner
    private Spinner mSpin1;
    private Spinner mSpin2;
    private Spinner mSpin3;


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

        mInput1 = findViewById(R.id.input_cltedit1);
        mInput2 = findViewById(R.id.input_cltedit2);
        mInput3 = findViewById(R.id.input_cltedit3);

        mSpin1 = findViewById(R.id.spin_cltedit1);

        //mSpin1 = findViewById(R.id.spin_accedit1);

        //mButt1 = findViewById(R.id.butt_accedit1);


        DaoClt daoClt = appDBcliente.daoUser();
        List<Cliente> mCltList = daoClt.getUsers();
        Cliente mClt = mCltList.get(cltIndex);

        mInput1.setText(mClt.nombre);
        //mInput2.setText(mClt.alias);

        List<Integer> bitListA = Basic.getBits(32);
        List<Integer> bitListB = Basic.getBits("1;");


        if(bitListA.size() <= bitListB.size()) {
            int idx = Math.max(bitListA.size()-1 , 0);
            int valA = bitListA.get(idx);
            int valB = bitListB.get(idx);

            mInput2.setText(String.format("%x", valA) +" :: "+ String.format("%x", valB));

        }
        else {

            mInput2.setText("Aqui no hay :(");
        }

        //------------------------------------------------------------------------------------------
        //Para la lista de Cuentas Activas para el cliente------------------------------------------
        List<Cuenta> mAcc = appDBcuenta.daoUser().getUsers();
        List<Object[]> maccList = new ArrayList<>();

        //String[] testBit = daoClt.getSaveBits(mClt.cliente).split(";");//"0x7;".split(";");

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

        BoxAdapter adapt2 = new BoxAdapter(this, maccList);
        mSpin1.setAdapter(adapt2);
        //mSpin1.setSelection(); //Set default ingreso

        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //currSel1 = i;
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
}