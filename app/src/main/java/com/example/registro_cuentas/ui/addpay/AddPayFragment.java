package com.example.registro_cuentas.ui.addpay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.registro_cuentas.AppDBclt;
import com.example.registro_cuentas.AppDBdeb;
import com.example.registro_cuentas.AppDBreg;
import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.CalcCalendar;
import com.example.registro_cuentas.Cliente;
import com.example.registro_cuentas.CurrencyEditText;
import com.example.registro_cuentas.CurrencyInput;
import com.example.registro_cuentas.DaoClt;
import com.example.registro_cuentas.Deuda;
import com.example.registro_cuentas.FilesManager;
import com.example.registro_cuentas.MainActivity;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.Registro;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.SelecAdapter;
import com.example.registro_cuentas.databinding.FragmentAddpayBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddPayFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener{

    private FragmentAddpayBinding binding;

    private Context mContext = BaseContext.getContext();

    // DB
    private List<AppDBreg> appDBregistro = StartVar.appDBregistro;
    private List<AppDBdeb> appDBdeuda = StartVar.appDBdeuda;

    private AppDBclt appDBcliente = StartVar.appDBcliente;
    private List<Cliente> listCliente = new ArrayList<>();

    private ConstraintLayout mConstrain;
    private BottomNavigationView mNavBar = StartVar.mNavBar;

    //Todos los Inputs
    private EditText mInput1;
    private EditText mInput2;
    private EditText mInput3;
    private CurrencyEditText mInput4;
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
    private boolean swPorc = false;

    private ImageButton mBtnImg1;
    private ImageView imageView1;

    private String mIndex = "";

    // Para guardar los permisos de app comprobados en main
    private boolean mPermiss = false;
    // Index de cuenta actual
    private int currtAcc = StartVar.mCurrAcc;
    private int currtTyp = StartVar.mCurrTyp;

    private String mCurr = mCurrencyList.get(StartVar.mCurrency);

    private Basic mBasic = new Basic(BaseContext.getContext());

    private FilesManager mFileM = new FilesManager(BaseContext.getContext());
    private String sImage = "";
    private Uri oldFile = null;
    private Uri currUri = null;

    @SuppressLint("DefaultLocale")
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
        mBtnImg1 = binding.buttPay2;
        imageView1 = binding.imagePay1;

        mInput1.setOnFocusChangeListener(this);
        mInput2.setOnFocusChangeListener(this);
        mInput3.setOnFocusChangeListener(this);
        mConstrain.setOnClickListener(this);
        mButt1.setOnClickListener(this);
        mBtnImg1.setOnClickListener(this);
        mSw.setOnClickListener(this);

        // Set imagen picker-----------------
        //mFileM.setImgPicker(imageView1, mBtnImg1);
        //------------------------------------

        mInputList.add(mInput1);
        mInputList.add(mInput2);
        mInputList.add(mInput3);
        //mInputList.add(mInput4);

        //Efecto moneda
        //-------------------------------------------------------------------------------------------------------
        mInput4.setCurrencySymbol(mCurr, true);
        mInput4.setText(Basic.setFormatter("0"));
        List<View> mViewL1 = new ArrayList<>();
        mViewL1.add(mNavBar);
        // Para eventos al mostrar o ocultar el teclado
        Basic mKeyBoardEvent = new Basic(mContext);
        mKeyBoardEvent.keyboardEvent(mConstrain, mInput1, mViewL1, 0); //opt = 0 is clear elm focus
        //-------------------------------------------------------------------------------------

        mPermiss = StartVar.mPermiss;
        if(!appDBregistro.isEmpty()) {
            mIndex = "" + appDBregistro.get(currtAcc).daoUser().getUsers().size();
            if (mIndex.isEmpty()) {
                mIndex = "0";
            }
        }
        listCliente = appDBcliente.daoUser().getUsers();

        //Para la lista del selector Cliente ----------------------------------------------------------------------------------------------
        List<String> mCltList = new ArrayList<>();
        List<String> mAliList = new ArrayList<>();
        List<String> mIdList = new ArrayList<>();
        mCltList.add("Agregar");
        mAliList.add("");
        mIdList.add("");
        int x = currtAcc;
        int siz = 0;
        for(int i = 0; i < listCliente.size(); i++){
            List<Integer> bitList = Basic.getBits(listCliente.get(i).bits);
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

            if(Basic.bitR(mByte, x) == 1) {
                mCltList.add(listCliente.get(i).nombre);
                mAliList.add(listCliente.get(i).alias);
                mIdList.add(listCliente.get(i).cliente);
            }
//            else {
//                Basic.msg(String.format("%s - %x - %s - %d",listCliente.get(i).nombre, bitList.size(), Basic.bitR(mByte, x) == 1, currtAcc));
//            }
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
                    mButt1.setEnabled(true);
                    mInput1.setText(mCltList.get(i).toUpperCase());
                    mInput1.setEnabled(false);

                    String alias = mAliList.get(i).toUpperCase();
                    if (!alias.isEmpty()) {
                        mInput2.setText(alias);
                        mInput2.setEnabled(false);
                    }
                    Deuda mDeb = appDBdeuda.size() > currtAcc? appDBdeuda.get(currtAcc).daoUser().getUsers(mIdList.get(i)) : null;
                    if(mDeb != null) {
                        if (mDeb.estat == 1) {
                            mInput4.setText(Basic.getValueFormatter(mDeb.total));
                            int mult = CalcCalendar.getRangeMultiple(mDeb.ulfech, currtTyp);
                            float monto = Basic.getDebt(mult, mDeb.total, mDeb.debe);

                            if (mDeb.pagado == 2 && monto <=0) {
                                mButt1.setEnabled(false);
                                Basic.msg("Este cliente no Tine DEUDAS!");
                            }
                        }
                    }
                    else {
                        mInput4.setText(Basic.setFormatter("0"));
                    }
                }
                else{
                    mInput1.setText("");
                    mInput1.setEnabled(true);

                    mInput2.setText("");
                    mInput2.setEnabled(true);

                    mInput4.setText(Basic.setFormatter("0"));
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



    // Registers a photo picker activity launcher in single-select mode.
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    imageView1.setImageURI(uri);
                    currUri = uri;
                }
                else {
                    Basic.msg("No hay imagen seleccionada!");
                }
            });
    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.butt_pay2) {
            if (StartVar.mPermiss) {
                // Launch the photo picker and let the user choose only images.
                //fmang.FilesManager();
                pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            }
            else {
                Basic.msg("Error Permiso Denegado!");
            }
        }
        if (itemId == R.id.butt_pay1) {
            //Comprueba que la lista de cuentas no este vacia
            StartVar mVars = new StartVar(mContext);
            if (StartVar.listacc.size() < 2) {
                setMessage(3); //MSG lista de cuentas vacia
                return;
            }

            boolean result = true;
            int msgIdx = 0;
            String cltId = "0";
            boolean newClt = true;
            List<String> mList = new ArrayList<>();
            mList.add("payID"+mIndex);
            for(int i = 0; i < mInputList.size(); i++) {
                String text = mInputList.get(i).getText().toString().toLowerCase();
                text = Basic.inputProcessor(text); //Elimina caracteres que afectan a los csv
                //Para el input Nombre
                if(i == 0) {
                    text = Basic.nameProcessor(text);
                    for (int j = 0; j < listCliente.size(); j++) {
                        String name = listCliente.get(j).nombre;
                        if(name.toLowerCase().equals(text)){
                            //MSG Cliente Ya Existe
                            msgIdx = 1;
                            //setMessage(msgIdx);

                            mSpin1.setSelection(j+1); //Set Client

                            cltId = listCliente.get(j).cliente;
                            newClt = false;
                            break;
                        }
                    }
                }
                if (text.isEmpty()){
                    if(i == 0) {
                        //MSG para entrada de Nombre
                        msgIdx = 0;
                        setMessage(msgIdx);

                        result = false;
                        break;
                    }
                    if(i == 3) {
                        //MSG para entrada de Monto
                        msgIdx = 2;
                        setMessage(msgIdx);

                        result = false;
                        break;
                    }
                    else if (i == 4) {
                        //MSG para entrada de...
                        msgIdx = 3;
                        setMessage(msgIdx);

                        result = false;
                        break;
                    }
                }
                mList.add(text);
            }
            //Para Limpiar Todos Los inputs
            for (int i = 0; i < mInputList.size(); i++) {
                EditText mInput = mInputList.get(i);
                if(mInput.isEnabled()) {
                    mInput.setText("");
                    mInput.setSelection(0);
                    mInput.clearFocus();
                }
            }
            if (result) {
                //Inicia la fecha actual
                String currdate = "";
                String currtime = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    currtime = LocalTime.now().toString();
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    currdate = LocalDate.now().toString();
                }

                String monto = Basic.setValue(Double.toString(mInput4.getNumericValue()));

                if(monto.isEmpty() || Basic.parseFloat(monto) <= 0.0){
                    //MSG Para entrada de monto
                    msgIdx = 2;
                    setMessage(msgIdx);
                    return;
                }

                //Se guarda la foto en un nuevo directorio --------------------------------
                Bitmap bitmap = null;
                try {
                    if(!sImage.isEmpty() || currUri == null){
                        oldFile = Uri.parse(sImage);
                    }
                    else {
                        //Log.d("PhotoPicker", "Aqi hayyyyyyyyyyyyy5555----------------------------------: ");
                        bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), currUri);
                        sImage = mFileM.SavePhoto(bitmap, (mList.get(0)+StartVar.mCurrAcc), oldFile, mContext, mContext.getContentResolver());
                    }
                }
                catch (IOException e) {
                    Basic.msg("Error al guardar la IMAGEN!");
                    e.printStackTrace();
                    sImage = "";
                }
                //-------------------------------------------------------------------
               if(newClt){
                   cltId = ""+listCliente.size();
                   Cliente objClt = null;
                   objClt = new Cliente(
                           "cltID"+cltId, mList.get(1), mList.get(2),"0",
                           (swPorc?1:0), currdate, 0,0, currdate, 0,
                           "0", Basic.saveNewBit(StartVar.mCurrAcc)
                   );
                   appDBcliente.daoUser().insetUser(objClt);

                   Deuda objDeb = new Deuda(
                           "cltID"+cltId, Integer.toString(currtAcc), "0", 0, currdate,
                           0, 0, CalcCalendar.getDateMinus(currdate,1, currtTyp), 0,"0"
                           );
                   appDBdeuda.get(currtAcc).daoUser().insetUser(objDeb);
               }
               else {
                   //Actualiza la fecha del ultimo pago
                   StartVar.appDBcliente.daoUser().updateUltfech(cltId, currdate);

                   Deuda mDeb = appDBdeuda.get(currtAcc).daoUser().getUsers(cltId);
                   if (mDeb != null) {
                       int pagado = 1;
                       String debe = mDeb.debe;
                       String ulFech = mDeb.ulfech;
                       if (mDeb.estat == 1) {
                           int mult = CalcCalendar.getRangeMultiple(ulFech, currtTyp);
                           float alldeb = Basic.getDebt(mult, mDeb.total, mDeb.debe);
                           if (alldeb > 0) {
                               float debt = Basic.parseFloat(debe);
                               float currMnt = Basic.parseFloat(monto);
                               float total = Basic.parseFloat(mDeb.total);

                               currMnt += debt;

                               int i = 1;
                               //Basic.msg(total+" : "+currMnt);
                               for (; i <= mult; i++) {
                                   if (currMnt < total) {
                                       debe = Float.toString(currMnt);
                                       i--;
                                       break;
                                   } else {
                                       currMnt -= total;
                                       debt -= total;
                                   }
                                   //La deuda no fue pagada
                                   if (currMnt == 0) {
                                       debe = Float.toString(0);
                                       break;
                                   }
                               }
                               //Deuda fue saldada
                               if (i == mult) {
                                   debe = "0";
                                   pagado = 2;
                                   ulFech = currdate;
                               } else if (i < 0) {
                                   Basic.msg("El MONTO es mayor a la deuda!");
                                   debe = Float.toString(currMnt);
                                   pagado = 2;
                                   ulFech = currdate;
                                   return;
                               } else {
                                   ulFech = CalcCalendar.getDatePlus(mDeb.ulfech, i, currtTyp);
                                   if (CalcCalendar.getRangeMultiple(ulFech, currtTyp) < 0) {
                                       Basic.msg("El MONTO es mayor a la deuda!");
                                       mInput4.setText(Basic.getValueFormatter(Float.toString(debt)));
                                       return;
                                   }
                               }
                           } else {
                               Basic.msg("Cliente sin deudas!");
                               pagado = 2;
                               StartVar.appDBdeuda.get(currtAcc).daoUser().updateDebt(cltId, pagado, ulFech, debe);
                               return;
                           }
                           //   Basic.msg(ulFech+" : "+  CalcCalendar.getRangeMultiple(ulFech, 0)+" : "+i );
                       }
                       StartVar.appDBdeuda.get(currtAcc).daoUser().updateDebt(cltId, pagado, ulFech, debe);
                   }
                   else {
                       Deuda objDeb = new Deuda(
                               "cltID"+cltId, Integer.toString(currtAcc), "0", 0, currdate,
                               0, 0, CalcCalendar.getDateMinus(currdate,1, currtTyp), 0,"0"
                       );
                       appDBdeuda.get(currtAcc).daoUser().insetUser(objDeb);
                   }
               }
                Registro obj = new Registro(
                        mList.get(0), mList.get(1), mList.get(3), monto, currSel2, (swPorc?1:0),
                        sImage, currdate, currtime, (newClt?"cltID"+listCliente.size():cltId), Integer.toString(currtAcc), 0, "0"
                );
                appDBregistro.get(currtAcc).daoUser().insetUser(obj);

               //Actualisza la lista de fechas
               CalcCalendar.startCalList(mContext);

               //SE Limpia la lista
                mList.clear();
                //Recarga La lista de la DB ----------------------------
                mVars.getRegListDB();
                mVars.getCltListDB();
                mVars.getDebListDB();
                //-------------------------------------------------------

                //Esto inicia las actividad Main
                startActivity(new Intent(mContext, MainActivity.class));
                //finish(); //Finaliza la actividad y ya no se accede mas
            }
        }
        if (itemId == R.id.sw_pay1){
            swPorc = !swPorc;
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
            Basic.msg("Primero debe registrar una CUENTA!");
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
