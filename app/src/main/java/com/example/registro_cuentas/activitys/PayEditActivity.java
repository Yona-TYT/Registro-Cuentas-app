package com.example.registro_cuentas.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.CurrencyEditText;
import com.example.registro_cuentas.FilesManager;
import com.example.registro_cuentas.Launcher;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.adapters.SelecAdapter;
import com.example.registro_cuentas.db.Cliente;
import com.example.registro_cuentas.db.dao.DaoPay;
import com.example.registro_cuentas.db.Pagos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class PayEditActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext = BaseContext.getContext();

    // DB
    private List<Pagos> appDBregistro = StartVar.appDBall.daoPay().getUsers();
    private List<Pagos> listRegistro = new ArrayList<>();
    private List<Cliente> listCliente = new ArrayList<>();

    private Pagos mpay;

    //Todos los Inputs
    private EditText mInput1;
    private CurrencyEditText mInput2;

    //Botones
    private Button mButt1;
    private Button mButt2;

    //Todos los Spinner
    private Spinner mSpin1;
    private int currSel1 = 0;
    private List<String> mSpinL1= Arrays.asList("Ingreso (+)", "Egreso (-)");
    private List<String> mCurrencyList= Arrays.asList("$", "Bs");
    //---------------------------------------------------------------------

    private Switch mSw;
    private boolean swPorc = false;

    private ImageButton mBtnImg1;
    private ImageView imageView1;
    private String currDir = "";

    // Classs para la gestion de archivos
    private FilesManager mFileM = new FilesManager();
    private String sImage = "";
    private Uri oldFile = null;
    private Uri currUri = null;

    private String mCurr = mCurrencyList.get(StartVar.mCurrency);

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pay_edit);

        //Se configura el Boton nav Back -----------------------------------------------
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                PayEditActivity.this.finish();
            }
        };
        onBackPressedDispatcher.addCallback(this, callback);
        //---------------------------------------------------------------------------------
        //Activate ToolBar ----------------------------------------------------------------
        Toolbar myToolbar = findViewById(R.id.toolbar_editpay);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Editar Pago");
        actionBar.setDisplayShowHomeEnabled(true);

        myToolbar.setTitleTextColor(ContextCompat.getColor(myToolbar.getContext(), R.color.inner_button));
        //------------------------------------------------------------------------------------------

        mSpin1 = findViewById(R.id.spin_payedit1);

        mSw = findViewById(R.id.sw_payedit1);

        mInput1 = findViewById(R.id.input_payedit1);
        mInput2 = findViewById(R.id.input_payedit2);

        mButt1 = findViewById(R.id.butt_payedit1);
        mBtnImg1 = findViewById(R.id.butt_payedit2);
        imageView1 = findViewById(R.id.image_payedit1);

        mpay = StartVar.appDBall.daoPay().getUsers(StartVar.currPayId);

        mInput1.setText(mpay.concep);
        currDir = mFileM.getImage(mpay.imagen, imageView1);

        mButt1.setOnClickListener(this);
        mSw.setOnClickListener(this);

        //Set Picker and Camera Launchers
        Launcher mLaunch = new Launcher(this.getActivityResultRegistry(), this.getApplicationContext(), new Launcher.OnCapture() {
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

        //Efecto moneda
        //-------------------------------------------------------------------------------------------------------
        mInput2.setCurrencySymbol(mCurr, true);
        mInput2.setText(Basic.getConverteValue(mpay.monto).toString());

        //----------------------------------------------------------------------------------------------------

        //--------------------------------------------------------------------------------------------
        //Para la lista del selector Tipo Operacion ----------------------------------------------------------------------------------------------
        SelecAdapter adapt2 = new SelecAdapter(mContext, mSpinL1);
        mSpin1.setAdapter(adapt2);
        mSpin1.setSelection(mpay.oper); //Set default ingreso

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    // MenuToolbar boton back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.sw_payedit1){
            swPorc = !swPorc;
        }
        if (itemId == R.id.butt_payedit1) {
            String concep = mInput1.getText().toString();
            concep = Basic.inputProcessor(concep); //Elimina caracteres que afectan a los csv

            double rent = mInput2.getNumericValue();
            if(rent <= 0.0){
                //MSG Para entrada de monto
                Basic.msg("Ingrese un MONTO Valido!.");
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
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currUri);
                    sImage = mFileM.SavePhoto(bitmap, mpay.pago +StartVar.accSelect, oldFile, mContext, this.getContentResolver());
                }
            }
            catch (IOException e) {
                sImage = "";
                Basic.msg("Error al guardar la IMAGEN!");
                e.printStackTrace();
            }
            //-------------------------------------------------------------------
            if(sImage.isEmpty()){
                sImage = currDir;
            }
            DaoPay mDao = StartVar.appDBall.daoPay();
            mDao.updatePay(mpay.pago, concep, rent, currSel1, (swPorc?1:0), sImage);

            //Esto inicia las actividad Reload
            startActivity(new Intent(mContext, ReloadActivity.class));
            this.finish(); //Finaliza la actividad y ya no se accede mas
        }
    }
    //------------------------------------------------------------

}
