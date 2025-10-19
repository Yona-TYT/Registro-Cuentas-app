package com.example.registro_cuentas.ui.home;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import android.widget.SearchView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.registro_cuentas.activitys.AccDtailsActivity;
import com.example.registro_cuentas.db.AllDao;
import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.CalcCalendar;
import com.example.registro_cuentas.db.Cliente;
import com.example.registro_cuentas.adapters.CltAdapter;
import com.example.registro_cuentas.db.Cuenta;
import com.example.registro_cuentas.CurrencyEditText;
import com.example.registro_cuentas.db.dao.DaoAcc;
import com.example.registro_cuentas.db.dao.DaoClt;
import com.example.registro_cuentas.db.dao.DaoDeb;
import com.example.registro_cuentas.db.dao.DaoPay;
import com.example.registro_cuentas.db.Deuda;
import com.example.registro_cuentas.db.Fecha;
import com.example.registro_cuentas.GetDollar;
import com.example.registro_cuentas.adapters.PayAdapter;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.db.Pagos;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.adapters.SelecAdapter;
import com.example.registro_cuentas.databinding.FragmentHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnFocusChangeListener{

    private FragmentHomeBinding binding;

    private Context mContext;

    // DB ----------------------------------------------------------------
    private AllDao appDBcuenta = StartVar.appDBall;

    private List<Cuenta> listCuenta;
    private List<Pagos> appDBregistro = StartVar.listreg;
    private List<Pagos> listPagos = new ArrayList<>();
    //--------------------------------------------------------------------

    //--------------------------
    private DaoPay daoRegistro = StartVar.appDBall.daoPay();
    private DaoDeb daoDeuda = StartVar.appDBall.daoDeb();
    private List<Deuda> listDeuda = new ArrayList<>();

    private DaoClt daoCliente = StartVar.appDBall.daoClt();
    private List<Cliente> listCliente = new ArrayList<>();

    private DaoAcc daoCuenta = StartVar.appDBall.daoAcc();
    private Cuenta mAcc;
    //------------------------------------

    private ConstraintLayout mConstrain;
    private BottomNavigationView mNavBar = StartVar.mNavBar;

    //Todos los Spinner
    private Spinner mSpin1;
    private Spinner mSpin2;
    private Spinner mSpin3;
    private Spinner mSpin4;

    private int currSel1 = StartVar.mCurrency;
    private int currSel2 = StartVar.mCurrAcc;
    private int currSel3 = StartVar.mCurrMes;
    private int currSel4 = StartVar.currSel4;

    private List<String> mSpinL1= Arrays.asList("Dolar", "Bolivar");
    private List<String> mSpinL4= Arrays.asList("Pagos", "Clientes");
    private List<String> mCurrencyList= Arrays.asList("$", "Bs");
    private String mCurr = "";
    //---------------------------------------------------------------------

    private CurrencyEditText mInput1;
    private Button mButt1;

    //---------------------------------------------------------------------
    private PayAdapter mPayadapter;
    private ArrayList<String> regdirList = new ArrayList<>();
    private List<String> accnameList = new ArrayList<>();
    //---------------------------------------------------------------------

    private CltAdapter mCltadapter;

    //---------------------------------------------------------------------
    private SearchView mSearch1;
    private ListView mLv1;
    //---------------------------------------------------------------------

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Para limpiar todas las listas
        empyLists();

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mContext = BaseContext.getContext();

        mConstrain = binding.constrainHome;

        mSpin1 = binding.spinHome1;
        mSpin2 = binding.spinHome2;
        mSpin3 = binding.spinHome3;
        mSpin4 = binding.spinHome4;

        mInput1 = binding.inputHome1;
        mButt1 = binding.buttHome1;

        mSearch1 = binding.searchBar;

        mLv1 = binding.lv1;

        mLv1.setOnItemClickListener(this);
        mConstrain.setOnClickListener(this);
        mSearch1.setOnFocusChangeListener(this);

        GetDollar mGet = new GetDollar(mContext, getActivity(), 0, mInput1);
        try {
            GetDollar.urlRun();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Para guardar el precio del dolar
        mInput1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    String value = Double.toString(mInput1.getNumericValue());
                    // Actualiza y guarda el Precio del dolar ------------------------
                    StartVar.appDBall.daoCfg().updateDolar(StartVar.mConfID, value);
                    StartVar mVars = new StartVar(mContext);
                    mVars.setDollar(value);
                    //----------------------------------------------------------------------------------

                    //Recarga la lista de pagos o lista dwe clientes -----------------------------------
                    //Lista de pagos
                    if(currSel4==0){
                        setPayList();
                    }
                    //Lista de Clientes
                    else {
                        setCltList();
                    }
                    //----------------------------------------------------------------------------------
                }
                else{
                    // Para eventos al mostrar o ocultar el teclado
                    List<View> mViewL1 = Arrays.asList(mLv1, mNavBar, mSearch1);
                    setHiddenObjc(mViewL1, mInput1);
                    //-----------------------------------------------
                }
            }
        });

        //Efecto moneda
        //---------------------------------------------------------------------------------------------
        //Basic.msg(""+StartVar.mDollar);
        mInput1.setText(Basic.setFormatter(StartVar.mDollar));

        //Para la lista del selector Tipo Moneda ------------------------------------------------------
        SelecAdapter adapt1 = new SelecAdapter(mContext, mSpinL1);
        mSpin1.setAdapter(adapt1);
        mSpin1.setSelection(currSel1); //Set La Moneda como default
        mCurr = mCurrencyList.get(currSel1);
        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel1 = i;
                mCurr = mCurrencyList.get(i);

                // Actualiza y guarda el estado del selector tipo de moneda ------------------------
                StartVar.appDBall.daoCfg().updateMoneda(StartVar.mConfID, i);

                StartVar mVars = new StartVar(mContext);
                mVars.setCurrency(i);
                //----------------------------------------------------------------------------------
                //Recarga la lista de pagos o lista dwe clientes -----------------------------------
                //Lista de pagos
                if(currSel4==0){
                    setPayList();
                }
                //Lista de Clientes
                else {
                    setCltList();
                }
                //----------------------------------------------------------------------------------
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //--------------------------------------------------------------------------------------------
        //Para la lista del selector Cuentas --------------------------------------------------------
        // Genera la lista de cuentas ---------------------------------------------------------
        listCuenta = daoCuenta.getUsers();
        List<String[]> maccList = new ArrayList<>();
        if(!listCuenta.isEmpty()) {
            mButt1.setOnClickListener(this);

            for (int i = 0;  i < listCuenta.size(); i++){
                String name = listCuenta.get(i).nombre;
                String desc = listCuenta.get(i).desc;
                String[] stList= new String[3];
                stList[0] = name;
                stList[1] =name;
                stList[2] = Integer.toString(i);
                maccList.add(stList);
                accnameList.add(name + (desc.replaceAll("[^a-zA-Z0-9]", "").isEmpty()? "" : " ("+desc+")"));
                //nameList.add(listCuenta.get(i).nombre);
            }
        }
        else {
            mButt1.setEnabled(false);
            String[] stList= new String[3];
            stList[0] = "NA";
            stList[1] = "NA";
            stList[2] = Integer.toString(0);
            maccList.add(stList);

            accnameList.add("Vacio (No hay Cuentas Disponibles)");

        }

        SelecAdapter adapt2 = new SelecAdapter(mContext, accnameList);
        mSpin2.setAdapter(adapt2);
        mSpin2.setSelection(currSel2); //Set La cuenta como default
        mSpin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel2 = i;
                // Actualiza y guarda el estado del selector de cuentas-----------------------------
                StartVar mVars = new StartVar(mContext);
                int idx = i;
                if(!listCuenta.isEmpty()) {
                    mAcc = appDBcuenta.daoAcc().getUsers().get(idx);
                    StartVar.appDBall.daoCfg().updateCurrAcc(StartVar.mConfID, idx);
                    mVars.setCurrentTyp(mAcc.acctipo);
                }
                mVars.setCurrentAcc(i);

                //----------------------------------------------------------------------------------

                //Recarga la lista de pagos y clientes en funcion de la cuenta seleccionada--------------------

                //Lista de pagos
                if(currSel4==0){
                    setPayList();
                }
                //Lista de Clientes
                else {
                    setCltList();
                }
                //----------------------------------------------------------------------------------
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //--------------------------------------------------------------------------------------------

        //Para la lista del selector Fechas ------------------------------------------------------------
        // Genera la lista de fechas ---------------------------------------------------------
        List<String> fechaList = new ArrayList<>();
        fechaList.add("Todos");
        String curM = "";
        String curY = "";
        List<Fecha> listFecha = StartVar.appDBall.daoDat().getUsers();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate currdate = LocalDate.now();
            curM = currdate.getMonth().toString();
            curY = Integer.toString(currdate.getYear());
        }
        for (int i = (listFecha.size()-1); i >=0 ; i--){
            String mes = listFecha.get(i).mes;
            String year = listFecha.get(i).year;
            fechaList.add(mes+" ("+year+")");
            if(curM.equals(mes) && curY.equals(year)){
                currSel3 = i;
            }
        }
        SelecAdapter adapt3 = new SelecAdapter(mContext, fechaList);
        mSpin3.setAdapter(adapt3);
        mSpin3.setSelection(currSel3); //Set La fecha default
        mSpin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int count = i==0?0 :adapterView.getCount();
                currSel3 = (count - i);
                StartVar.appDBall.daoCfg().updateMes(StartVar.mConfID, (count - i));
                StartVar mVars = new StartVar(mContext);
                mVars.setCurrentMes((count - i));

                if(currSel4 == 0) {
                    //Recarga la lista de pagos en funcion de la cuenta seleccionada--------------------
                    if (!appDBregistro.isEmpty()) {
                        setPayList();
                    }
                }
                else{
                    setCltList();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //--------------------------------------------------------------------------------------------

        //Para la lista del selector Tipo Lista --------------------------------------------------------
        SelecAdapter adapt4 = new SelecAdapter(mContext, mSpinL4);
        mSpin4.setAdapter(adapt4);
        mSpin4.setSelection(currSel4); //Set La lista como default
        mSpin4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel4 = i;
                //Lista de pagos
                if(i==0){
                    setPayList();
                }
                //Lista de Clientes
                else {
                    setCltList();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //--------------------------------------------------------------------------------------------

        // Genera la lista de registros ---------------------------------------------------------
        setPayList();
        //--------------------------------------------------------------------------------------

        //Para el adapter del buscador -------------------------------------------------------
        mSearch1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(currSel4 == 0) {
                    ArrayList<Integer> idxList = (ArrayList<Integer>) mPayadapter.getItem(0);
                }
                else{
                    ArrayList<Integer> idxList = (ArrayList<Integer>) mCltadapter.getItem(0);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(currSel4 == 0) {
                    mPayadapter.getFilter().filter(newText);
                }
                else{
                    mCltadapter.getFilter().filter(newText);
                }
                return false;
            }
        });
        //----------------------------------------------------------------------------------------
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
        if (itemId == R.id.butt_home1) {
            Application application = (Application) mContext.getApplicationContext();
            Intent mIntent = new Intent(mContext, AccDtailsActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(mIntent);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
        int itemId = adapterView.getId();
        if (itemId == R.id.constrain_home) {
            mLv1.setVisibility(View.VISIBLE);
            mSearch1.setVisibility(View.VISIBLE);
        }
        if (itemId == R.id.lv1) {
            //Log.d("PhotoPicker", " Aquiiiiiiiiii Hayyyyyy 11100------------------------: " + position);
            //nextViewActivity((int)id);
            //Para ocultar el teclado virtual ----------------------------------------------------------------
            //view.requestFocusFromTouch();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            //------------------------------------------------------------------------------------------------
        }
    }

    public void setPayList(){
        if(mAcc == null){
            return;
        }

        listPagos = StartVar.appDBall.daoPay().getListByGroupId(mAcc.cuenta);
        List<Fecha> listFecha = StartVar.appDBall.daoDat().getUsers();
        int idx = currSel3 - 1;
        Fecha selFecha = listFecha.get(Math.max(idx, 0));
        List<Object[]> mregList = new ArrayList<>();
        for (int i = 0; i < listPagos.size(); i++) {
            Pagos reg = listPagos.get(i);
            DaoClt mDao = StartVar.appDBall.daoClt();
            String name = mDao.getSaveName(reg.cltid);
            String fecha = reg.fecha;
            if(currSel3 == 0) {
                Object[] stList = new Object[5];
                stList[0] = i;
                stList[1] = name;
                stList[2] = reg.monto;
                stList[3] = fecha;
                stList[4] = reg.oper;
                mregList.add(stList);
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate date = LocalDate.parse(fecha);
                    if(date.getMonth().toString().equals(selFecha.mes)){
                        Object[] stList = new Object[5];
                        stList[0] = i;
                        stList[1] = name;
                        stList[2] = reg.monto;
                        stList[3] = fecha;
                        stList[4] = reg.oper;
                        mregList.add(stList);
                    }
                }
            }
        }
        //Para configurar la lista de pagos
        mPayadapter = new PayAdapter(mContext, mregList);
        mLv1.setAdapter(mPayadapter);
        mPayadapter.getFilter().filter("");
    }

    public void setCltList(){
        if(mAcc == null){
            return;
        }
        List<Cliente> listClt = daoCliente.getUsers();
        List<Object[]> cltList = new ArrayList<>();

//        for (Deuda mD : daoDeuda.getUsers()) {
//            Basic.msg("" + mD.cltid);
//        }

        //Basic.msg(""+listDeb.size());
        for (int i = 0; i < listClt.size(); i++) {
            Cliente clt = listClt.get(i);
            Deuda mDeb = null;

            for (Deuda mD : daoDeuda.getListByGroupId(clt.cliente)){
                if(mD.accid.equals(mAcc.cuenta)){
                    mDeb = mD;
                    break;
                }
            }

            if(mDeb==null){
                continue;
            }
            Object[] stList = new Object[4];
            stList[0] = i;

            String ultFec = mDeb.ulfech;
            if (ultFec.isEmpty()){
                ultFec = clt.fecha;
            }
            Float debe = mDeb.paid;
            Float rent = mDeb.rent;
            int isDeb = mDeb.pagado;
            int mTyp =  StartVar.mCurrTyp;
            int mult = CalcCalendar.getRangeMultiple(ultFec, mTyp);
            String monto = Float.toString(Basic.getDebt(mult, rent, debe));

            String txA = "";
            String txB = "";
            //Basic.msg(mult+" "+clt.nombre+" t"+monto);
            if(mTyp != 0) {
                if(mDeb.rent == 0 || mDeb.estat == 0) {
                    continue;
                }
                if (isDeb == 0 && rent <= 0) {
                    txA = " [Sin Registros] "+ ultFec;
                    txB = "[NA]";
                } else if (isDeb == 1 || mult > 0) {
                    txA = " [" + (mDeb.oper == 0 ? "+" : "-") + Basic.setFormatter(monto) + " " + mCurr + "] ";
                    txB = " [PENDIENTE]";
                } else {
                    txA = " [Pagado] ";
                    txB = "Ult: " + ultFec;
                }
            }
            else {
                if (isDeb == 0 && rent <= 0) {
                    txA = " [Sin Registros]";
                    txB = "[NA]";
                }
                else {
                    txA = "  ";
                    txB = " Ult: " + ultFec;
                }
            }
            stList[1] = clt.nombre;
            stList[2] = txA;
            stList[3] = txB;

            cltList.add(stList);
        }
        //Para configurar la lista de pagos
        mCltadapter = new CltAdapter(mContext, cltList);
        mLv1.setAdapter(mCltadapter);
        mCltadapter.getFilter().filter("");
    }

    public void empyLists(){
        accnameList.clear();
        regdirList.clear();
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        //Toast.makeText(mContext, "Siz is "+b, Toast.LENGTH_LONG).show();
        int itemId = view.getId();
        if (itemId == R.id.searchBar) {
            if (b) {
                mNavBar.setVisibility(View.INVISIBLE);
            }
            else {
                mNavBar.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setHiddenObjc(List<View> mList, View objc){
        //----------------------------------------------------------------------------------------------------
        Basic mKeyBoardEvent = new Basic(mContext);
        mKeyBoardEvent.keyboardEvent(mConstrain, objc, mList, 0); //opt = 0 is clear elm focus
        //-------------------------------------------------------------------------------------
    }

}