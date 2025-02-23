package com.example.registro_cuentas;

import static android.service.controls.ControlsProviderService.TAG;
import static com.example.registro_cuentas.StartVar.appDBcuenta;
import static com.example.registro_cuentas.StartVar.appDBfecha;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.registro_cuentas.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.annotations.NonNull;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static final int STORAGE_PERMISSION_CODE = 23;

    private List<Cuenta> listCuenta = new ArrayList<>();


    // Para guardar datos a exportar
    private List<String[]> totalList = new ArrayList<>();

    //Check for permission---------------------------------
    private FilesManager mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Se configura el Boton nav Back -----------------------------------------------
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        };
        onBackPressedDispatcher.addCallback(MainActivity.this, callback);
        //---------------------------------------------------------------------------------

        BaseContext.initialise(this);
        //Satrted variables
        StartVar startVar = new StartVar(getApplicationContext());
        Basic mBasic = new Basic(getApplicationContext());

        //Start File manager class
        mFile = new FilesManager(this);

        CalcCalendar calen = new CalcCalendar();

        if(!StartVar.mPermiss) {
            if (checkStoragePermissions()) {
                startVar.setmPermiss(true);
            }
            else {
                requestForStoragePermissions();
                startVar.setmPermiss(checkStoragePermissions());
            }
        }

        //-------------------------------------------------------
        startVar.setAccListDB();
        startVar.setCltListDB();
        startVar.setFecListDB();
        startVar.setmPermiss(true);

        // Se agregan datos solo la primera vez a para las fechas ---------------------------------------------
        List<Fecha> listFecha = appDBfecha.daoUser().getUsers();
        if(listFecha.isEmpty()) {
            Fecha obj;
            //Inicia la fecha actual
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
               LocalDate currdate = LocalDate.now();
               LocalTime currtime = LocalTime.now();
               obj = new Fecha("dateID0", ""+currdate.getYear(), currdate.getMonth().toString(), ""+currdate.getDayOfMonth(), CalcCalendar.getTime(currtime.toString()), currdate.toString());
            }
            else {
                obj = new Fecha("dateID0", "0", "0", "0", "0", "0");
            }
            appDBfecha.daoUser().insetUser(obj);
            //-------------------------------------------------------
        }
        //Recarga La lista de la DB ----------------------------
        startVar.getFecListDB();
        //----------------------------------------------------------------------------------------------------------------------

        // Se agregan datos solo la primera vez en el primer elemento de la lista ---------------------------------------------
        listCuenta = appDBcuenta.daoUser().getUsers();
        if(listCuenta.isEmpty()) {
            //Inicia la fecha actual
            String currdate = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                currdate = LocalDate.now().toString();
            }
            Cuenta obj = new Cuenta(StartVar.saveDataName, "0", "0", "0", 0, 0, 0,0, "", currdate);
            appDBcuenta.daoUser().insetUser(obj);
            //Recarga La lista de la DB ----------------------------
            startVar.getAccListDB();
            //-------------------------------------------------------
            //Actualiza la db para los registros y Deuda
            startVar.setRegListDB();
            startVar.setDebListDB();
            //--------------------------------
        }
        else {
            //Actualiza la db para los registros y Deuda
            startVar.setRegListDB();
            startVar.setDebListDB();
            //--------------------------------
            int idx = 0;
            if(listCuenta.size() > 1) {
                DaoAcc mDao = appDBcuenta.daoUser();
                idx = mDao.getSaveCurrentAcc(StartVar.saveDataName);
                startVar.setCurrentTyp(mDao.getUsers().get(idx).acctipo);
                startVar.setCurrentAcc(idx);

                idx = appDBcuenta.daoUser().getSaveCurrency(StartVar.saveDataName);
                startVar.setCurrency(idx);

                String value = appDBcuenta.daoUser().getSaveDollar(StartVar.saveDataName);
                startVar.setDollar(value);
            }
            idx = appDBcuenta.daoUser().getSaveCurrentFec(StartVar.saveDataName);
            startVar.setCurrentMes(idx);
        }
        //----------------------------------------------------------------------------------------------------------------------

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_pay, R.id.navigation_acc, R.id.navigation_clt)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        View rootView = this.findViewById(android.R.id.content);
        startVar.setRootView(rootView);
        startVar.setNavBar(navView);

    }

    public void setBottomNavigationVisibility(int visibility) {
        // get the reference of the bottomNavigationView and set the visibility.
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setVisibility(visibility);
    }
    //Para cargar los menus en toolbar
    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.save, menu);
        getMenuInflater().inflate(R.menu.impor, menu);

        for(int i = 0; i < menu.size(); i++){
            MenuItem item = menu.getItem(i);
//            Drawable drawable = item.getIcon();

//            if(drawable != null) {
////                drawable.mutate();
////                drawable.setColorFilter(ContextCompat.getColor(this, R.color.inner_button), PorterDuff.Mode.SRC_ATOP);
//            }

            SpannableString spannabl = new SpannableString(item.getTitle().toString());
            spannabl.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)),0 ,spannabl.length(),0);
            item.setTitle(spannabl);
        }
        //test.setBackgroundColor(ContextCompat.getColor(test.getContext(), R.color.purple_500));


        return true;
    }
    // Accion listern para los menus toolbar
    @SuppressLint("SetWorldReadable")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int itemId = item.getItemId();

        //Para Exportar archivo CSV
        if (itemId == R.id.save) {
            //Se agrega un indicador numerico para identificar nuevas versiones del save.csv
            totalList.add(new String[]{"1"});
            totalList.add(new String[]{"<0>"}); // Etiqueta de inicio Cuenta
            for(int i = 0; i < listCuenta.size(); i++) {
                Cuenta arr = listCuenta.get(i);
                //------------------------------------------------------
                // Se crea la lista para esportar a csv  ---------------
                String[] txList= new String[10];
                txList[0]=arr.cuenta;
                txList[1]=arr.nombre;
                txList[2]=arr.desc;
                txList[3]=arr.monto;
                txList[4]=arr.acctipo.toString();
                txList[5]=arr.fecselc.toString();
                txList[6]=arr.accselc.toString();
                txList[7]=arr.moneda.toString();
                txList[8]=arr.dolar;
                txList[9]="2024-11-01";

                totalList.add(txList);
                //--------------------------------------------------------
            }

            totalList.add(new String[]{"<1>"}); // Etiqueta de inicio Registro
            //Instancia de la base de datos
            for (int i = 0; i < StartVar.appDBregistro.size(); i++) {
                List<Registro> list = StartVar.appDBregistro.get(i).daoUser().getUsers();
                for (int j = 0; j < list.size(); j++) {
                    Registro arr = list.get(j);

                    //------------------------------------------------------
                    // Se crea la lista para exportar a csv  ---------------
                    String[] txList= new String[13];

                    txList[0]=arr.registro;
                    txList[1]=arr.nombre;
                    txList[2]=arr.concep;
                    txList[3]=arr.monto;
                    txList[4]=arr.oper.toString();
                    txList[5]=arr.porc.toString();
                    txList[6]=arr.imagen;
                    txList[7]=arr.fecha;
                    txList[8]=arr.time;
                    txList[9]=arr.cltid;
                    txList[10]=arr.accid;
                    txList[11]=arr.more4.toString();
                    txList[12]=arr.more5;

                    totalList.add(txList);
                    //--------------------------------------------------------
                }
            }

            totalList.add(new String[]{"<2>"}); // Etiqueta de inicio Deuda
            //Instancia de la base de datos
            for (int i = 0; i < StartVar.appDBdeuda.size(); i++) {
                List<Deuda> list = StartVar.appDBdeuda.get(i).daoUser().getUsers();
                for (int j = 0; j < list.size(); j++) {
                    Deuda arr = list.get(j);

                    //------------------------------------------------------
                    // Se crea la lista para esportar a csv  ---------------
                    String[] txList= new String[10];

                    txList[0]=arr.deuda;
                    txList[1]=arr.accidx;
                    txList[2]=arr.total;
                    txList[3]=arr.porc.toString();
                    txList[4]=arr.fecha;
                    txList[5]=arr.estat.toString();
                    txList[6]=arr.pagado.toString();
                    txList[7]=arr.ulfech;
                    txList[8]=arr.oper.toString();
                    txList[9]=arr.debe;

                    totalList.add(txList);
                    //--------------------------------------------------------
                }
            }

            totalList.add(new String[]{"<3>"}); // Etiqueta de inicio Cliente
            List<Cliente> listCliente = StartVar.listclt;
            for(int i = 0; i < listCliente.size(); i++) {
                Cliente arr = listCliente.get(i);
                //------------------------------------------------------
                // Se crea la lista para exportar a csv  ---------------
                String[] txList= new String[12];

                txList[0]=arr.cliente;
                txList[1]=arr.nombre;
                txList[2]=arr.alias;
                txList[3]=arr.total;
                txList[4]=arr.porc.toString();
                txList[5]=arr.fecha;
                txList[6]=arr.estat.toString();
                txList[7]=arr.pagado.toString();
                txList[8]=arr.ulfech;
                txList[9]=arr.oper.toString();
                txList[10]=arr.debe == null? "0" : arr.debe;
                txList[11]=arr.bits;

                totalList.add(txList);
                //--------------------------------------------------------
            }

            totalList.add(new String[]{"<4>"}); // Etiqueta de inicio Fechas
            List<Fecha> listFecha = StartVar.listfec;
            for(int i = 0; i < listFecha.size(); i++) {
                Fecha arr = listFecha.get(i);
                //------------------------------------------------------
                // Se crea la lista para esportar a csv  ---------------
                String[] txList= new String[6];

                txList[0]=arr.fecha;
                txList[1]=arr.year;
                txList[2]=arr.mes;
                txList[3]=arr.dia;
                txList[4]=arr.hora;
                txList[5]=arr.date;

                totalList.add(txList);
                //--------------------------------------------------------
            }
            try {
                File file = mFile.csvExport(totalList);
                Log.d("PhotoPicker", " Aquiiiiiiiiii Hayyyyyy ------------------------: "+ StartVar.mPermiss);
                if(file != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setType("text/comma-separated-values");
                    // Se obtine la Uri , se debe modificar manidest con: android:authorities="com.example.cow_data.provider"
                    Uri fileUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", file);
                    // Log.d("PhotoPicker", " Aquiiiiiiiiii Hayyyyyy ------------------------: "+ fileUri.toString());

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // this will not work
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION); // this will not work
                    intent.putExtra(Intent.EXTRA_STREAM, fileUri);

                    startActivity(Intent.createChooser(intent, "Enviar datos para GUARDAR"));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Para Importar archivo CSV
        if (itemId == R.id.impor) {
            if (StartVar.mPermiss) {
                try {
                    String[] mimetype = {"text/csv", "text/comma-separated-values"};
                    mCsvRequest.launch(mimetype);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

    //Para importar archivos CSV
    private final ActivityResultLauncher<String[]> mCsvRequest = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    StartVar mImpVar = new StartVar(this);
                    // call this to persist permission across decice reboots
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    StringBuilder stringBuilder = new StringBuilder();
                    try (InputStream inputStream = getContentResolver().openInputStream(uri);
                         BufferedReader reader = new BufferedReader( new InputStreamReader(Objects.requireNonNull(inputStream)))) {
                        String line;
                        String version = "1";
                        int opt = 0;
                        String[] t = {"<0>", "<1>", "<2>", "<3>", "<4>"};
                        while ((line = reader.readLine()) != null) {
                            String[] spl = line.split(",");
                            //Log.d("PhotoPicker", " Aquiiiiiiiiii Hayyyyyy ------------------------: "+ line);
                            int f = spl.length;
                            for(int i = 0; i < spl.length; i++){
                                spl[i] = spl[i].replaceAll("\"", "");
                            }
                            if(f==1){
                                String tx = spl[0];
                                if(tx.equals(t[0])){
                                    t[0] = "";
                                    stringBuilder.append(line);
                                    continue;
                                }
                                else if(tx.equals(t[1])){
                                    t[1] = "";
                                    //Recarga La lista de la DB ----------------------------
                                    mImpVar.getAccListDB();
                                    opt = 1;
                                    stringBuilder.append(line);
                                    continue;
                                }
                                else if(tx.equals(t[2])){
                                    //Recarga La lista de la DB ----------------------------
                                    mImpVar.getRegListDB();
                                    Basic.msg(":- "+StartVar.listreg.size());
                                    t[2] = "";
                                    opt = 2;
                                    stringBuilder.append(line);
                                    continue;
                                }
                                else if(tx.equals(t[3])){
                                    //Recarga La lista de la DB ----------------------------
                                    mImpVar.getDebListDB();
                                    Basic.msg(":- "+StartVar.listdeb.size());
                                    t[3] = "";
                                    opt = 3;
                                    stringBuilder.append(line);
                                    continue;
                                }
                                else if(tx.equals(t[4])){
                                    t[4] = "";
                                    StartVar.appDBfecha.daoUser().removerUser("0");
                                    opt = 4;
                                    stringBuilder.append(line);
                                    continue;
                                }
                                stringBuilder.append(line);
                                continue;
                            }
                            //if(Objects.equals(version, "1")) {
                                if(opt==0) {
                                    String id = spl[0];
                                    if(id.equals(StartVar.saveDataName)){
                                        StartVar.appDBcuenta.daoUser().updateData(id, Integer.parseInt(spl[5]), Integer.parseInt(spl[6]), Integer.parseInt(spl[7]), spl[8], spl[9]);
                                    }
                                    else {
                                        Cuenta obj = new Cuenta(
                                                id, spl[1], spl[2], spl[3], Integer.parseInt(spl[4]), 0, 0, 0, "0","0"
                                        );
                                        StartVar.appDBcuenta.daoUser().insetUser(obj);
                                    }
                                }
                                else if(opt==1){
                                    String idx = spl[10];
                                    String name = StartVar.listacc.get(Integer.parseInt(idx)).cuenta;
                                    AppDBreg db = Room.databaseBuilder( this, AppDBreg.class, StartVar.saveRegName+name).allowMainThreadQueries().build();

                                    //Basic.msg(spl[10] +" "+name+" "+spl[1]);
                                    Registro obj = new Registro(
                                            spl[0], spl[1], spl[2], spl[3], Integer.parseInt(spl[4]), Integer.parseInt(spl[5]),
                                            spl[6], spl[7], spl[8], spl[9], spl[10], Integer.parseInt(spl[11]), spl[12]
                                    );
                                    db.daoUser().insetUser(obj);
                                    StartVar.appDBregistro.add(db);
                                }

                                else if(opt==2){
                                    String idx = spl[1];
                                    String name = StartVar.listacc.get(Integer.parseInt(idx)).cuenta;
                                    AppDBdeb db = Room.databaseBuilder( this, AppDBdeb.class, StartVar.saveDebName+name).allowMainThreadQueries().build();

                                    Deuda obj = new Deuda(
                                            spl[0], spl[1], spl[2], Integer.parseInt(spl[3]), spl[4], Integer.parseInt(spl[5]),
                                            Integer.parseInt(spl[6]), spl[7], Integer.parseInt(spl[8]), spl[9]
                                    );
                                    db.daoUser().insetUser(obj);
                                    StartVar.appDBdeuda.add(db);
                                }

                                else if(opt==3){
                                    Cliente obj = new Cliente(
                                            spl[0], spl[1], spl[2], spl[3], Integer.parseInt(spl[4]), spl[5], Integer.parseInt(spl[6]),
                                            Integer.parseInt(spl[7]), spl[8], Integer.parseInt(spl[9]), spl[10], spl[11]
                                    );
                                    StartVar.appDBcliente.daoUser().insetUser(obj);
                                }
                                else {
                                    String id = spl[0];
                                    if(id.equals("dateID0")) {
                                        StartVar.appDBfecha.daoUser().updateUser(spl[0], spl[1], spl[2], spl[3], spl[4], spl[5]);
                                    }
                                    else {
                                        Fecha obj = new Fecha(
                                                spl[0], spl[1], spl[2], spl[3], spl[4], spl[5]
                                        );
                                        StartVar.appDBfecha.daoUser().insetUser(obj);
                                    }
                                }
                           // }
                            stringBuilder.append(line);
                        }
                        mImpVar.getFecListDB();

                        Intent mIntent = new Intent(this, MainActivity.class);
                        startActivity(mIntent);
                        this.finish();
                    }
                    catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    Basic.msg("Solicitud Denegada!");
                }
            }
    );


    private boolean checkStoragePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            //Android is 11 (R) or above
            else if (Environment.isExternalStorageManager()){
                  Log.d("PhotoPicker", " Permiso Aquiiiiiiiiii Hayyyyyy 11100------------------------: " );
                return true;
            }
            else {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityIfNeeded(intent, 101);
                    return true;
                }
                catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    startActivityIfNeeded(intent, 101);
                    return true;
                }
            }
        }
        else {
            Log.d("PhotoPicker", " -----Permiso Aquiiiiiiiiii Hayyyyyy 11100------------------------: " );

            //Below android 11
            int write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);

            return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
        }
    }

    private ActivityResultLauncher<Intent> storageActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>(){
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                                //Android is 11 (R) or above
                                if(Environment.isExternalStorageManager()) {
                                    //Manage External Storage Permissions Granted
                                    Log.d(TAG, "onActivityResult: Manage External Storage Permissions Granted");
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

    void requestForStoragePermissions() {
        //Android is 11 (R) or above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            }
            catch (Exception e){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        }
        else{
            //Below android 11
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    STORAGE_PERMISSION_CODE
            );
        }
    }

}