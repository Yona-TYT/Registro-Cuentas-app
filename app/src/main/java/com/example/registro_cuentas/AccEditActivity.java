package com.example.registro_cuentas;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class AccEditActivity extends AppCompatActivity implements View.OnClickListener {

    // DB
    private AppDBacc appDBcuenta = StartVar.appDBcuenta;
    private Cuenta mAcc;

    //Todos los Inputs
    private EditText mInput1;
    private EditText mInput2;
    private EditText mInput3;
    private List<EditText> mInputList = new ArrayList<>();

    private ConstraintLayout mConstrain;
    private BottomNavigationView mNavBar = StartVar.mNavBar;

    //Botones
    private Button mButt1;

    private String mIndex = "";

    private List<String> mCurrencyList= Arrays.asList("$", "Bs");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_acc);

        //Se configura el Boton nav Back -----------------------------------------------
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AccEditActivity.this.finish();
            }
        };
        onBackPressedDispatcher.addCallback(this, callback);
        //---------------------------------------------------------------------------------
        //Activate ToolBar ----------------------------------------------------------------
        Toolbar myToolbar = findViewById(R.id.toolbar_editacc);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Editar Cuenta");
        actionBar.setDisplayShowHomeEnabled(true);

        myToolbar.setTitleTextColor(ContextCompat.getColor(myToolbar.getContext(), R.color.inner_button));
        //------------------------------------------------------------------------------------------

        mInput1 = findViewById(R.id.input_accedit1);
        mInput2 = findViewById(R.id.input_accedit2);
        mInput3 = findViewById(R.id.input_accedit3);

        mButt1 = findViewById(R.id.butt_accedit1);

        mAcc = appDBcuenta.daoUser().getUsers().get(StartVar.mCurrentAcc+1);

        mInput1.setText(mAcc.nombre);
        mInput2.setText(mAcc.desc);

        mButt1.setOnClickListener(this);

        //Efecto moneda
        //-------------------------------------------------------------------------------------------------------
        String curr = mCurrencyList.get(StartVar.mCurrency);
        mInput3.setText(Basic.setMask(mAcc.monto, curr));
        List<View> mViewL1 = new ArrayList<>();
        int mOpt = 0;
        CurrencyInput mCInput = new CurrencyInput( this, mInput3,  mViewL1, curr, mOpt);
        mCInput.set();
        //----------------------------------------------------------------------------------------------------

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

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.butt_accedit1) {
            String nombre = mInput1.getText().toString();
            nombre = Basic.inputProcessor(nombre); //Elimina caracteres que afectan a los csv
            if(nombre.isEmpty()){
                Basic.msg("Ingrese un NOMBRE Valido!.");
                return;
            }

            String desc = mInput2.getText().toString();
            desc = Basic.inputProcessor(desc); //Elimina caracteres que afectan a los csv
            if(desc.isEmpty()){
                Basic.msg("Ingrese una DESCRIPCION Valida!.");
                return;
            }

            String monto = Basic.setValue(mInput3.getText().toString());
            if(monto.isEmpty() || Float.parseFloat(monto) <= 0.0){
                //MSG Para entrada de monto
                Basic.msg("Ingrese un MONTO Valido!.");
                return;
            }

            DaoAcc mDao = StartVar.appDBcuenta.daoUser();
            mDao.updateAccount(mAcc.cuenta, nombre, desc, monto);

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