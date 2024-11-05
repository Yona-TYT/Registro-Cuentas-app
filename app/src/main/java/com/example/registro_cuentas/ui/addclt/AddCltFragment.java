package com.example.registro_cuentas.ui.addclt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.registro_cuentas.AppDBclt;
import com.example.registro_cuentas.AppDBreg;
import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.CalcCalendar;
import com.example.registro_cuentas.Cliente;
import com.example.registro_cuentas.CurrencyInput;
import com.example.registro_cuentas.DaoClt;
import com.example.registro_cuentas.DaoReg;
import com.example.registro_cuentas.MainActivity;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.SelecAdapter;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.databinding.FragmentAddcltBinding;

import com.example.registro_cuentas.ui.addpay.AddPayViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AddCltFragment extends Fragment  implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnFocusChangeListener{

    private FragmentAddcltBinding binding;

    private Context mContext = BaseContext.getContext();

    // DB
    private List<AppDBreg> appDBregistro = StartVar.appDBregistro;
    private AppDBclt appDBcliente = StartVar.appDBcliente;
    private List<Cliente> listCliente = new ArrayList<>();
    private Cliente mclt = null;

    private ConstraintLayout mConstrain;
    private BottomNavigationView mNavBar = StartVar.mNavBar;

    private TextView mText1;
    private TextView mText2;
    private List<String> mListFech = Arrays.asList("Dias", "Meses", "Años");
    private int accType = 0;




    //Todos los Inputs
    private EditText mInput1;
    private EditText mInput2;
    private EditText mInput3;
    private EditText mInput4;
    private List<EditText> mInputList = new ArrayList<>();

    //Botones
    private Button mButt1;

    //Todos los Spinner
    private Spinner mSpin1;
    private Spinner mSpin2;
    private int currSel1 = 0;
    private int currSel2 = 0;
    private List<String> mSpinL2= Arrays.asList("Ingreso (+)", "Egreso (-)");
    private List<String> mCurrencyList= Arrays.asList("$", "Bs");
    //---------------------------------------------------------------------

    private Switch mSw;
    private boolean swEstat = false;

    private String mIndex = "";

    // Para guardar los permisos de app comprobados en main
    private boolean mPermiss = false;
    // Index de cuenta actual
    private int currtAcc = StartVar.mCurrentAcc;

    private Basic mBasic = new Basic(BaseContext.getContext());

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Se configura el Boton nav Back -----------------------------------------------
        OnBackPressedDispatcher onBackPressedDispatcher = this.getActivity().getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent mIntent = new Intent(mContext, MainActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
            }
        };
        onBackPressedDispatcher.addCallback(this.getActivity(), callback);
        //---------------------------------------------------------------------------------

        AddPayViewModel addPayViewModel = new ViewModelProvider(this).get(AddPayViewModel.class);

        binding = FragmentAddcltBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mConstrain = binding.constrainClt;

        mSpin1 = binding.spinClt1;
        mSpin2 = binding.spinClt2;

        mSw = binding.swClt1;

        mText1 = binding.txviewClt1;
        mText2 = binding.txviewClt2;

        mInput1 = binding.inputClt1;
        mInput2 = binding.inputClt2;
        mInput3 = binding.inputClt3;
        mInput4 = binding.inputClt4;

        mButt1 = binding.buttClt1;

        mInput1.setOnFocusChangeListener(this);
        mInput2.setOnFocusChangeListener(this);
        mInput3.setOnFocusChangeListener(this);
        mConstrain.setOnClickListener(this);
        mButt1.setOnClickListener(this);
        mSw.setOnClickListener(this);

        // Set imagen picker-----------------
        //mFileM.setImgPicker(imageView1, mBtnImg1);
        //------------------------------------

        mInputList.add(mInput1);
        mInputList.add(mInput2);
        mInputList.add(mInput3);
        mInputList.add(mInput4);

        //Efecto moneda
        //-------------------------------------------------------------------------------------------------------
        String curr = mCurrencyList.get(StartVar.mCurrency);
        mInput4.setEnabled(false);
        List<View> mViewL1 = new ArrayList<>();
        mViewL1.add(mNavBar);
        int mOpt = 0;
        CurrencyInput mCInput = new CurrencyInput( mContext, mInput4,  mViewL1, curr, mOpt);
        mCInput.set();
        //----------------------------------------------------------------------------------------------------

        // Para eventos al mostrar o ocultar el teclado-----
        mBasic.steAllKeyEvent(mConstrain, mInputList);
        //-----------------------------------------------

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
            if (currSel1 > 0) {
                mclt = listCliente.get(currSel1 - 1);
                swEstat = mclt.estat==1;
                mSw.setChecked(swEstat);
            }
            else {
                mInput4.setText(Basic.setMask("0", curr));
            }
        }
        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel1 = i;
                if (i > 0) {
                    mclt = listCliente.get(i-1);
                    String curr = mCurrencyList.get(StartVar.mCurrency);

                    int mult = CalcCalendar.getRangeMultiple(mclt.ulfech,0);
                    float monto = Basic.getDebt(mult, mclt.total, mclt.debe);
                    String tx = mclt.pagado == 0 ? monto +" "+curr+" ("+mult+" "+mListFech.get(accType)+")":"Pagado";
                    mText1.setText("Deuda: "+tx);
                    mText2.setText(mclt.ulfech +" (Ultima Fecha Pagada)" );
                    mInput1.setText(mclt.nombre.toUpperCase());
                    mInput2.setText(mclt.alias.toUpperCase());
                    mInput4.setText(Basic.setMask(mclt.total, curr));

                    swEstat = mclt.estat==1;
                    if(swEstat) {
                        mInput4.setEnabled(true);
                        mSw.setChecked(true);
                    }
                    else {
                        mInput4.setEnabled(false);
                        mSw.setChecked(false);
                        mText1.setText("Deuda: NA");
                    }
                }
                else{
                    mInput1.setText("");
                    mInput2.setText("");
                    mText1.setText("Deuda: NA");
                    mText2.setText("Uiltimo Pago: NA");
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
        if (itemId == R.id.sw_clt1){
            swEstat = !swEstat;
            if(swEstat){
                mInput4.setEnabled(true);
                mSpin2.setEnabled(true);
            }
            else{
                mInput4.setText(Basic.setMask("0", mCurrencyList.get(StartVar.mCurrency)));
                mSpin2.setSelection(0);
                mInput4.setEnabled(false);
                mSpin2.setEnabled(false);
            }
        }
        if (itemId == R.id.butt_clt1) {
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

            String monto = Basic.setValue(mInput4.getText().toString());
            if(swEstat) {
                if (monto.isEmpty() || Float.parseFloat(monto) <= 0.0) {
                    //MSG Para entrada de monto
                    Basic.msg("Ingrese un MONTO Valido!.");
                    return;
                }
            }

            if (currSel1 == 0){
                String cltId = ""+listCliente.size();
                Cliente objClt = null;
                //Inicia la fecha actual
                String currdate = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    currdate = LocalDate.now().toString();
                }
                objClt = new Cliente(
                        "cltID"+cltId, nombre, alias, monto, 0,
                        currdate, (swEstat?1:0),0, currdate, currSel2, "0"
                );
                appDBcliente.daoUser().insetUser(objClt);
            }
            else {
                DaoClt mDao = StartVar.appDBcliente.daoUser();
                mDao.updateUser(
                        mclt.cliente, nombre, alias, monto, 0,
                        mclt.fecha, (swEstat ? 1 : 0), mclt.pagado, mclt.ulfech, currSel2, "0"
                );
            }

            //Actualisza la lista de fechas
            CalcCalendar.startCalList(mContext);

            StartVar mVars = new StartVar(mContext);
            //Recarga La lista de la DB ----------------------------
            mVars.getCltListDB();
            //-------------------------------------------------------

            //Esto inicia las actividad Main
            startActivity(new Intent(mContext, MainActivity.class));
            //finish(); //Finaliza la actividad y ya no se accede mas
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}