package com.example.registro_cuentas.db.dao;

import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Insert;

import com.example.registro_cuentas.db.Conf;

import java.util.List;

@Dao
public interface DaoCfg {
    @Query("SELECT * FROM Conf")
    List<Conf> getUsers();

    @Query("SELECT * FROM Conf WHERE config= :user")
    Conf getUsers(String user);

    @Insert
    void insetUser(Conf...config);

    @Query("UPDATE Conf SET version = :version, hexid= :hexid, date= :date, time= :time, curr= :save1, dolar= :save2, moneda= :save3, mes= :save4 WHERE config= :user")
    void updateUser(String user, String version, String hexid, String date, String time, Integer save1, String save2, Integer save3, Integer save4);

    @Query("UPDATE Conf SET date= :date, time= :time WHERE config= :user")
    void updateDateTime(String user, String date, String time);

    @Query("UPDATE Conf SET curr= :save1, dolar= :save2, moneda= :save3, mes= :save4 WHERE config= :user")
    void updateSaves(String user, Integer save1, String save2, Integer save3, Integer save4);

    @Query("UPDATE Conf SET curr= :save1 WHERE config= :user")
    void updateCurrAcc(String user, Integer save1);
    @Query("UPDATE Conf SET curr= dolar= :save2 WHERE config= :user")
    void updateDolar(String user, String save2);
    @Query("UPDATE Conf SET moneda= :save3 WHERE config= :user")
    void updateMoneda(String user, Integer save3);
    @Query("UPDATE Conf SET mes= :save4 WHERE config= :user")
    void updateMes(String user, Integer save4);


    @Query("DELETE FROM Conf WHERE  config= :user")
    void removerUser(String user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertUser(Conf user);
}
