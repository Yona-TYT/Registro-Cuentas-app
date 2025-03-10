package com.example.registro_cuentas;

import android.content.Context;
import android.view.View;

import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class StartVar {

    //Nombre de data Base
    private static String nameDBacc = "Cuentas";
    private static String nameDBclt = "Clientes";
    private static String nameDBfec = "Fechas";

    //Todas las listas----------------------------------------------
    public static List<Cuenta> listacc =  new ArrayList<>();
    public static List<Cliente> listclt =  new ArrayList<>();
    public static List<Fecha> listfec =  new ArrayList<>();
    public static List<List> listreg = new ArrayList<>();
    public static List<List> listdeb = new ArrayList<>();
    // DB
    public static AppDBacc appDBcuenta;
    public static AppDBclt appDBcliente;
    public static AppDBfec appDBfecha;
    public static List<AppDBreg> appDBregistro =  new ArrayList<>();
    public static List<AppDBdeb> appDBdeuda =  new ArrayList<>();
    //-------------------------------------------------------------------

    // Var redundants
    public static boolean mPermiss = false;     //Permisos de gestion multimedia
    public static int mCurrAcc = 0;      // Cuenta seleccionada
    public static int mCurrTyp = 0;      // Cuenta seleccionada
    public static int mCurrency = 0;        //Moneda seleccionada
    public static int mCurrMes = 0;        //Mes seleccionado

    public static String mDollar = "";       //Precio del dolar


    public static ArrayList<String> textList;
    public static ArrayList<String> dirList;
    public static ArrayList<String> typeList;
    public static ArrayList<String> morlist = new ArrayList<>();


    public static int currSel4 = 0;
    public static int payIndex = 0;
    public static int cltIndex = 0;
    public static String cltBit = "0x0";


    public static String saveDataName = "savedataID0";
    public static String saveRegName = "reg";
    public static String saveDebName = "deb";

    //Barra de navegacion
    public static BottomNavigationView mNavBar;
    //Root View
    public static View mRootView;


    private Context mContex;
    public StartVar(Context mContex){
        this.mContex = mContex;
    }

    //------------------------------------------ Para guardar las cuentas
    public void setAccListDB(){
        //Instancia de la base de datos
        StartVar.appDBcuenta = Room.databaseBuilder( mContex, AppDBacc.class, nameDBacc).allowMainThreadQueries().build();
        StartVar.listacc =  appDBcuenta.daoUser().getUsers();
    }

    public void getAccListDB(){
        //Instancia de la base de datos
        StartVar.listacc =  StartVar.appDBcuenta.daoUser().getUsers();
    }
    //----------------------------------------------------------------------------------

    //------------------------------------------ Para guardar los clientes
    public void setCltListDB(){
        //Instancia de la base de datos
        StartVar.appDBcliente = Room.databaseBuilder( mContex, AppDBclt.class, nameDBclt).allowMainThreadQueries().build();
        StartVar.listclt =  appDBcliente.daoUser().getUsers();
    }

    public void getCltListDB(){
        //Instancia de la base de datos
        StartVar.listclt =  StartVar.appDBcliente.daoUser().getUsers();
    }
    //----------------------------------------------------------------------------------

    //---------------------------------------- Para Guardar los datos
    public void setRegListDB(){
        //Limpia las listas
        if(StartVar.appDBregistro != null){
            StartVar.appDBregistro.clear();
            StartVar.listreg.clear();
        }

        //Instancia de la base de datos
        int siz = StartVar.listacc.size();
        for (int i = 0; i < siz; i++){
            String name = StartVar.listacc.get(i).cuenta;
            AppDBreg db = Room.databaseBuilder( mContex, AppDBreg.class, saveRegName+name).allowMainThreadQueries().build();
            StartVar.appDBregistro.add(db);
            StartVar.listreg.add(db.daoUser().getUsers());
        }
    }
    //----------------------------------------------------------------------------------

    public void getRegListDB(){
        //Limpia las listas
        StartVar.appDBregistro.clear();
        StartVar.listreg.clear();

        //Instancia de la base de datos
        for (int i = 0; i < StartVar.appDBregistro.size(); i++){
            AppDBreg db = StartVar.appDBregistro.get(i);
            StartVar.listreg.add(db.daoUser().getUsers());
        }
    }
    //--------------------------------------------------------------------------------

    //---------------------------------------- Para Guardar info de deudas CLientes
    public void setDebListDB(){
        //Limpia las listas
        if(StartVar.appDBdeuda != null){
            StartVar.appDBdeuda.clear();
            StartVar.listdeb.clear();
        }

        //Instancia de la base de datos
        int siz = StartVar.listacc.size();
        for (int i = 0; i < siz; i++){
            String name = StartVar.listacc.get(i).cuenta;
            AppDBdeb db = Room.databaseBuilder( mContex, AppDBdeb.class, saveDebName+name).allowMainThreadQueries().build();
            StartVar.appDBdeuda.add(db);
            List<Deuda> list = db.daoUser().getUsers();
            StartVar.listdeb.add(list);
        }
    }
    //----------------------------------------------------------------------------------

    public void getDebListDB(){
        //Limpia las listas
        StartVar.appDBdeuda.clear();
        StartVar.listdeb.clear();

        //Instancia de la base de datos
        for (int i = 0; i < StartVar.appDBdeuda.size(); i++){
            AppDBdeb db = StartVar.appDBdeuda.get(i);
            StartVar.listdeb.add(db.daoUser().getUsers());
        }
    }
    //--------------------------------------------------------------------------------
    //------------------------------------------ Para guardar las Fechas
    public void setFecListDB(){
        //Instancia de la base de datos
        StartVar.appDBfecha = Room.databaseBuilder( mContex, AppDBfec.class, nameDBfec).allowMainThreadQueries().build();
        StartVar.listfec =  appDBfecha.daoUser().getUsers();
    }

    public void getFecListDB(){
        //Instancia de la base de datos
        StartVar.listfec =  StartVar.appDBfecha.daoUser().getUsers();
    }

    public void setmPermiss(boolean permiss){
        mPermiss = permiss;
    }
    public void setCurrentAcc(int idx){
        mCurrAcc = idx;
    }
    public void setCurrentTyp(int idx){
        mCurrTyp = idx;
    }
    public void setCurrency(int idx){
        mCurrency = idx;
    }
    public void setCurrentMes(int idx){
        mCurrMes = idx;
    }
    public void setCurrentClt(int idx){
        mCurrMes = idx;
    }

    public void setDollar(String idx){
        mDollar = idx;
    }
    public void setNavBar(BottomNavigationView view){mNavBar = view;}
    public void setRootView(View view){mRootView = view;}

    public void setArrayList(ArrayList<String> listA, ArrayList<String> listB, ArrayList<String> listC){
        StartVar.textList = listA;
        StartVar.dirList = listB;
        StartVar.typeList = listC;
    }

    public void setCurrSel4(int value){
        StartVar.currSel4 = value;
    }
    public void setPayIndex(int value){
        StartVar.payIndex = value;
    }
    public void setCltIndex(int value){
        StartVar.cltIndex = value;
    }
    public void setCltBit(String value){
        StartVar.cltBit = value;
    }

    public void setMorlist(ArrayList<String> list){
        StartVar.morlist.clear();
        StartVar.morlist = list;
    }
}