package com.example.registro_cuentas;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SatrtVar {

    //Nombre de data Base
    private static String nameDBacc = "Cuentas";
    private static String nameDBreg = "Registros";

    // Var redundants
    public static List<Cuenta> listacc =  new ArrayList<>();
    public static List<List> listreg = new ArrayList<>();
    public static boolean mPermiss;
    public static int mCurrenrAcc;
    public static int mCurrency;
    public static String mDollar;

    // DB
    public static AppDBacc appDBcuenta;
    public static List<AppDBreg> appDBregistro =  new ArrayList<>();
    public static ArrayList<String> textList;
    public static ArrayList<String> dirList;
    public static ArrayList<String> typeList;
    public static ArrayList<String> morlist = new ArrayList<>();


    public static int currSel2 = 0;
    public static int payIndex = 0;
    public static String saveDataName = "savedataID0";


    private Context mContex;
    public SatrtVar(Context mContex){
        this.mContex = mContex;
    }

    //------------------------------------------ Para guardar las cuentas
    public void setAccListDB(){
        //Instancia de la base de datos
        SatrtVar.appDBcuenta = Room.databaseBuilder( mContex, AppDBacc.class, nameDBacc).allowMainThreadQueries().build();
        SatrtVar.listacc =  appDBcuenta.daoUser().getUsers();
    }

    public void getAccListDB(){
        //Instancia de la base de datos
        SatrtVar.listacc =  SatrtVar.appDBcuenta.daoUser().getUsers();
    }
    //----------------------------------------------------------------------------------

    //---------------------------------------- Para Guardar los datos
    public void setRegListDB(){
        //Limpia las listas
        if(SatrtVar.appDBregistro != null){
            SatrtVar.appDBregistro.clear();
            SatrtVar.listreg.clear();
        }

        //Instancia de la base de datos
        int siz = SatrtVar.listacc.size();
        for (int i = 0; i < siz; i++){
            String name = SatrtVar.listacc.get(i).cuenta;
            AppDBreg db = Room.databaseBuilder( mContex, AppDBreg.class, name).allowMainThreadQueries().build();
            SatrtVar.appDBregistro.add(db);
            SatrtVar.listreg.add(db.daoUser().getUsers());
        }
    }

    public void getRegListDB(){
        //Limpia las listas
        SatrtVar.appDBregistro.clear();
        SatrtVar.listreg.clear();

        //Instancia de la base de datos
        for (int i = 0; i < SatrtVar.appDBregistro.size(); i++){
            AppDBreg db = SatrtVar.appDBregistro.get(i);
            SatrtVar.listreg.add(db.daoUser().getUsers());
        }
    }
    //--------------------------------------------------------------------------------

    public void setmPermiss(boolean permiss){
        mPermiss = permiss;
    }
    public void setCurrentAcc(int idx){
        mCurrenrAcc = idx;
    }
    public void setCurrency(int idx){
        mCurrency = idx;
    }
    public void setDollar(String idx){
        mDollar = idx;
    }

    public void setArrayList(ArrayList<String> listA, ArrayList<String> listB, ArrayList<String> listC){
        SatrtVar.textList = listA;
        SatrtVar.dirList = listB;
        SatrtVar.typeList = listC;
    }

    public void setCurrSel2(int value){
        SatrtVar.currSel2 = value;
    }
    public void setPayIndex(int value){
        SatrtVar.payIndex = value;
    }


    public void setMorlist(ArrayList<String> list){
        SatrtVar.morlist.clear();
        SatrtVar.morlist = list;
    }
}
