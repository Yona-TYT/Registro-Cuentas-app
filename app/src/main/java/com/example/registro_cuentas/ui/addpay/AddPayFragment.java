package com.example.registro_cuentas.ui.addpay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.registro_cuentas.AppDBclt;
import com.example.registro_cuentas.AppDBreg;
import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Cliente;
import com.example.registro_cuentas.Cuenta;
import com.example.registro_cuentas.MainActivity;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.Registro;
import com.example.registro_cuentas.SatrtVar;
import com.example.registro_cuentas.databinding.FragmentAddpayBinding;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPayFragment extends Fragment implements View.OnClickListener{

    private FragmentAddpayBinding binding;

    private Context mContext;

    // DB
    private List<AppDBreg> appDBregistro = SatrtVar.appDBregistro;
    private AppDBclt appDBcliente = SatrtVar.appDBcliente;
    private List<Cliente> listCliente = new ArrayList<>();

    //Todos los Inputs
    private EditText mInput1;
    private EditText mInput2;
    private EditText mInput3;
    private List<EditText> mInputList = new ArrayList<>();

    //Botones
    private Button mButt1;
    private Button mButt2;

    //Selectores


    private List<String> mList = new ArrayList<>();
    private String mIndex = "";

    // Para guardar los permisos de app comprobados en main
    private boolean mPermiss = false;
    // Index de cuenta actual
    private int currtAcc = SatrtVar.mCurrenrAcc;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        AddPayViewModel addPayViewModel = new ViewModelProvider(this).get(AddPayViewModel.class);

        binding = FragmentAddpayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mContext = BaseContext.getContext();

        mInput1 = binding.inputPay1;
        mInput2 = binding.inputPay2;
        mInput3 = binding.inputPay3;
        mButt1 = binding.buttPay1;

        mButt1.setOnClickListener(this);

        mInputList.add(mInput1);
        mInputList.add(mInput2);
        mInputList.add(mInput3);

        mPermiss = SatrtVar.mPermiss;
        if(!appDBregistro.isEmpty()) {
            mIndex = "" + appDBregistro.get(currtAcc).daoUser().getUsers().size();
            if (mIndex.isEmpty()) {
                mIndex = "0";
            }
        }

        listCliente = appDBcliente.daoUser().getUsers();

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
                    mInputList.get(i).setText("");
                }
                //Comprueba que la lista de cuentas no este vacia
                SatrtVar mVars = new SatrtVar(mContext);
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
                        mList.get(0), mList.get(1), mList.get(2), mList.get(3), 0, 0,
                        "5", currdate.toString(), currtime.toString(), cltId, "", "", ""
                    );
                    appDBregistro.get(currtAcc).daoUser().insetUser(obj);

                   if(newClt){
                       cltId = ""+listCliente.size();
                       Toast.makeText(mContext, "Siz is "+ cltId, Toast.LENGTH_LONG).show();
                       Cliente objClt = new Cliente(cltId,"","",0, currdate.toString());
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
    }

    public static String[] dataValidate(String text){
        Pattern patt = Pattern.compile("(^{"+text+"}$)");
        Matcher matcher = patt.matcher(text);
        if(matcher.find()) {
            if (text.contains("-")) {
                return text.split("-");
            }
            else if (text.contains("/")) {
                return text.split("/");
            }
            else if (text.contains(".")) {
                return text.split("\\.");
            }
            else {
                return null;
            }
        }
        return null;
    }
}