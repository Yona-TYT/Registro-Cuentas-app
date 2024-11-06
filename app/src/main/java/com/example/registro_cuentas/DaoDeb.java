package com.example.registro_cuentas;

import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Insert;
import java.util.List;

@Dao
public interface DaoDeb {
    @Query("SELECT * FROM deuda")
    List<Deuda> getUsers();

    @Query("SELECT * FROM deuda WHERE deuda= :user")
    Deuda getUsers(String user);

    // Se obtinen valores individuales de accselc, moneda, dolar------------------------------------

    @Insert
    void insetUser(Deuda...deudas);

    @Query("UPDATE deuda SET accidx= :accidx, total= :total, porc= :porc, fecha= :fecha, estat= :estat, pagado= :pagado, ulfech= :ulfech, oper= :oper, debe= :debe WHERE deuda= :user")
    void updateUser(String user, String accidx, String total, Integer porc, String fecha, Integer estat, Integer pagado, String ulfech, Integer oper, String debe);

    @Query("UPDATE deuda SET  pagado= :pagado, ulfech= :ulfech, debe= :debe WHERE deuda= :user")
    void updateDebt(String user, Integer pagado, String ulfech, String debe);


    @Query("DELETE FROM deuda WHERE  deuda= :user")
    void removerUser(String user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertUser(Deuda user);
}

