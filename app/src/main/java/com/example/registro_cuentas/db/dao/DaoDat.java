package com.example.registro_cuentas.db.dao;

import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Insert;

import com.example.registro_cuentas.db.Fecha;

import java.util.List;

@Dao
public interface DaoDat {
    @Query("SELECT * FROM fecha")
    List<Fecha> getUsers();

    @Query("SELECT * FROM fecha WHERE fecha= :user")
    Fecha getUsers(String user);

    // Se obtinen valores individuales de accselc, moneda, dolar------------------------------------
    @Query("SELECT mes FROM fecha WHERE fecha= :user ")
    String getSaveAlias(String user);

    @Insert
    void insetUser(Fecha...fechas);

    @Query("UPDATE fecha SET year= :year, mes= :mes, dia= :dia, hora= :hora, date= :date WHERE fecha= :user")
    void updateUser(String user, String year, String mes, String dia, String hora, String date);

    @Query("DELETE FROM fecha WHERE  fecha= :user")
    void removerUser(String user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertUser(Fecha user);
}

