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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.BitsOper;
import com.example.registro_cuentas.CalcCalendar;
import com.example.registro_cuentas.DBListCreator;
import com.example.registro_cuentas.db.Cliente;
import com.example.registro_cuentas.db.Conf;
import com.example.registro_cuentas.db.Cuenta;
import com.example.registro_cuentas.CurrencyEditText;
import com.example.registro_cuentas.db.DatabaseUtils;
import com.example.registro_cuentas.db.dao.DaoAcc;
import com.example.registro_cuentas.db.dao.DaoCfg;
import com.example.registro_cuentas.db.dao.DaoClt;
import com.example.registro_cuentas.db.dao.DaoDeb;
import com.example.registro_cuentas.db.Deuda;
import com.example.registro_cuentas.activitys.MainActivity;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.adapters.SelecAdapter;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.databinding.FragmentAddcltBinding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AddCltFragment extends Fragment  implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnFocusChangeListener{

    private FragmentAddcltBinding binding;

    private Context mContext = BaseContext.getContext();

    // DB
    private DaoClt daoCliente;
    private DaoDeb daoDeuda;
    private DaoAcc daoCuenta;
    private DaoCfg daoConf;

    private List<Cliente> listCliente = new ArrayList<>();
    private Cliente mClt = null;
    private Deuda mDeb = null;
    private Cuenta mAcc = null;

    private ConstraintLayout mConstrain;

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
    private ImageButton buttNext;
    private ImageButton buttPrev;

    //Todos los Spinner
    private Spinner mSpin1;
    private Spinner mSpin2;
    private Spinner mSpin3;

    private int currSel1 = 0;
    private int currSel2 = 0;
    private int currSel3 = 0;
    private List<String> mSpin1L = Arrays.asList("Todos", "Visibles", "Invisibles", "Activos", "Inactivos");
    private List<String> mSpin3L = Arrays.asList("Ingreso (+)", "Egreso (-)");

    private List<String> mCurrencyList= Arrays.asList("$", "Bs");
    //---------------------------------------------------------------------

    private Switch mSwActive;
    private boolean swEstat = true;

    private CheckBox mCheck;
    private boolean checkEstat = true;
    private String hexString = "0x00000000";

    // Para guardar los permisos de app comprobados en main
    private boolean mPermiss = false;
    // Index de cuenta actual
    private int currtAcc = StartVar.accSelect;
    private int currtTyp = StartVar.accCierre;

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
        mSpin3 = binding.spinClt3;


        mText1 = binding.txviewClt1;
        mText2 = binding.txviewClt2;
        mText3 = binding.txviewClt3;

        mInput1 = binding.inputClt1;
        mInput2 = binding.inputClt2;
        mInput3 = binding.inputClt4;

        mSwActive = binding.swClt1;
        mCheck = binding.checkClt1;

        mButt1 = binding.buttClt1;
        buttNext = binding.buttNext;
        buttPrev = binding.buttPrev;

        mInput1.setOnFocusChangeListener(this);
        mInput2.setOnFocusChangeListener(this);
        mConstrain.setOnClickListener(this);
        mButt1.setOnClickListener(this);
        buttNext.setOnClickListener(this);
        buttPrev.setOnClickListener(this);
        mSwActive.setOnClickListener(this);
        mCheck.setOnClickListener(this);

        // Set imagen picker-----------------
        //mFileM.setImgPicker(imageView1, mBtnImg1);
        //------------------------------------

        mInputList.add(mInput1);
        mInputList.add(mInput2);
        mInputList.add(mInput3);

        //Inicializa los dao
        daoCliente = StartVar.appDBall.daoClt();
        daoDeuda = StartVar.appDBall.daoDeb();
        daoCuenta = StartVar.appDBall.daoAcc();

        List<Cuenta> mAccList = StartVar.appDBall.daoAcc().getUsers();
        if (!mAccList.isEmpty()) {
            mAcc = mAccList.get(currtAcc);
        }

        //Efecto moneda
        //-------------------------------------------------------------------------------------------------------
        mInput3.setCurrencySymbol(mCurr, true);
        mInput3.setText("0");
        List<View> mViewL1 = new ArrayList<>();

        //Inicia el sw dependiendo de swEstat
        mSwActive.setChecked(swEstat);

        if (mAcc != null) {
            mText1.setText("Cuenta: " + mAcc.nombre + " (" + mAcc.desc + ")");
        }

        //Para la lista del selector Mostrar ----------------------------------------------------------------------------------------------
        SelecAdapter adapt1 = new SelecAdapter(mContext, mSpin1L);
        mSpin1.setAdapter(adapt1);
        daoConf = StartVar.appDBall.daoCfg();
        currSel1 = daoConf.getUsers(StartVar.mConfID).show;
        mSpin1.setSelection(currSel1); //Set default ingreso

        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel1 = i;
                daoConf.updateView(StartVar.mConfID, currSel1);
                setAdapterClt(0);    //Recarga el adapter de lista clientes
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //--------------------------------------------------------------------------------------------

        //Para la lista del selector Cliente ----------------------------------------------------------------------------------------------
        setAdapterClt(0);

        mSpin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel2 = i;
                mDeb = null;
                mClt = null;

                if (i > 0) {
                    mClt = listCliente.get(i-1);

                    setChekboxStatus(mClt);

                    String accId = mAcc.cuenta;
                    mDeb = daoDeuda.getUserByCltAndAcc(mClt.cliente, accId);

                    //mDeb = listDeuda.size() > currtAcc? StartVar.appDBall.daoDeb().getUsers(mIdList.get(i)) : null;
                    Double total = 0d;
                    //Log.d("PhotoPicker", "-->>>>>>>>>>>>>>>>>>>>>>>>>>>> year: "+mDeb );
                    if (mDeb != null) {

                        //Basic.msg(mClt.nombre);
                        total = mDeb.rent;
                        if (currtTyp > 0) {
                            int mult = CalcCalendar.getRangeMultiple(mDeb.ulfech, currtTyp);
                            Double monto = Basic.getDebt(mult, mDeb.rent, mDeb.remnant);

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
                        swEstat = false;
                        mInput3.setEnabled(swEstat);
                        mSwActive.setChecked(swEstat);

                        mText2.setText("Deuda: NA");
                        mText3.setText("Uiltimo Pago: NA");
                        mInput3.setText("0,00");
                    }
                    mInput1.setText(mClt.nombre.toUpperCase());
                    mInput2.setText(mClt.alias.toUpperCase());
                    mInput3.setText(Basic.getValueFormatter(String.valueOf(total)));
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
        SelecAdapter adapt2 = new SelecAdapter(mContext, mSpin3L);
        mSpin3.setAdapter(adapt2);
        mSpin3.setSelection(0); //Set default ingreso

        mSpin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel3 = i;
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
                mSpin3.setEnabled(true);
            }
            else{
                mInput3.setEnabled(false);
                mSpin3.setEnabled(false);
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

            Double rent = mInput3.getNumericValue();

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

            if(mClt == null){
                for(Cliente mC : daoCliente.getUsers()){
                    if(mC.nombre.equals(nombre.toLowerCase())){
                        mInput1.setText("");
                        Basic.msg("El Nombre Ya EXISTE!");
                        return;
                    }
                }
            }

            if (currSel2 == 0 && mClt == null){
                String cltId = DatabaseUtils.generateId("cltID", daoCliente);
                String debId = DatabaseUtils.generateId("debID", daoDeuda);
                String accId = daoCuenta.getUsers().get(StartVar.accSelect).cuenta;

                Cliente objClt = null;

                //Datos basicos del cliente
                objClt = new Cliente(
                        cltId, nombre, alias, "@null", 0,
                        currdate,0f, currdate, currSel3, BitsOper.setBitInHexString(hexString, currtAcc, checkEstat)
                );
                daoCliente.insertUser(objClt);

                //Datos de deudas y monto fijo
                Deuda objDeb = new Deuda(
                        debId, accId, cltId, rent, 0, 0, currdate, (swEstat?1:0), 0,
                        CalcCalendar.getCorrectDate(currdate, currtTyp), 0,0d, (swEstat?"@null":currdate)
                );
                daoDeuda.insertUser(objDeb);
            }
            else {

                String cltId = mClt.cliente;

                if(mDeb == null){
                    String debId = DatabaseUtils.generateId("debID", daoDeuda);

                    String accId = daoCuenta.getUsers().get(StartVar.accSelect).cuenta;

                    //Datos de deudas y monto fijo
                    Deuda objDeb = new Deuda(
                            debId, accId, cltId, rent, 0, 0, currdate, (swEstat?1:0), 0,
                            CalcCalendar.getCorrectDate(currdate, currtTyp), currSel3,0d, (swEstat?"@null":currdate)
                    );
                    daoDeuda.insertUser(objDeb);

                    daoCliente.updateUser(
                            cltId, nombre, alias, "@null", 0,
                            mClt.fecha, 0f,  mClt.ulfech
                    );

                    daoCliente.updateBits(cltId, BitsOper.setBitInHexString(hexString, currtAcc, checkEstat));
                }
                else {
                    Log.d("Monto", "-->>>>>>>>>>>>>>>>>>>>>>>>>>>> Aqui hayyyyyy: " + mClt.cliente);

                    daoCliente.updateUser(
                            cltId, nombre, alias, "@null", 0,
                            mClt.fecha, 0f,  mClt.ulfech
                    );

                    daoCliente.updateBits(cltId, BitsOper.setBitInHexString(hexString, currtAcc, checkEstat));

                    String mUltFec = mDeb.ulfech;
                    if(!mDeb.disabfec.equals("@null") && swEstat){
                        //Hace comparaciones de fecha para determinar si es necesario actualizar mUltFec
                        String startMonthDate = CalcCalendar.getCorrectDate(mDeb.disabfec, currtTyp);
                        if(CalcCalendar.getRangeMultiple(startMonthDate, currtTyp) > 0) {
                            mUltFec = CalcCalendar.getCorrectDate(currdate, currtTyp);
                        }
                    }
                    daoDeuda.updateFormCltWin(
                            mDeb.deuda, rent, (swEstat ? 1 : 0), mUltFec,
                            currSel3, (swEstat ? "@null" : currdate)
                    );
                }
            }

            //Actualisza la lista de fechas
            CalcCalendar.addCurrentMonthIfAbsent(mContext);

            Basic.msg("Se han GUARDADO los cambios");

            if(currSel2 == 0){
                mSpin2.setSelection(0);
                mInput1.setText("");
                mInput2.setText("");
                mInput3.setText(Basic.setFormatter("0"));
            }

            DBListCreator.createDbLists(); //Actualiza la lista para exportar csv
        }

        if(itemId == R.id.buttNext){
            // 2 = next
            setAdapterClt(2);
        }

        if(itemId == R.id.buttPrev){
            // 1 = prev
            setAdapterClt(1);
        }
    }

    private int nextItem(List<?> mList, int currentIndex) {
        if (mList.size() < 2) {
            Basic.msg("La Lista esta VACIA!");
            return 0;
        }
        return (currentIndex + 1) % mList.size();  // Wrap-around: si llega al final, vuelve al inicio
    }

    private int prevItem(List<?> mList, int currentIndex) {
        if (mList.size() < 2) {
            Basic.msg("La Lista esta VACIA!");
            return 0;
        }
        return (currentIndex - 1 + mList.size()) % mList.size();  // Wrap-around: si va antes del inicio, va al final
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private void setAdapterClt(int dir){
        String accId = mAcc.cuenta;

        listCliente.clear();
        List<String> mCltList = new ArrayList<>();
        mCltList.add("Agregar");

        for(Cliente mC : daoCliente.getUsers()){

            if(currSel1 == 0) {
                mCltList.add(mC.nombre);
                listCliente.add(mC);
            }

            else if (currSel1 == 1 ) {
                if(BitsOper.isActiveBit( mC.bits, currtAcc)){
                    mCltList.add(mC.nombre);
                    listCliente.add(mC);
                }
            }

            else if (currSel1 == 2) {
                if(!BitsOper.isActiveBit( mC.bits, currtAcc)){
                    mCltList.add(mC.nombre);
                    listCliente.add(mC);
                }
            }

            else if (currSel1 == 3) {
                Deuda mD = daoDeuda.getUserByCltAndAcc(mC.cliente, accId);

                if(mD != null && mD.estat == 1){
                    mCltList.add(mC.nombre);
                    listCliente.add(mC);
                }
            }

            else if (currSel1 == 4) {
                Deuda mD = daoDeuda.getUserByCltAndAcc(mC.cliente, accId);

                if(mD == null || mD.estat == 0){
                    mCltList.add(mC.nombre);
                    listCliente.add(mC);
                }
            }
        }
        SelecAdapter adapt1 = new SelecAdapter(mContext, mCltList);
        mSpin2.setAdapter(adapt1);

        if(dir == 1){
            mSpin2.setSelection(prevItem(mCltList, currSel2));
        }
        else if(dir == 2){
            mSpin2.setSelection(nextItem(mCltList, currSel2));
        }
        else {
            mSpin2.setSelection(0);
            mInput1.setText("");
            mInput2.setText("");
            mInput3.setText(Basic.setFormatter("0"));
        }
    }

    private void setChekboxStatus(Cliente mC) {
        hexString = mC.bits;
        List<Integer> bitList = BitsOper.getBits(hexString);

        if (bitList.isEmpty()) {
            bitList.add(0);  // Caso base: al menos un grupo de 0
        }

        int globalPos = currtAcc;

        // Cálculo correcto: grupo y offset
        int group = globalPos / 32;
        int offset = globalPos % 32;

        // Padda con 0 si el grupo no existe
        while (group >= bitList.size()) {
            bitList.add(0);
        }

        int mByte = bitList.get(group);

        // Extracción del bit
        boolean mRes = BitsOper.bitR(mByte, offset) == 1;

        mCheck.setChecked(mRes);
        checkEstat = mRes;
        mCheck.setText("Visible para: " + mAcc.nombre);

        // Debug opcional (usa Log.d en Android)
        // Log.d("setChekboxStatus", String.format("%s - grupo:%d offset:%d - bit: %b - pos: %d",
        //     mC.nombre, group, offset, mRes, globalPos));
    }
}
