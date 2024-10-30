package com.example.registro_cuentas;

import static com.example.registro_cuentas.StartVar.appDBcuenta;

import android.os.Bundle;

import com.example.registro_cuentas.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private StartVar startVar;

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
        startVar = new StartVar(getApplicationContext());
        startVar.setAccListDB();
        startVar.setCltListDB();
        startVar.setmPermiss(true);
        List<Cuenta> listCuenta = appDBcuenta.daoUser().getUsers();

        // Se agregan datos solo la primera vez en el primer elemento de la lista ---------------------------------------------
        if(listCuenta.isEmpty()) {
            Cuenta obj = new Cuenta(StartVar.saveDataName, "", "", "", 0, 0, 0, "");
            appDBcuenta.daoUser().insetUser(obj);
            //Recarga La lista de la DB ----------------------------
            startVar.getAccListDB();
            //-------------------------------------------------------
            //Actualiza la db para los registros
            startVar.setRegListDB();
            //--------------------------------
        }
        else if(listCuenta.size() > 1){
            startVar.setRegListDB();
            int idx = appDBcuenta.daoUser().getSaveCurrentAcc(StartVar.saveDataName);
            startVar.setCurrentAcc(idx);

            idx = appDBcuenta.daoUser().getSaveCurrency(StartVar.saveDataName);
            startVar.setCurrency(idx);

            String value = appDBcuenta.daoUser().getSaveDollar(StartVar.saveDataName);
            startVar.setDollar(value);
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

        startVar.setNavBar(navView);
    }

    public void setBottomNavigationVisibility(int visibility) {
        // get the reference of the bottomNavigationView and set the visibility.
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setVisibility(visibility);
    }

}