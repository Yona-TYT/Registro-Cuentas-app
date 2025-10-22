package com.example.registro_cuentas.ui.addacc;

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

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.registro_cuentas.activitys.ReloadActivity;
import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.db.Cuenta;
import com.example.registro_cuentas.CurrencyEditText;
import com.example.registro_cuentas.activitys.MainActivity;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.adapters.SelecAdapter;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.databinding.FragmentAddaccBinding;
import com.example.registro_cuentas.db.DatabaseUtils;
import com.example.registro_cuentas.db.dao.DaoAcc;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddAccFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener{

    private FragmentAddaccBinding binding;

    private Context mContext = BaseContext.getContext();

    // DB
    private DaoAcc daoCuenta = StartVar.appDBall.daoAcc();

    //Todos los Inputs
    private EditText mInput1;
    private EditText mInput2;
    private CurrencyEditText mInput3;
    private List<EditText> mInputList = new ArrayList<>();

    //Todos los Spinner
    private Spinner mSpin1;
    private int currSel1 = 0;
    private List<String> mSpinL1 = Arrays.asList("Sin Cierre", "Cierre Dia", "Cierre Mes", "Cierre Año");
    //---------------------------------------------------------------------

    private ConstraintLayout mConstrain;
    private BottomNavigationView mNavBar = StartVar.mNavBar;

    //Botones
    private Button mButt1;

    private String mIndex = "";

    private List<String> mCurrencyList= Arrays.asList("$", "Bs");

    // Para guardar los permisos de app comprobados en main
    private boolean mPermiss = false;

    private Basic mBasic = new Basic(BaseContext.getContext());

    private String mCurr = mCurrencyList.get(StartVar.mCurrency);

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        binding = FragmentAddaccBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mConstrain = binding.constrainAcc;

        mInput1 = binding.inputAdd1;
        mInput2 = binding.inputAdd2;
        mInput3 = binding.inputAdd3;
        mSpin1 = binding.spinAdd1;
        mButt1 = binding.buttAdd1;

        mButt1.setOnClickListener(this);
        mInput1.setOnFocusChangeListener(this);
        mInput2.setOnFocusChangeListener(this);

        mInputList.add(mInput1);
        mInputList.add(mInput2);

        //Efecto moneda
        //-------------------------------------------------------------------------------------------------------
        mInput3.setCurrencySymbol(mCurr, true);
        mInput3.setText(Basic.setFormatter("0"));
        List<View> mViewL1 = new ArrayList<>();
        mViewL1.add(mNavBar);
        // Para eventos al mostrar o ocultar el teclado
        Basic mKeyBoardEvent = new Basic(mContext);
        mKeyBoardEvent.keyboardEvent(mConstrain, mInput1, mViewL1, 0); //opt = 0 is clear elm focus
        //-------------------------------------------------------------------------------------

        //--------------------------------------------------------------------------------------------
        //Para la lista del selector Tipo De Cuenta ----------------------------------------------------------------------------------------------
        SelecAdapter adapt1 = new SelecAdapter(mContext, mSpinL1);
        mSpin1.setAdapter(adapt1);
        //mSpin1.setSelection(mAcc.acctipo); //Set default ingreso

        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel1 = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //--------------------------------------------------------------------------------------------

        mPermiss = StartVar.mPermiss;
        mIndex = DatabaseUtils.generateId("accID", daoCuenta);

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
        if (itemId == R.id.butt_add1) {
            boolean result = true;
            int msgIdx = 0;
            List<String> mList = new ArrayList<>();
            mList.add(mIndex);
            for(int i = 0; i < mInputList.size(); i++) {
                String text = mInputList.get(i).getText().toString();
                text = Basic.inputProcessor(text); //Elimina caracteres que afectan a los csv
                if (text.isEmpty()){
                    if(i == 2) {
                        //MSG para entrada de Monto
                        msgIdx = 3;
                    }
                    else if (i == 3) {
                        //MSG para entrada de...
                        msgIdx = 2;
                    }
                    result = false;
                    break;
                }
                mList.add(text);
            }
            if (result) {
                //Para Limpiar Todos Los inputs
                for (int i = 0; i < mInputList.size(); i++) {
                    EditText mInput = mInputList.get(i);
                    mInput.setText("");
                    mInput.setSelection(0);
                    mInput.clearFocus();
                }
                String monto = Basic.setValue(Double.toString(mInput3.getNumericValue()));
                if(monto.isEmpty() || Basic.parseFloat(monto) < 0.0){
                    //MSG Para entrada de monto
                    msgIdx = 2;
                    setMessage(msgIdx);
                    return;
                }

                String currdate = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    currdate = LocalDate.now().toString();
                }

                Cuenta obj = new Cuenta(mList.get(0), mList.get(1), mList.get(2), monto, currSel1, 0, 0,0,"0", currdate);
                daoCuenta.insertUser(obj);
                //SE Limpia la lista
                mList.clear();

                //Recarga La lista de la DB ----------------------------
                StartVar mVars = new StartVar(mContext);
                mVars.getAccListDB();
                //-------------------------------------------------------

                //Esto inicia las actividad Reload
                startActivity(new Intent(mContext, ReloadActivity.class));
            }
        }
    }
    public void setMessage(int idx){
        if(idx == 0){
            Basic.msg("Ingrese NOMBRE Cliente!.");
        }
        else if(idx == 1){
            Basic.msg("El nombre Cliente Ya EXISTE!.");
        }
        else if(idx == 2){
            Basic.msg("Ingrese un MONTO Valido!.");
        }
        else if(idx == 3){
            Basic.msg("");
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        //Toast.makeText(mContext, "Siz is "+b, Toast.LENGTH_LONG).show();
        if (b) {
            mNavBar.setVisibility(View.INVISIBLE);
        } else {
            mNavBar.setVisibility(View.VISIBLE);
        }
    }
}