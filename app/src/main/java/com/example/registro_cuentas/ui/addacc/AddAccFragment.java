package com.example.registro_cuentas.ui.addacc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.registro_cuentas.AppDBacc;
import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Cuenta;
import com.example.registro_cuentas.MainActivity;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.SatrtVar;
import com.example.registro_cuentas.databinding.FragmentAddaccBinding;
import com.example.registro_cuentas.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class AddAccFragment extends Fragment implements View.OnClickListener{

    private FragmentAddaccBinding binding;

    private Context mContext;

    // DB
    private AppDBacc appDBcuenta = SatrtVar.appDBcuenta;

    //Todos los Inputs
    private EditText mInput1;
    private EditText mInput2;
    private EditText mInput3;
    private List<EditText> mInputList = new ArrayList<>();

    //Botones
    private Button mButt1;

    private List<String> mList = new ArrayList<>();
    private String mIndex = "";

    // Para guardar los permisos de app comprobados en main
    private boolean mPermiss = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AddAccViewModel addAccViewModel = new ViewModelProvider(this).get(AddAccViewModel.class);

        binding = FragmentAddaccBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mContext = BaseContext.getContext();

        mInput1 = binding.inputAdd1;
        mInput2 = binding.inputAdd2;
        mInput3 = binding.inputAdd1;
        mButt1 = binding.buttAdd1;

        mButt1.setOnClickListener(this);

        mInputList.add(mInput1);
        mInputList.add(mInput2);
        mInputList.add(mInput3);

        mPermiss = SatrtVar.mPermiss;
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
            mList.add("userID"+mIndex);
            for(int i = 0; i < mInputList.size(); i++) {
                String text = mInputList.get(i).getText().toString();
                text = text.replaceAll("\"", "");
                text = text.replaceAll(",", "");
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
                for(int i = 0; i < mInputList.size(); i++) {
                    mInputList.get(i).setText("");
                }
                Cuenta obj = new Cuenta(mList.get(0), mList.get(1), mList.get(2), mList.get(3), 0, 0, 0, "");
                appDBcuenta.daoUser().insetUser(obj);
                //SE Limpia la lista
                mList.clear();

                //Recarga La lista de la DB ----------------------------
                SatrtVar mVars = new SatrtVar(mContext);
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
}