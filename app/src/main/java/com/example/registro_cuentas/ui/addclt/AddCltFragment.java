package com.example.registro_cuentas.ui.addclt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.BitsOper;
import com.example.registro_cuentas.CalcCalendar;
import com.example.registro_cuentas.DBListCreator;
import com.example.registro_cuentas.db.Cliente;
import com.example.registro_cuentas.db.Cuenta;
import com.example.registro_cuentas.CurrencyEditText;
import com.example.registro_cuentas.db.dao.DaoAcc;
import com.example.registro_cuentas.db.dao.DaoClt;
import com.example.registro_cuentas.db.dao.DaoDeb;
import com.example.registro_cuentas.db.Deuda;
import com.example.registro_cuentas.activitys.MainActivity;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.adapters.SelecAdapter;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.databinding.FragmentAddcltBinding;

import com.example.registro_cuentas.db.Pagos;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AddCltFragment extends Fragment  implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnFocusChangeListener{

    private FragmentAddcltBinding binding;

    private Context mContext = BaseContext.getContext();

    // DB
    private List<Pagos> appDBregistro = StartVar.listreg;
    private DaoClt daoCliente = StartVar.appDBall.daoClt();

    private DaoDeb daoDeuda = StartVar.appDBall.daoDeb();
    private List<Deuda> listDeuda = new ArrayList<>();

    private List<Cliente> listCliente = new ArrayList<>();
    private Cliente mClt = null;
    private Deuda mDeb = null;
    private Cuenta mAcc = null;


    private DaoAcc daoCuenta = StartVar.appDBall.daoAcc();

    private ConstraintLayout mConstrain;
    private BottomNavigationView mNavBar = StartVar.mNavBar;

    private TextView mText1;
    private TextView mText2;
    private TextView mText3;

    private List<String> mListFech = Arrays.asList( "","Dias", "Meses", "Años");

    //Todos los Inputs
    private EditText mInput1;
    private EditText mInput2;
    private CurrencyEditText mInput3;
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

    private Switch mSwActive;
    private boolean swEstat = true;

    private CheckBox mCheck;
    private boolean checkEstat = true;
    private String hexString = "0x00000000";

    private String mIndex = "";

    // Para guardar los permisos de app comprobados en main
    private boolean mPermiss = false;
    // Index de cuenta actual
    private int currtAcc = StartVar.mCurrAcc;
    private int currtTyp = StartVar.mCurrTyp;

    private String mCurr = mCurrencyList.get(StartVar.mCurrency);

    private Basic mBasic = new Basic(BaseContext.getContext());

    @SuppressLint("SetTextI18n")
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

        binding = FragmentAddcltBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mConstrain = binding.constrainClt;

        mSpin1 = binding.spinClt1;
        mSpin2 = binding.spinClt2;


        mText1 = binding.txviewClt1;
        mText2 = binding.txviewClt2;
        mText3 = binding.txviewClt3;

        mInput1 = binding.inputClt1;
        mInput2 = binding.inputClt2;
        mInput3 = binding.inputClt4;

        mSwActive = binding.swClt1;
        mCheck = binding.checkClt1;

        mButt1 = binding.buttClt1;

        mInput1.setOnFocusChangeListener(this);
        mInput2.setOnFocusChangeListener(this);
        mConstrain.setOnClickListener(this);
        mButt1.setOnClickListener(this);
        mSwActive.setOnClickListener(this);
        mCheck.setOnClickListener(this);

        // Set imagen picker-----------------
        //mFileM.setImgPicker(imageView1, mBtnImg1);
        //------------------------------------

        mInputList.add(mInput1);
        mInputList.add(mInput2);
        mInputList.add(mInput3);

        listDeuda = daoDeuda.getUsers();

        List<Cuenta> mAccList = StartVar.appDBall.daoAcc().getUsers();
        if (!mAccList.isEmpty()) {
            mAcc = mAccList.get(currtAcc);
        }

        //Efecto moneda
        //-------------------------------------------------------------------------------------------------------
        mInput3.setCurrencySymbol(mCurr, true);
        mInput3.setText("0");
        List<View> mViewL1 = new ArrayList<>();
        mViewL1.add(mNavBar);
        // Para eventos al mostrar o ocultar el teclado
        Basic mKeyBoardEvent = new Basic(mContext);
        mKeyBoardEvent.keyboardEvent(mConstrain, mInput1, mViewL1, 0); //opt = 0 is clear elm focus
        //-------------------------------------------------------------------------------------

        // Para eventos al mostrar o ocultar el teclado-----
        mBasic.steAllKeyEvent(mConstrain, mInputList);
        //-----------------------------------------------

        //Inicia el sw dependiendo de swEstat
        mSwActive.setChecked(swEstat);

        if (mAcc != null) {
            mText1.setText("Cuenta: " + mAcc.nombre + " (" + mAcc.desc + ")");
        }

        //Para la lista del selector Cliente ----------------------------------------------------------------------------------------------
        setAdapterClt();

        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel1 = i;

                if (i > 0) {
                    mClt = listCliente.get(i-1);

                    setChekboxStatus(mClt);

                    String accId = daoCuenta.getUsers().get(StartVar.mCurrAcc).cuenta;
                    for(Deuda mD : daoDeuda.getListByGroupId(mClt.cliente)){
                        if(mD.accid.equals(accId)){
                            mDeb = mD;
                            break;
                        }
                    }

                    //mDeb = listDeuda.size() > currtAcc? StartVar.appDBall.daoDeb().getUsers(mIdList.get(i)) : null;
                    Float total = 0f;
                    //Log.d("PhotoPicker", "-->>>>>>>>>>>>>>>>>>>>>>>>>>>> year: "+mDeb );
                    if (mDeb != null) {

                        //Basic.msg(mClt.nombre);
                        total = mDeb.rent;
                        if (currtTyp > 0) {
                            int mult = CalcCalendar.getRangeMultiple(mDeb.ulfech, currtTyp);
                            float monto = Basic.getDebt(mult, mDeb.rent, mDeb.paid);

                            int isDeb = mDeb.pagado;
                            String tx = "";
                            if (isDeb == 0) {
                                tx = " [Sin Registros] ";
                            } else if (isDeb == 1 || mult > 0) {
                                tx = " [" + (mDeb.oper == 0 ? "+" : "-") + monto + " " + mCurr + "] ";
                            } else {
                                tx = " [Pagado] ";
                            }

                            //tx = mDeb.pagado == 1 ? monto + " " + mCurr + " (" + mult + " " + mListFech.get(currtTyp) + ")" : "Pagado";
                            mText2.setText("Deuda: " + tx);
                            mText3.setText(isDeb == 0 ? "No hay Pagos Registrados" : mDeb.ulfech + " (Ultima Fecha Pagada)");
                        } else {
                            mText2.setText("Deuda: Cuenta Sin Cierre");
                        }
                        swEstat = mDeb.estat == 1;

                        if (swEstat) {
                            mInput3.setEnabled(true);
                            mSwActive.setChecked(true);
                        }
                        else {
                            mInput3.setEnabled(false);
                            mSwActive.setChecked(false);
                            mText2.setText("Deuda: NA");
                        }
                    }
                    else {
                        mInput3.setEnabled(false);
                        mSwActive.setChecked(false);
                        mText2.setText("Deuda: NA");
                        mText3.setText("Uiltimo Pago: NA");
                        mInput3.setText("0,00");
                    }
                    mInput1.setText(mClt.nombre.toUpperCase());
                    mInput2.setText(mClt.alias.toUpperCase());
                    mInput3.setText(Basic.getValueFormatter(total.toString()));
                }
                else{
                    mInput3.setEnabled(true);
                    swEstat = true;
                    mSwActive.setChecked(true);
                    mCheck.setChecked(true);
                    mCheck.setText("Visible para: "+mAcc.nombre);

                    mInput1.setText("");
                    mInput2.setText("");
                    mInput3.setText("0,00");

                    mText2.setText("Deuda: NA");
                    mText3.setText("Uiltimo Pago: NA");
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
                mInput3.setEnabled(true);
                mSpin2.setEnabled(true);
            }
            else{
                mInput3.setEnabled(false);
                mSpin2.setEnabled(false);
            }
        }
        if (itemId == R.id.check_clt1){
            checkEstat = !checkEstat;
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

            Float rent = (float) mInput3.getNumericValue();

            if (rent < 0.0) {
                //MSG Para entrada de monto
                Basic.msg("Ingrese un MONTO Valido!.");
                return;
            }

            //Inicia la fecha actual
            String currdate = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                currdate = LocalDate.now().toString();
            }

            if (currSel1 == 0){
                String cltId = "cltID"+listCliente.size();
                String debId = "debID"+listDeuda.size();
                String accId = daoCuenta.getUsers().get(StartVar.mCurrAcc).cuenta;

                Cliente objClt = null;

                //Datos basicos del cliente
                objClt = new Cliente(
                        cltId, nombre, alias, "@null", 0,
                        currdate,0f, currdate, currSel2, BitsOper.setBitInHexString(hexString, currtAcc, checkEstat)
                );
                daoCliente.insetUser(objClt);

                //Datos de deudas y monto fijo
                Basic.msg(""+swEstat);
                Deuda objDeb = new Deuda(
                        debId, accId, cltId, rent, 0, currdate,
                        (swEstat?1:0), 0, CalcCalendar.getCorrectDate(currdate, currtTyp), 0,0f
                );
                daoDeuda.insetUser(objDeb);
            }
            else {

                String cltId = mClt.cliente;

                if(mDeb == null){
                    String debId = "debID"+daoDeuda.getUsers().size();
                    String accId = daoCuenta.getUsers().get(StartVar.mCurrAcc).cuenta;

                    //Datos de deudas y monto fijo
                    Deuda objDeb = new Deuda(
                            debId, accId, cltId, rent, 0, currdate,
                            (swEstat?1:0), 0, CalcCalendar.getCorrectDate(currdate, currtTyp), 0,0f
                    );
                    daoDeuda.insetUser(objDeb);
                }
                else {
                    Log.d("Monto", "-->>>>>>>>>>>>>>>>>>>>>>>>>>>> Aqui hayyyyyy: " + mClt.cliente);

                    daoCliente.updateUser(
                            cltId, nombre, alias, "@null", 0,
                            mClt.fecha, 0f,  mClt.ulfech
                    );

                    daoCliente.updateBits(cltId, BitsOper.setBitInHexString(hexString, currtAcc, checkEstat));

                    daoDeuda.updateUser(
                            mDeb.deuda, mDeb.accid, mClt.cliente, rent, 0, mDeb.fecha, (swEstat ? 1 : 0),
                            mDeb.pagado, mDeb.ulfech, currSel2, 0f
                    );
                }
            }

            //Actualisza la lista de fechas
            CalcCalendar.addCurrentMonthIfAbsent(mContext);

            StartVar mVars = new StartVar(mContext);
            //Recarga La lista de la DB ----------------------------
            mVars.getCltListDB();
            //-------------------------------------------------------

            Basic.msg("Se han GUARDADO los cambios");

            setAdapterClt();
            //mSpin1.setSelection(0); //Set default client

            DBListCreator.createDbLists(); //Actualiza la lista para exportar csv
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private void setAdapterClt(){
        listCliente = daoCliente.getUsers();
        List<String> mCltList = new ArrayList<>();
        mCltList.add("Agregar");

        for(Cliente mC : listCliente){
            mCltList.add(mC.nombre);
        }
        SelecAdapter adapt1 = new SelecAdapter(mContext, mCltList);
        mSpin1.setAdapter(adapt1);
        mSpin1.setSelection(0); //Set default client
        mInput1.setText("");
        mInput2.setText("");
        mInput3.setText(Basic.setFormatter("0"));
    }

    private void setChekboxStatus(Cliente mC){
        int x = currtAcc;
        int siz = 0;

        hexString = mC.bits;
        List<Integer> bitList = BitsOper.getBits(hexString);
        int mByte = bitList.get(0);
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

        //Basic.msg(String.format("%s - %x - %s - %d",listCliente.get(i).nombre, bitList.size(), Basic.bitR(mByte, x) == 1, currtAcc));
        boolean mRes = BitsOper.bitR(mByte, x) == 1;
        mCheck.setChecked(mRes);
        checkEstat = mRes;
        mCheck.setText("Visible para: "+mAcc.nombre);
    }
}
