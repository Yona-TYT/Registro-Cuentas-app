package com.example.registro_cuentas.activitys;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import net.openid.appauth.AuthState;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.registro_cuentas.AppContextProvider;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.FilesManager;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.db.GenericQueue;
import com.example.registro_cuentas.drive.DriveManager;
import com.example.registro_cuentas.drive.SetWorkResult;
import com.example.registro_cuentas.ex.PreferenceHelper;

public class Preloader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preloder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/"+StartVar.dirAppName+"/");
        if(file.exists()){
            FilesManager.DeleteFile(file);
        }

        //Check valus before start main activity
        //Satrted variables
        StartVar startVar = new StartVar();
        startVar.setAllListDB();
        startVar.setmActivity(this);
        new Basic(AppContextProvider.getContext());

        StartVar.mLifecycle = ProcessLifecycleOwner.get();

        //Se reinicia el cursor para el gallery adapter
        PreferenceHelper.getInstance().setGalleryPosition(0, 0);

        //Se crea el directorio de .cowdate
        FilesManager.directoryCreate();

        //Satrted variables
        Basic mBasic = new Basic(getApplicationContext());

        // Inicializar la variable para las colas
        StartVar.genericQueue = new GenericQueue(StartVar.mLifecycle, AppContextProvider.getContext());

        DriveManager manager = new DriveManager(PreferenceHelper.getInstance());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        SetWorkResult mWorkResult = new SetWorkResult(StartVar.mLifecycle, executorService, manager);

        AuthState authState = new AuthState();
        authState = DriveManager.getAuthState();

        //En caso de estancar se forza el inicio de mainActivity
        startMainDelayErr(30000);

        if(authState.isAuthorized()) {
            if(!StartVar.mainStart) {
                Basic.msg("Sincronizando Datos...");
                manager.dataSynchronizeStarting();
                mWorkResult.observeWorkResult();
                return;
            }
        }
        else{
            StartVar.setmMainStart(true);
        }

        if(StartVar.mainStart) {
            startMainDelay(800);
        }
    }

    private void startMainDelay(int s){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Esto inicia las actividad Main despues de tiempo de espera del preloder
                startActivity(new Intent(Preloader.this, ReloadActivity.class));
                finish(); //Finaliza la actividad y ya no se accede mas
            }
        }, s);
    }

    private void startMainDelayErr(int s){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(StartVar.mActivity == null || StartVar.mActivity.getClass().getSimpleName().equals("Preloader")) {
                    //Esto inicia las actividad Main despues de tiempo de espera del preloder
                    startActivity(new Intent(Preloader.this, ReloadActivity.class));
                    Basic.msg("Algo fallo, Inicio forzado!");
                    finish(); //Finaliza la actividad y ya no se accede mas
                }
            }
        }, s);
    }
}