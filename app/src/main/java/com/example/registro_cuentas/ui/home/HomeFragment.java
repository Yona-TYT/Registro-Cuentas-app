package com.example.registro_cuentas.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.registro_cuentas.AppDBacc;
import com.example.registro_cuentas.AppDBreg;
import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.Cuenta;
import com.example.registro_cuentas.PayAdapter;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.Registro;
import com.example.registro_cuentas.SatrtVar;
import com.example.registro_cuentas.SearchAdapter;
import com.example.registro_cuentas.SelecAdapter;
import com.example.registro_cuentas.databinding.FragmentHomeBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private FragmentHomeBinding binding;

    private Context mContext;

    // DB ----------------------------------------------------------------
    private AppDBacc appDBcuenta = SatrtVar.appDBcuenta;
    private List<Cuenta> listCuenta;
    private List<AppDBreg> appDBregistro = SatrtVar.appDBregistro;
    private List<Registro> listRegistro;
    //--------------------------------------------------------------------

    private ConstraintLayout mConstrain;

    //Todos los Spinner
    private Spinner mSpin1;
    private Spinner mSpin2;
    private int currSel1 = SatrtVar.mCurrency;
    private int currSel2 = SatrtVar.mCurrenrAcc;
    private List<String> mSpinL1= Arrays.asList("Dolar", "Bs");
    //---------------------------------------------------------------------

    private EditText mInput1;

    //Todos los View
    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private List<TextView> mTextList = new ArrayList<>();

    //---------------------------------------------------------------------
    private PayAdapter mPayadapter;
    private ArrayList<String> regdirList = new ArrayList<>();
    private List<String> accnameList = new ArrayList<>();
    //---------------------------------------------------------------------

    //---------------------------------------------------------------------
    private SearchView mSearch1;
    private ListView mLv1;
    //---------------------------------------------------------------------

    private int currIdx = 0;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Para limpiar todas las listas
        empyLists();

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mContext = BaseContext.getContext();

        mConstrain = binding.constrainHome;

        mSpin1 = binding.spinHome1;
        mSpin2 = binding.spinHome2;

        mInput1 = binding.inputHome1;

        mSearch1 = binding.searchBar;

        mLv1 = binding.lv1;

        mLv1.setOnItemClickListener(this);
        mConstrain.setOnClickListener(this);

        // Para eventos al mostrar o ocultar el teclado
        Basic mKeyBoardEvent = new Basic(mContext);
        mKeyBoardEvent.keyboardEvent(mConstrain, mInput1, 0); //opt = 0 is clear elm focus
        //-------------------------------------------------------------------------------------

        mInput1.setText(SatrtVar.mDollar);
        mInput1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //Toast.makeText(mContext, "Siz is "+b, Toast.LENGTH_LONG).show();
                if(b) {
                    mLv1.setVisibility(View.INVISIBLE);
                    mSearch1.setVisibility(View.INVISIBLE);
                }
                else{
                    mLv1.setVisibility(View.VISIBLE);
                    mSearch1.setVisibility(View.VISIBLE);
                }
            }
        });
        mInput1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                String value = mInput1.getText().toString();
                // Actualiza y guarda el estado del selector tipo de moneda ------------------------
                appDBcuenta.daoUser().updateDollar(SatrtVar.saveDataName, value);

                SatrtVar mVars = new SatrtVar(mContext);
                mVars.setDollar(value);
                //----------------------------------------------------------------------------------
            }
        });

        //Para la lista del selector Tipo Moneda ----------------------------------------------------------------------------------------------
        SelecAdapter adapt1 = new SelecAdapter(mContext, mSpinL1);
        mSpin1.setAdapter(adapt1);
        mSpin1.setSelection(currSel1); //Set La Moneda como default
        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel1 = i;
                // Actualiza y guarda el estado del selector tipo de moneda ------------------------
                appDBcuenta.daoUser().updateCurrency(SatrtVar.saveDataName, i);

                SatrtVar mVars = new SatrtVar(mContext);
                mVars.setCurrency(i);
                //----------------------------------------------------------------------------------
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //--------------------------------------------------------------------------------------------

        // Genera la lista de cuentas ---------------------------------------------------------
        listCuenta = appDBcuenta.daoUser().getUsers();
        List<String[]> maccList = new ArrayList<>();
        for (int i = 1; listCuenta.size() > 1 && i < listCuenta.size(); i++){
            String name = listCuenta.get(i).nombre;
            String[] stList= new String[3];
            stList[0] = name;
            stList[1] =name;
            stList[2] = Integer.toString((i-1));
            maccList.add(stList);
            accnameList.add(name);
           //nameList.add(listCuenta.get(i).nombre);
        }
        //Para la lista del selector Cuentas ----------------------------------------------------------------------------------------------
        SelecAdapter adapt2 = new SelecAdapter(mContext, accnameList);
        mSpin2.setAdapter(adapt2);
        mSpin2.setSelection((currSel2==0?0:currSel2-1)); //Set La cuenta como default
        mSpin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel2 = i;
                // Actualiza y guarda el estado del selector de cuentas-----------------------------
                int idx = i+1;
                if(idx < listCuenta.size()) {
                    appDBcuenta.daoUser().updateCurrentAcc(SatrtVar.saveDataName, idx);
                }
                SatrtVar mVars = new SatrtVar(mContext);
                mVars.setCurrentAcc(i);
                //----------------------------------------------------------------------------------

                //Recarga la lista de pagos en funcion de la cuenta seleccionada--------------------
                if(!appDBregistro.isEmpty()) {
                    setRegList();
                }
                //----------------------------------------------------------------------------------
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //--------------------------------------------------------------------------------------------

        // Genera la lista de registros ---------------------------------------------------------
        setRegList();
        //--------------------------------------------------------------------------------------

        //Para el adapter del buscador -------------------------------------------------------
        mSearch1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ArrayList<Integer> idxList = (ArrayList<Integer>)mPayadapter.getItem(0);
                if(!idxList.isEmpty()){
                    setTextView(idxList.get(0), mTextList);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                mPayadapter.getFilter().filter(newText);
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
            currIdx = (int)id;
            //Para ocultar el teclado virtual ----------------------------------------------------------------
            //view.requestFocusFromTouch();
            InputMethodManager imm=(InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            //------------------------------------------------------------------------------------------------
        }
    }

    public void setTextView(int idx, List<TextView> list) {
        Cuenta acc = listCuenta.get(idx);
        int i = 0;
        list.get(i).setText("Cuenta: "+acc.nombre);
        i++;
        list.get(i).setText("Descripcion: "+acc.desc);
        i++;
        list.get(i).setText("Estimado: "+acc.monto);
    }

    public void setRegList(){
        if(!appDBregistro.isEmpty()) {
            listRegistro = appDBregistro.get(currSel2).daoUser().getUsers();
            List<String[]> mregList = new ArrayList<>();
            for (int i = 0; i < listRegistro.size(); i++) {
                String name = listRegistro.get(i).nombre;
                String[] stList = new String[3];
                stList[0] = name;
                stList[1] = listRegistro.get(i).monto;
                ;
                stList[2] = Integer.toString(i);
                mregList.add(stList);
            }
            //Para configurar la lista de pagos
            mPayadapter = new PayAdapter(mContext, mregList);
            mLv1.setAdapter(mPayadapter);
            mPayadapter.getFilter().filter("");
        }
        else if(mLv1.getChildCount() > 0){
            mLv1.removeAllViews();
        }
    }

    public void empyLists(){
        mTextList.clear();
        accnameList.clear();
        regdirList.clear();
    }
}