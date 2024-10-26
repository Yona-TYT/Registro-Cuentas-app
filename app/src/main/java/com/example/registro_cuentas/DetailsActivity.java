package com.example.registro_cuentas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class DetailsActivity extends AppCompatActivity {

    // DB ----------------------------------------------------------------
    private AppDBacc appDBcuenta = SatrtVar.appDBcuenta;
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

        mTextList.add(mText1);
        mTextList.add(mText2);
        mTextList.add(mText3);
        mTextList.add(mText4);
        mTextList.add(mText5);

        // Se llenan los textView
        setInputList();

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
            String txName = reg.nombre;
            String txConc = reg.concep;
            String txMont = reg.monto;
            String txFech = reg.fecha;
            String txHora = cale.getTime(reg.time);

            int i = 0;
            mTextList.get(i).setText("Cliente: " + txName);
            i++;
            mTextList.get(i).setText("Concepto: " + txConc);
            i++;
            mTextList.get(i).setText("Monto: "+ txMont+ " "+mCurrencyList.get(mCindex));
            i++;
            mTextList.get(i).setText("Fecha: "+ txFech);
            i++;
            mTextList.get(i).setText("Hora: "+ txHora);

        }
    }
}