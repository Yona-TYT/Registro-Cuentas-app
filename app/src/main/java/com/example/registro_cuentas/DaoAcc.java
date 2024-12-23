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

    @Query("SELECT ultfec FROM cuenta WHERE cuenta= :user ")
    String getSaveDate(String user);
    //----------------------------------------------------------------------------------------------

    @Insert
    void insetUser(Cuenta...cuentas);

    @Query("UPDATE cuenta SET nombre= :nombre, desc= :desc, monto= :monto, acctipo= :acctipo, fecselc = :fecselc, moneda= :moneda, dolar= :dolar WHERE cuenta= :user")
    void updateUser(String user, String nombre, String desc, String monto, Integer acctipo, Integer fecselc, Integer moneda, String dolar );

    @Query("UPDATE cuenta SET nombre= :nombre, desc= :desc, monto= :monto, acctipo= :acctipo WHERE cuenta= :user")
    void updateAccount(String user, String nombre, String desc, String monto, Integer acctipo);

    @Query("UPDATE cuenta SET fecselc= :fecselc,  accselc= :accselc, moneda= :moneda, dolar= :dolar, ultfec= :ultfec WHERE cuenta= :user")
    void updateData(String user, Integer fecselc, Integer accselc, Integer moneda, String dolar, String ultfec);

    // Para actualizar valores individuales --------------------------------------------------------
    @Query("UPDATE cuenta SET accselc= :accselc WHERE cuenta= :user")
    void updateCurrentAcc(String user, Integer accselc );

    @Query("UPDATE cuenta SET fecselc= :fecselc WHERE cuenta= :user")
    void updateCurrentFec(String user, Integer fecselc );

    @Query("UPDATE cuenta SET moneda= :moneda WHERE cuenta= :user")
    void updateCurrency(String user, Integer moneda );

    @Query("UPDATE cuenta SET dolar= :dolar WHERE cuenta= :user")
    void updateDollar(String user, String dolar );

    @Query("UPDATE cuenta SET ultfec= :ultfec WHERE cuenta= :user")
    void updateDete(String user, String ultfec );
    //----------------------------------------------------------------------------------------------

    @Query("DELETE FROM cuenta WHERE  cuenta= :user")
    void removerUser(String user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertUser(Cuenta user);
}
