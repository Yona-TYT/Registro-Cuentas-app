package com.example.registro_cuentas;

import static com.example.registro_cuentas.SatrtVar.appDBcuenta;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.registro_cuentas.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private SatrtVar satrtVar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseContext.initialise(this);
        //Satrted variables
        satrtVar = new SatrtVar(getApplicationContext());
        satrtVar.setAccListDB();
        satrtVar.setmPermiss(true);
        List<Cuenta> listCuenta = appDBcuenta.daoUser().getUsers();

        // Se agregan datos solo la primera vez en el primer elemento de la lista ---------------------------------------------
        if(listCuenta.isEmpty()) {
            Cuenta obj = new Cuenta(SatrtVar.saveDataName, "", "", "", 0, 0, 0, "");
            appDBcuenta.daoUser().insetUser(obj);
            //Recarga La lista de la DB ----------------------------
            satrtVar.getAccListDB();
            //-------------------------------------------------------
            //Actualiza la db para los registros
            satrtVar.setRegListDB();
            //--------------------------------
        }
        else if(listCuenta.size() > 1){
            satrtVar.setRegListDB();
            int idx = appDBcuenta.daoUser().getSaveCurrentAcc(SatrtVar.saveDataName);
            satrtVar.setCurrentAcc(idx);

            idx = appDBcuenta.daoUser().getSaveCurrency(SatrtVar.saveDataName);
            satrtVar.setCurrency(idx);

            String value = appDBcuenta.daoUser().getSaveDollar(SatrtVar.saveDataName);
            satrtVar.setDollar(value);
        }
        //----------------------------------------------------------------------------------------------------------------------

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_pay, R.id.navigation_acc)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

}