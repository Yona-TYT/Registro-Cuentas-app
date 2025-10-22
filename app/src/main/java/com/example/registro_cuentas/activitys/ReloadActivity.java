package com.example.registro_cuentas.activitys;

import static com.example.registro_cuentas.StartVar.appDBall;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registro_cuentas.BaseContext;
import com.example.registro_cuentas.Basic;
import com.example.registro_cuentas.CalcCalendar;
import com.example.registro_cuentas.DBListCreator;
import com.example.registro_cuentas.R;
import com.example.registro_cuentas.StartVar;
import com.example.registro_cuentas.db.Conf;
import com.example.registro_cuentas.db.Cuenta;
import com.example.registro_cuentas.db.Fecha;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pre);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BaseContext.initialise(this);
        //Satrted variables
        StartVar startVar = new StartVar(getApplicationContext());
        Basic mBasic = new Basic(getApplicationContext());

        startVar.setAllListDB();

        StartVar.reloadActivity = this;

        // Se agregan datos solo la primera vez a para las fechas ---------------------------------------------
        List<Fecha> listFecha = StartVar.listfec;
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
            appDBall.daoDat().insertUser(obj);
            //-------------------------------------------------------
        }
        //Recarga La lista de la DB ----------------------------
        startVar.getFecListDB();
        //----------------------------------------------------------------------------------------------------------------------

        // Se agregan datos solo la primera vez en el primer elemento de la lista ---------------------------------------------
        List<Cuenta> listCuenta = appDBall.daoAcc().getUsers();

        if(!listCuenta.isEmpty()) {
            Conf mCfg = StartVar.appDBall.daoCfg().getUsers(StartVar.mConfID);
            int idx = 0;
            idx = mCfg.curr;
            if(idx < listCuenta.size()) {
                startVar.setCurrentTyp(listCuenta.get(idx).acctipo);
                startVar.setCurrentAcc(idx);
                startVar.setCurrency(mCfg.moneda);
                startVar.setDollar(mCfg.dolar);
                startVar.setCurrentMes(mCfg.mes);
            }
        }
        //----------------------------------------------------------------------------------------------------------------------

        DBListCreator.createDbLists(); //Actualiza la lista para exportar csv

        //Esto inicia las actividad Main
        startActivity(new Intent(BaseContext.getContext(), MainActivity.class));
        finish(); //Finaliza la actividad y ya no se accede mas
    }
}