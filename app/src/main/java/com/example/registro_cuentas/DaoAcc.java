package com.example.registro_cuentas;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoAcc {
    @Query("SELECT * FROM cuenta")
    List<Cuenta> getUsers();

    @Query("SELECT * FROM cuenta WHERE cuenta= :user")
    Cuenta getUsers(String user);

    // Se obtinen valores individuales de accselc, moneda, dolar------------------------------------
    @Query("SELECT accselc FROM cuenta WHERE cuenta= :user ")
    Integer getSaveCurrentAcc(String user);

    @Query("SELECT fecselc FROM cuenta WHERE cuenta= :user ")
    Integer getSaveCurrentFec(String user);

    @Query("SELECT moneda FROM cuenta WHERE cuenta= :user ")
    Integer getSaveCurrency(String user);

    @Query("SELECT dolar FROM cuenta WHERE cuenta= :user ")
    String getSaveDollar(String user);
    //----------------------------------------------------------------------------------------------

    @Insert
    void insetUser(Cuenta...cuentas);

    @Query("UPDATE cuenta SET nombre= :nombre, desc= :desc, monto= :monto, acctipo= :acctipo, fecselc = :fecselc, moneda= :moneda, dolar= :dolar WHERE cuenta= :user")
    void updateUser(String user, String nombre, String desc, String monto, Integer acctipo, Integer fecselc, Integer moneda, String dolar );

    @Query("UPDATE cuenta SET nombre= :nombre, desc= :desc, monto= :monto WHERE cuenta= :user")
    void updateAcc(String user, String nombre, String desc, String monto);

    @Query("UPDATE cuenta SET acctipo= :acctipo, moneda= :moneda, dolar= :dolar WHERE cuenta= :user")
    void updateData(String user, Integer acctipo, Integer moneda, String dolar );

    // Para actualizar valores individuales --------------------------------------------------------
    @Query("UPDATE cuenta SET accselc= :accselc WHERE cuenta= :user")
    void updateCurrentAcc(String user, Integer accselc );

    @Query("UPDATE cuenta SET fecselc= :fecselc WHERE cuenta= :user")
    void updateCurrentFec(String user, Integer fecselc );

    @Query("UPDATE cuenta SET moneda= :moneda WHERE cuenta= :user")
    void updateCurrency(String user, Integer moneda );

    @Query("UPDATE cuenta SET dolar= :dolar WHERE cuenta= :user")
    void updateDollar(String user, String dolar );
    //----------------------------------------------------------------------------------------------

    @Query("DELETE FROM cuenta WHERE  cuenta= :user")
    void removerUser(String user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertUser(Cuenta user);
}
