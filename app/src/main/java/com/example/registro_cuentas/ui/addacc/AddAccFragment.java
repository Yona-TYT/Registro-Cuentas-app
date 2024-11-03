package com.example.registro_cuentas.ui.addacc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.registro_cuentas.AppDBacc;
import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.Cuenta;
import com.example.registro_cuentas.CurrencyInput;
import com.example.registro_cuentas.MainActivity;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.databinding.FragmentAddaccBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddAccFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener{

    private FragmentAddaccBinding binding;

    private Context mContext = BaseContext.getContext();

    // DB
    private AppDBacc appDBcuenta = StartVar.appDBcuenta;

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

    // Para guardar los permisos de app comprobados en main
    private boolean mPermiss = false;

    private Basic mBasic = new Basic(BaseContext.getContext());

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AddAccViewModel addAccViewModel = new ViewModelProvider(this).get(AddAccViewModel.class);

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
        mButt1 = binding.buttAdd1;

        mButt1.setOnClickListener(this);
        mInput1.setOnFocusChangeListener(this);
        mInput2.setOnFocusChangeListener(this);

        mInputList.add(mInput1);
        mInputList.add(mInput2);
        mInputList.add(mInput3);

        //Efecto moneda
        //-------------------------------------------------------------------------------------------------------
        String curr = mCurrencyList.get(StartVar.mCurrency);
        mInput3.setText(Basic.setMask("0", curr));
        List<View> mViewL1 = new ArrayList<>();
        mViewL1.add(mNavBar);
        int mOpt = 0;
        CurrencyInput mCInput = new CurrencyInput( mContext, mInput3,  mViewL1, curr, mOpt);
        mCInput.set();
        //----------------------------------------------------------------------------------------------------

        // Para eventos al mostrar o ocultar el teclado-----
        mBasic.steAllKeyEvent(mConstrain, mInputList);
        //-----------------------------------------------

        mPermiss = StartVar.mPermiss;
        mIndex = "" + appDBcuenta.daoUser().getUsers().size();
        if (mIndex.isEmpty()) {
            mIndex = "0";
        }
        
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
            mList.add("userID"+mIndex);
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
                String monto = Basic.setValue(mList.get(3));
                if(monto.isEmpty() || Float.parseFloat(monto) <= 0.0){
                    //MSG Para entrada de monto
                    msgIdx = 2;
                    setMessage(msgIdx);
                    return;
                }
                Cuenta obj = new Cuenta(mList.get(0), mList.get(1), mList.get(2), monto, 0, 0, 0,0,"0");
                appDBcuenta.daoUser().insetUser(obj);
                //SE Limpia la lista
                mList.clear();

                //Recarga La lista de la DB ----------------------------
                StartVar mVars = new StartVar(mContext);
                mVars.getAccListDB();
                //-------------------------------------------------------
                //-------------------------------------------------------
                //Actualiza la db para los registros
                mVars.setRegListDB();
                //--------------------------------

                //Esto inicia las actividad Main despues de tiempo de espera del preloder
                startActivity(new Intent(mContext, MainActivity.class));
                //finish(); //Finaliza la actividad y ya no se accede mas
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