package com.example.registro_cuentas.ui.addpay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.registro_cuentas.AppDBclt;
import com.example.registro_cuentas.AppDBreg;
import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.Cliente;
import com.example.registro_cuentas.Cuenta;
import com.example.registro_cuentas.CurrencyInput;
import com.example.registro_cuentas.MainActivity;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.Registro;
import com.example.registro_cuentas.SatrtVar;
import com.example.registro_cuentas.SelecAdapter;
import com.example.registro_cuentas.databinding.ActivityMainBinding;
import com.example.registro_cuentas.databinding.FragmentAddpayBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPayFragment extends Fragment implements View.OnClickListener{

    private FragmentAddpayBinding binding;

    private Context mContext = BaseContext.getContext();

    // DB
    private List<AppDBreg> appDBregistro = SatrtVar.appDBregistro;
    private AppDBclt appDBcliente = SatrtVar.appDBcliente;
    private List<Cliente> listCliente = new ArrayList<>();

    private ConstraintLayout mConstrain;
    private BottomNavigationView mNavBar = SatrtVar.mNavBar;

    //Todos los Inputs
    private EditText mInput1;
    private EditText mInput2;
    private EditText mInput3;
    private EditText mInput4;
    private List<EditText> mInputList = new ArrayList<>();

    //Botones
    private Button mButt1;
    private Button mButt2;

    //Todos los Spinner
    private Spinner mSpin1;
    private Spinner mSpin2;
    private int currSel1 = 0;
    private int currSel2 = 0;
    private List<String> mSpinL2= Arrays.asList("Ingreso (+)", "Egreso (-)");
    private List<String> mCurrencyList= Arrays.asList("$", "Bs");
    //---------------------------------------------------------------------

    private Switch mSw;
    private boolean swPorc = false;

    private List<String> mList = new ArrayList<>();
    private String mIndex = "";

    // Para guardar los permisos de app comprobados en main
    private boolean mPermiss = false;
    // Index de cuenta actual
    private int currtAcc = SatrtVar.mCurrenrAcc;

    private Basic mBasic = new Basic(BaseContext.getContext());

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        AddPayViewModel addPayViewModel = new ViewModelProvider(this).get(AddPayViewModel.class);

        binding = FragmentAddpayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mConstrain = binding.constrainPay;

        mSpin1 = binding.spinPay1;
        mSpin2 = binding.spinPay2;

        mSw = binding.swPay1;

        mInput1 = binding.inputPay1;
        mInput2 = binding.inputPay2;
        mInput3 = binding.inputPay3;
        mInput4 = binding.inputPay4;

        mButt1 = binding.buttPay1;

        mConstrain.setOnClickListener(this);
        mButt1.setOnClickListener(this);
        mSw.setOnClickListener(this);

        mInputList.add(mInput1);
        mInputList.add(mInput2);
        mInputList.add(mInput3);
        mInputList.add(mInput4);

        //Efecto moneda
        //-------------------------------------------------------------------------------------------------------
        List<View> mViewL1 = new ArrayList<>();
        mViewL1.add(mNavBar);
        int mOpt = 0;
        CurrencyInput mCInput = new CurrencyInput( mContext, mInput4,  mViewL1, mCurrencyList.get(SatrtVar.mCurrency), mOpt);
        mCInput.set();
        //----------------------------------------------------------------------------------------------------

        // Para eventos al mostrar o ocultar el teclado-----
        mBasic.steAllKeyEvent(mConstrain, mInputList);
        mBasic.setAllfocusEvent(mNavBar, mInputList);
        //-----------------------------------------------

        mPermiss = SatrtVar.mPermiss;
        if(!appDBregistro.isEmpty()) {
            mIndex = "" + appDBregistro.get(currtAcc).daoUser().getUsers().size();
            if (mIndex.isEmpty()) {
                mIndex = "0";
            }
        }
        listCliente = appDBcliente.daoUser().getUsers();

        //Para la lista del selector Cliente ----------------------------------------------------------------------------------------------
        List<String> mCltList = new ArrayList<>();
        mCltList.add("Agregar");
        for(int i = 0; i < listCliente.size(); i++){
            mCltList.add(listCliente.get(i).nombre);
        }
        SelecAdapter adapt1 = new SelecAdapter(mContext, mCltList);
        mSpin1.setAdapter(adapt1);
        if(!mCltList.isEmpty()) {
            mSpin1.setSelection(currSel1); //Set default client
        }
        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel1 = i;
                if (i > 0) {
                    mInput1.setText(listCliente.get(i-1).nombre.toUpperCase());
                    mInput1.setEnabled(false);
                }
                else{
                    mInput1.setText("");
                    mInput1.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //--------------------------------------------------------------------------------------------
        //Para la lista del selector Tipo Operacion ----------------------------------------------------------------------------------------------
        SelecAdapter adapt2 = new SelecAdapter(mContext, mSpinL2);
        mSpin2.setAdapter(adapt2);
        mSpin2.setSelection(0); //Set default ingreso

        mSpin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel2 = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //--------------------------------------------------------------------------------------------

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.butt_pay1) {
            boolean result = true;
            int msgIdx = 0;
            String cltId = "0";
            boolean newClt = true;
            mList.add("payID"+mIndex);
            for(int i = 0; i < mInputList.size(); i++) {
                String text = mInputList.get(i).getText().toString().toLowerCase();
                text = text.replaceAll("\"", "");
                text = text.replaceAll(",", "");
                //Para el input Nombre
                if(i == 0) {
                    for (int j = 0; j < listCliente.size(); j++) {
                        String name = listCliente.get(j).nombre;
                        if(name.toLowerCase().equals(text)){
                            msgIdx = 1;
                            cltId = listCliente.get(j).cliente;
                            newClt = false;
                            break;
                        }
                    }
                }
                if (text.isEmpty()){
                    if(i == 0) {
                        //MSG para entrada de Nombre
                        msgIdx = 3;
                        result = false;
                        break;

                    }
                    if(i == 3) {
                        //MSG para entrada de Monto
                        msgIdx = 3;
                        result = false;
                        break;

                    }
                    else if (i == 4) {
                        //MSG para entrada de...
                        msgIdx = 2;
                    }
                }
                mList.add(text);
            }
            if (result) {
                //Para Limpiar Todos Los inputs
                for (int i = 0; i < mInputList.size(); i++) {
                    mInputList.get(i).setText("");
                }
                SatrtVar mVars = new SatrtVar(mContext);
                //Comprueba que la lista de cuentas no este vacia
                if (mVars.listacc.size() > 1) {
                    //Inicia la fecha actual
                    LocalDate currdate = null;
                    LocalTime currtime = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        currtime = LocalTime.now();
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        currdate = LocalDate.now();
                    }
                    assert currdate != null;
                    Registro obj = new Registro(
                        mList.get(0), mList.get(1), mList.get(3), mList.get(4), currSel2, (swPorc?1:0),
                        "5", currdate.toString(), currtime.toString(), (newClt?""+listCliente.size():cltId), "", "", ""
                    );
                    appDBregistro.get(currtAcc).daoUser().insetUser(obj);

                   if(newClt){
                       cltId = ""+listCliente.size();
                       Toast.makeText(mContext, "Siz is "+ cltId, Toast.LENGTH_LONG).show();
                       Cliente objClt = new Cliente(cltId, mList.get(1), mList.get(2),"", (swPorc?1:0), currdate.toString());
                       appDBcliente.daoUser().insetUser(objClt);
                   }

                    //SE Limpia la lista
                    mList.clear();

                    //Recarga La lista de la DB ----------------------------
                    mVars.getRegListDB();
                    mVars.getCltListDB();
                    //-------------------------------------------------------

                    //Esto inicia las actividad Main despues de tiempo de espera del preloder
                    startActivity(new Intent(mContext, MainActivity.class));
                    //finish(); //Finaliza la actividad y ya no se accede mas
               }
            }
        }
        if (itemId == R.id.sw_pay1){
            swPorc = !swPorc;
        }
    }
}