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
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.BitsOper;
import com.example.registro_cuentas.CalcCalendar;
import com.example.registro_cuentas.activitys.ReloadActivity;
import com.example.registro_cuentas.db.Cliente;
import com.example.registro_cuentas.CurrencyEditText;
import com.example.registro_cuentas.db.Cuenta;
import com.example.registro_cuentas.db.DatabaseUtils;
import com.example.registro_cuentas.db.dao.DaoAcc;
import com.example.registro_cuentas.db.dao.DaoClt;
import com.example.registro_cuentas.db.dao.DaoDeb;
import com.example.registro_cuentas.db.dao.DaoPay;
import com.example.registro_cuentas.db.Deuda;
import com.example.registro_cuentas.FilesManager;
import com.example.registro_cuentas.Launcher;
import com.example.registro_cuentas.activitys.MainActivity;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.adapters.SelecAdapter;
import com.example.registro_cuentas.databinding.FragmentAddpayBinding;
import com.example.registro_cuentas.db.Pagos;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddPayFragment extends Fragment implements View.OnClickListener {

    private FragmentAddpayBinding binding;

    private Context mContext = BaseContext.getContext();

    // DB
    private DaoAcc daoCuenta;
    private DaoPay daoPagos;
    private DaoDeb daoDeuda;
    private DaoClt daoCliente;

    private List<Cliente> listCliente = new ArrayList<>();

    private Cuenta mAcc;
    private Cliente mClt;

    private ConstraintLayout mConstrain;

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

    //Selectores
    private Switch mSw1;
    private boolean swTotal = false;
    private Switch mSw2;
    private boolean swPorc = false;

    private ImageButton mBtnImg1;
    private ImageView imageView1;

    private String mIndex = "";

    // Para guardar los permisos de app comprobados en main
    private boolean mPermiss = false;
    // Index de cuenta actual
    private int currtAcc = StartVar.accSelect;
    private int currtTyp = StartVar.accCierre;

    private String mCurr = mCurrencyList.get(StartVar.mCurrency);

    private FilesManager mFileM = new FilesManager();
    private String sImage = "";
    private Uri oldFile = null;
    private Uri currUri = null;

    private List<String> mCltList = new ArrayList<>();
    private List<String> mAliList = new ArrayList<>();
    private List<String> mIdList = new ArrayList<>();

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

        binding = FragmentAddpayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mConstrain = binding.constrainPay;

        mSpin1 = binding.spinPay1;
        mSpin2 = binding.spinPay2;

        mSw1 = binding.swPay1;
        mSw2 = binding.swPay2;

        mInput1 = binding.inputPay1;
        mInput2 = binding.inputPay2;
        mInput3 = binding.inputPay3;
        mInput4 = binding.inputPay4;

        mButt1 = binding.buttPay1;
        mBtnImg1 = binding.buttPay2;
        imageView1 = binding.imagePay1;

        mConstrain.setOnClickListener(this);
        mButt1.setOnClickListener(this);
        //mBtnImg1.setOnClickListener(this);
        mSw1.setOnClickListener(this);
        mSw2.setOnClickListener(this);

        //Set Picker and Camera Launchers
        Launcher mLaunch = new Launcher(getActivity().getActivityResultRegistry(), getActivity().getApplicationContext(), new Launcher.OnCapture() {
            @Override
            public void invoke(List<Uri> uris) {
                if (!uris.isEmpty()) {
                    Uri uri = uris.get(0);
                    try {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        if(imageView1 != null){
                            imageView1.setImageURI(uri);
                        }
                        currUri = uri;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Basic.msg("No hay imagen seleccionada!");
                }
            }
        });

        getLifecycle().addObserver(mLaunch);

        // Adjunta al botón para el picker
        mLaunch.attachToViewPicker(mBtnImg1, false, false);

        // Adjunta al botón para la camara
        mLaunch.attachToViewCam(mBtnImg1, true);

        mInputList.add(mInput1);
        mInputList.add(mInput2);
        mInputList.add(mInput3);
        //mInputList.add(mInput4);

        //Inicializa los dao
        daoCuenta = StartVar.appDBall.daoAcc();
        daoPagos = StartVar.appDBall.daoPay();
        daoCliente = StartVar.appDBall.daoClt();
        daoDeuda = StartVar.appDBall.daoDeb();

        mAcc = daoCuenta.getUsers().get(StartVar.accSelect);

        //Efecto moneda
        //-------------------------------------------------------------------------------------------------------
        mInput4.setCurrencySymbol(mCurr, true);
        mInput4.setText(Basic.setFormatter("0"));

        mPermiss = StartVar.mPermiss;
        if(daoPagos != null) {
            mIndex = DatabaseUtils.generateId("payID", daoPagos);
        }
        listCliente = daoCliente.getUsers();

        //Para la lista del selector Cliente ----------------------------------------------------------------------------------------------
        mCltList.add("Agregar");
        mAliList.add("");
        mIdList.add("");

        int pos = currtAcc;  // Posición global fija (mismo para todos clientes)
        int group = pos / 32;
        int offset = pos % 32;
        for (int i = 0; i < listCliente.size(); i++) {
            List<Integer> bitList = BitsOper.getBits(listCliente.get(i).bits);
            if (bitList.isEmpty()) {
                // Si no bits, asume unchecked (no agregar)
                continue;
            }
            // Padda con 0 si grupo no existe para este cliente
            while (group >= bitList.size()) {
                bitList.add(0);
            }
            int mByte = bitList.get(group);
            // Extracción del bit
            Cliente mClt = listCliente.get(i);
            if (BitsOper.bitR(mByte, offset) == 1) {

                Deuda mDeb = null;
                String accId = daoCuenta.getUsers().get(StartVar.accSelect).cuenta;
                for(Deuda mD : daoDeuda.getListByGroupId(mClt.cliente)){
                    if(mD.accid.equals(accId)){
                        mDeb = mD;
                        break;
                    }
                }
                if(mDeb != null && mDeb.estat == 1) {
                    swTotal = mDeb.estat == 1;
                    swPorc = mDeb.estat == 1;

                    mCltList.add(mClt.nombre);
                    mAliList.add(mClt.alias);
                    mIdList.add(mClt.cliente);
                }
            }
            // Debug opcional (usa Log.d en Android para evitar spam)
            // Log.d("FilterClientes", String.format("%s - grupo:%d offset:%d - bit:%b - pos:%d",
            //     listCliente.get(i).nombre, group, offset, BitsOper.bitR(mByte, offset) == 1, pos));
        }
        SelecAdapter adapt1 = new SelecAdapter(mContext, mCltList);
        mSpin1.setAdapter(adapt1);

        mSpin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currSel1 = i;
                mInput2.setHint("Alias (Opcional)");
                mClt = null;
                mSw1.setChecked(false);
                mSw2.setChecked(false);
                swPorc = false;
                swTotal = false;

                if(mAcc.acctipo == 0){
                    mSw1.setEnabled(false);
                }
                else {
                    mSw1.setEnabled(true);
                }

                if (i > 0) {

                    setEnabled(true);

                    mInput1.setText(mCltList.get(i).toUpperCase());
                    mInput1.setEnabled(false);

                    String alias = mAliList.get(i).toUpperCase();
                    mInput2.setText(alias);
                    mInput2.setEnabled(false);
                    if(alias.isEmpty()){
                        mInput2.setHint("Vacio");
                    }

                    mClt = daoCliente.getUsers(mIdList.get(i));

                    Cuenta mAcc = daoCuenta.getUsers().get(StartVar.accSelect);

                    String accId = mAcc.cuenta;

                    Deuda mDeb = daoDeuda.getUserByCltAndAcc(mClt.cliente, accId);

                    if(mDeb != null) {
                        if (mDeb.estat == 1) {
                            String ultFec = mDeb.ulfech;
                            if (ultFec.isEmpty()){
                                ultFec = mDeb.fecha;
                            }
                            mInput4.setText(Basic.getValueFormatter(String.valueOf(mDeb.rent)));
                            int mult = CalcCalendar.getRangeMultiple(ultFec, currtTyp);
                            Double monto = Basic.getDebt(mult, mDeb.rent, mDeb.remnant);

                            if (mAcc.acctipo > 0) {
                                if(mDeb.rent == 0){
                                    setEnabled(false);
                                    Basic.msg("Cliente sin RENTA");
                                }
                                else if (monto <= 0) {
                                    setEnabled(false);
                                    Basic.msg("Este cliente no Tiene DEUDAS!");
                                }
                            }
                        }
                        else {
                            setEnabled(false);
                            Basic.msg("Este cliente esta INACTIVO!");
                        }
                    }
                    else {
                        mInput4.setText(Basic.setFormatter("0"));
                    }
                }
                else{
                    setEnabled(true);

                    mInput1.setText("");
                    mInput2.setText("");
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

    private void setEnabled(boolean active){
        mInput1.setEnabled(active);
        mInput2.setEnabled(active);
        mInput3.setEnabled(active);
        mInput4.setEnabled(active);
        mButt1.setEnabled(active);
        mButt1.setEnabled(active);
        mBtnImg1.setEnabled(active);
        mSpin2.setEnabled(active);
        mSw2.setEnabled(active);
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.butt_pay1) {
            //Comprueba que la lista de cuentas no este vacia
            StartVar mVars = new StartVar(mContext);
            if (daoCuenta.getUsers().isEmpty()) {
                setMessage(3); //MSG lista de cuentas vacia
                return;
            }

            boolean result = true;
            int msgIdx = 0;
            String cltId = "0";
            boolean newClt = true;
            List<String> mList = new ArrayList<>();
            mList.add(mIndex);

            for(int i = 0; i < mInputList.size(); i++) {
                String text = mInputList.get(i).getText().toString().toLowerCase();
                text = Basic.inputProcessor(text); //Elimina caracteres que afectan a los csv
                //Para el input Nombre
                if(i == 0) {
                    text = Basic.nameProcessor(text);
                    for (int j = 1; j < mIdList.size(); j++) {
                        String name = daoCliente.getUsers(mIdList.get(j)).nombre;
                        if(name.toLowerCase().equals(text)){
                            //MSG Cliente Ya Existe
                            msgIdx = 1;
                            //setMessage(msgIdx);

                            mSpin1.setSelection(j); //Set Client

                            cltId = mIdList.get(j);
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

            if (result) {

                //Para Limpiar Todos Los inputs
                for (int i = 0; i < mInputList.size(); i++) {
                    EditText mInput = mInputList.get(i);
                    if(mInput.isEnabled()) {
                        mInput.setText("");
                        mInput.setSelection(0);
                        mInput.clearFocus();
                    }
                }

                String debId = DatabaseUtils.generateId("debID", daoDeuda);

                String accId = mAcc.cuenta;

                //Inicia la fecha actual
                String currdate = "";
                String currtime = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    currtime = LocalTime.now().toString();
                    currdate = LocalDate.now().toString();
                }

                Double rent = Basic.setValue(mInput4.getNumericValue());

                if(rent <= 0.0){
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
                        sImage = mFileM.SavePhoto(bitmap, (mList.get(0)+StartVar.accSelect), oldFile, mContext, mContext.getContentResolver());
                    }
                }
                catch (IOException e) {
                    Basic.msg("Error al guardar la IMAGEN!");
                    e.printStackTrace();
                    sImage = "";
                }
                //-------------------------------------------------------------------

               if(newClt){
                   for(Cliente mC : daoCliente.getUsers()){
                       if(mC.nombre.equals(mList.get(1).toLowerCase())){
                           mInput1.setText("");
                           Basic.msg("El Nombre Ya EXISTE!");
                           return;
                       }
                   }

                   cltId = DatabaseUtils.generateId("cltID", daoCliente);
                   Cliente objClt = null;
                   objClt = new Cliente(
                           cltId, mList.get(1), mList.get(2),"@null",
                           0, currdate, 0f, currdate, 0,
                           BitsOper.saveNewBit(StartVar.accSelect)
                   );
                   daoCliente.insertUser(objClt);

                   Deuda objDeb = new Deuda(
                           debId, accId, cltId, 0d, (swTotal?1:0), (swPorc?1:0), currdate,
                           1, 0, CalcCalendar.getCorrectDate(currdate, currtTyp), currSel2,0d,"@null"
                           );
                   daoDeuda.insertUser(objDeb);
               }
               else {
                   //Actualiza la fecha del ultimo pago
                   daoCliente.updateUltfech(cltId, currdate);

                   Deuda mDeb = daoDeuda.getUserByCltAndAcc(cltId, accId);

                   if(mDeb == null){
                       Deuda objDeb = new Deuda(
                               debId, accId, cltId, 0d, (swTotal?1:0), (swPorc?1:0), currdate,
                               0, 0, CalcCalendar.getCorrectDate(currdate, currtTyp), currSel2,0d, "@null"
                       );
                       daoDeuda.insertUser(objDeb);
                   }
                   else if (mAcc.acctipo > 0){
                       int pagado = 1;
                       Object[] mObj = CalcCalendar.dateToMoney(mDeb.ulfech, mAcc.acctipo, mDeb.rent, (mDeb.remnant + rent));
                       //Basic.msg("dateToMoney("+mDeb.ulfech +", "+ mAcc.acctipo+", "+mDeb.rent+", "+ (mDeb.paid + rent)+")", true);
                       if(mObj != null){
                           //Basic.msg(" ulfech "+mDeb.ulfech+" "+mObj[1]+" "+mObj[2]);
                           double maxRent = (double)mObj[0];    //Total a pagar (si es 0 = Cuenta saldada)
                           double remnant = (double)mObj[1];    //Remanente del pago (Se guarda para compensar futuros pagos)
                           String startDate = (String) mObj[2]; //La ultima fecha saldada

                           //Basic.msg("maxRent: "+maxRent+" startDate "+mDeb.ulfech+ " remnant "+remnant, true);

                           if (maxRent == 0 && remnant > 0) {
                                   Basic.msg("El MONTO es mayor a la deuda!");
                                   if(swTotal) {
                                       int mult = CalcCalendar.getRangeMultiple(mDeb.ulfech, mAcc.acctipo);
                                       mInput4.setText(Basic.getValueFormatter(String.valueOf( mDeb.rent * mult - mDeb.remnant)));
                                   }
                                   else {
                                       mInput4.setText(Basic.getValueFormatter(String.valueOf( mDeb.rent - mDeb.remnant)));
                                   }
                                   return;
                           }
                           else {
                               if(maxRent == 0){
                                   pagado = 2;
                               }
                               daoDeuda.updateFormPayWin(mDeb.deuda, (swTotal?1:0), (swPorc?1:0), pagado, startDate, currSel2, remnant);
                           }
                       }
                       else {
                             Basic.msg("Cliente sin deudas!");
                             pagado = 2;
                             daoDeuda.updateFormPayWin(mDeb.deuda, (swTotal?1:0), (swPorc?1:0), pagado, mDeb.ulfech, currSel2, mDeb.remnant);
                             return;
                       }
                   }
               }
                Pagos obj = new Pagos(
                        mList.get(0), mList.get(1), mList.get(3), rent, currSel2, (swPorc?1:0),
                        sImage, currdate, currtime, cltId, accId, 0, "0"
                );
                StartVar.appDBall.daoPay().insertUser(obj);

               //Actualisza la lista de fechas
               CalcCalendar.addCurrentMonthIfAbsent(mContext);

               //SE Limpia la lista
                mList.clear();

                //Esto inicia las actividad Reload
                startActivity(new Intent(mContext, ReloadActivity.class));
            }
        }
        if (itemId == R.id.sw_pay1){
            if(mClt == null){
                Basic.msg("Cliente no VALIDO");
                swTotal = false;
                mSw1.setChecked(false);
            }
            else{
                Deuda mDeb = daoDeuda.getUserByCltAndAcc(mClt.cliente, mAcc.cuenta);
                if(mDeb == null){
                    Basic.msg("Cliente no VALIDO");
                    swTotal = false;
                    mSw1.setChecked(false);
                }
                else {
                    swTotal = !swTotal;
                    if (swTotal) {
                        int mult = CalcCalendar.getRangeMultiple(mDeb.ulfech, mAcc.acctipo);
                        mInput4.setText(Basic.getValueFormatter(String.valueOf((Double) (mDeb.rent * mult))));
                    }
                    else{
                        mInput4.setText(Basic.getValueFormatter(String.valueOf(mDeb.rent)));
                    }
                }
            }
        }
        if (itemId == R.id.sw_pay2){
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
}
