package com.example.registro_cuentas;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.room.Room;

import com.example.registro_cuentas.db.AllDao;
import com.example.registro_cuentas.db.Cliente;
import com.example.registro_cuentas.db.Conf;
import com.example.registro_cuentas.db.Cuenta;
import com.example.registro_cuentas.db.Deuda;
import com.example.registro_cuentas.db.Fecha;
import com.example.registro_cuentas.db.Pagos;
import com.example.registro_cuentas.db.UsuarioQueue;
import com.example.registro_cuentas.drive.SetWorkResult;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Base64;

public class StartVar {

    //Mapa de arrays
    public static HashMap<String, ArrayList<String>> arrayMap = new HashMap<>();
    public static final String mId1 = "id1";
    public static final String mId2 = "id2";

    //Nombre de data Base
    private static String nameDBacc = "Cuentas";
    private static String nameDBclt = "Clientes";
    private static String nameDBfec = "Fechas";
    public static String nameDBconf = "Config-RG";

    //Worker tags
    public static final String WORK_TAG_DOWNLOAD = "DownloadWorkConfigDb"; // Define WORK_TAG para configdb
    //public static final String WORK_TAG_UPLOAD = "UploadWorkCowData"; // Define WORK_TAG para cowdatadb

    public static List<String[]> csvList = new ArrayList<>();

    //Todas las listas----------------------------------------------
    public static List<Cuenta> listacc =  new ArrayList<>();
    public static List<Cliente> listclt =  new ArrayList<>();
    public static List<Fecha> listfec =  new ArrayList<>();
    public static List<Pagos> listreg = new ArrayList<>();
    public static List<Deuda> listdeb = new ArrayList<>();
    // DB
    public static AllDao appDBall;

    //-------------------------------------------------------------------

    // DB Config
    public static Conf mConfigDB;
    public static String mConfID = "confID0";

    // Var redundants
    public static boolean mPermiss = false;     //Permisos de gestion multimedia
    public static int accSelect = 0;      // Cuenta seleccionada
    public static int accCierre = 0;      // Cuenta seleccionada
    public static int mCurrency = 0;        //Moneda seleccionada
    public static int mCurrMes = 0;        //Mes seleccionado

    public static Double mDollar = 0d;       //Precio del dolar

    public static ArrayList<String> textList;
    public static ArrayList<String> dirList;
    public static ArrayList<String> typeList;
    public static ArrayList<String> morlist = new ArrayList<>();

    public static List<Object[]> bitList = new ArrayList<>();

    public static int currSel4 = 0;
    public static String currPayId = "";
    public static int cltIndex = 0;
    public static String cltBit = "0x0";


    public static String saveRegName = "reg";
    public static String saveDebName = "deb";

    public static final String dirAppName = "/.accdata/";

    //Root View
    public static View mRootView;

    //Preloder
    public static boolean mainStart = false;
    //Hacer upload cuando los datos esten disponibles.
    public static boolean makeUpdate = false;

    public static Context mContex;
    public static Activity mActivity;
    public static Activity reloadActivity;
    public static UsuarioQueue usuarioQueue;
    public static int sendDate = 0;

    public static SetWorkResult mWorkResult = null;

    public StartVar(Context mContex){
        this.mContex = mContex;
    }

    public static void getConfigDB(){
        //Instancia de la base de datos
        StartVar.mConfigDB =  StartVar.appDBall.daoCfg().getUsers(mConfID);
    }

    //------------------------------------------ Para guardar las cuentas
    public void setAllListDB(){
        //Instancia de la base de datos
        StartVar.appDBall = Room.databaseBuilder( mContex, AllDao.class, nameDBacc).allowMainThreadQueries().build();

        StartVar.listacc = appDBall.daoAcc().getUsers();
        StartVar.listclt = appDBall.daoClt().getUsers();
        StartVar.listdeb = appDBall.daoDeb().getUsers();
        StartVar.listfec = appDBall.daoDat().getUsers();
        StartVar.listreg = appDBall.daoPay().getUsers();

        //Instancia de la base de datos para Config
        mConfigDB = StartVar.appDBall.daoCfg().getUsers(mConfID);

        if(mConfigDB == null){
            String date = "";
            String time= "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                date = LocalDate.now().toString();
                time = LocalTime.now().toString();
            }

            // Generar UUID
            UUID uuid = UUID.randomUUID();
            // Convertir UUID a bytes (16 bytes)
            ByteBuffer byteBuffer = ByteBuffer.allocate(16);
            byteBuffer.putLong(uuid.getMostSignificantBits());
            byteBuffer.putLong(uuid.getLeastSignificantBits());

            // Codificar en Base64 (sin padding para ahorrar espacio)
            String textID = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                textID = Base64.getUrlEncoder().withoutPadding().encodeToString(byteBuffer.array());
            }

            //configDatabase.daoConf().insertUser();
            Conf obj = new Conf(mConfID, "2", textID, date, time, 0, 0d, 0, 0, 0);
            StartVar.appDBall.daoCfg().insertUser(obj);
        }


    }

    public void getAccListDB(){
        //Instancia para obtener Cuentas
        StartVar.listacc =  StartVar.appDBall.daoAcc().getUsers();
    }
    //----------------------------------------------------------------------------------

    public void getCltListDB(){
        //Instancia para obtener Clientes
        StartVar.listclt =  StartVar.appDBall.daoClt().getUsers();
    }
    //----------------------------------------------------------------------------------


    public void getFecListDB(){
        //Instancia para obtener Fechas
        StartVar.listfec =  StartVar.appDBall.daoDat().getUsers();
    }

    public void setmPermiss(boolean permiss){
        mPermiss = permiss;
    }
    public void setCurrentAcc(int idx){
        accSelect = idx;
    }
    public void setCurrentTyp(int idx){
        accCierre = idx;}
    public void setCurrency(int idx){
        mCurrency = idx;
    }
    public void setCurrentMes(int idx){
        mCurrMes = idx;
    }
    public void setCurrentClt(int idx){
        mCurrMes = idx;
    }

    public void setDollar(Double value){
        StartVar.mDollar = value;
    }
    public void setRootView(View view){mRootView = view;}

    public void setArrayList(ArrayList<String> listA, ArrayList<String> listB, ArrayList<String> listC){
        StartVar.textList = listA;
        StartVar.dirList = listB;
        StartVar.typeList = listC;
    }

    public void setCurrSel4(int value){
        StartVar.currSel4 = value;
    }
    public void setPayId(String value){
        StartVar.currPayId = value;
    }
    public void setCltIndex(int value){
        StartVar.cltIndex = value;
    }
    public static void setCltBit(String value){
        StartVar.cltBit = value;
    }

    public void setMorlist(ArrayList<String> list){
        StartVar.morlist.clear();
        StartVar.morlist = list;
    }

    public static void setmMainStart(boolean mStart){mainStart = mStart;}

    public static void setCsvList(List<String[]> mList){
        StartVar.csvList.clear();
        StartVar.csvList = mList;
    }

}