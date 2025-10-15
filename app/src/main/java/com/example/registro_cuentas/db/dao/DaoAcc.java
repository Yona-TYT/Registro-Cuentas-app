package com.example.registro_cuentas.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.registro_cuentas.db.Cuenta;

import java.util.List;

@Dao
public interface DaoAcc {

    // Nuevo: @Insert simple con parámetro directo (para inferencia)
    @Insert
    void insert(Cuenta cuenta);  // Room infiere Deuda del parámetro

    // Nuevo: @Query simple para List (si no lo tienes ya)
    @Query("SELECT * FROM cuenta")
    List<Cuenta> getAllCuentas();  // Renombra si quieres evitar conflicto con getUsers

    //-------------------------------------------------------------------------------------


    @Query("SELECT * FROM cuenta")
    List<Cuenta> getUsers();

    @Query("SELECT * FROM cuenta WHERE cuenta= :user")
    Cuenta getUsers(String user);

    //----------------------------------------------------------------------------------------------

    @Insert
    void insetUser(Cuenta...cuentas);

    @Query("UPDATE cuenta SET nombre= :nombre, desc= :desc, monto= :monto, acctipo= :acctipo, fecselc = :fecselc, moneda= :moneda, dolar= :dolar WHERE cuenta= :user")
    void updateUser(String user, String nombre, String desc, String monto, Integer acctipo, Integer fecselc, Integer moneda, String dolar );

    @Query("UPDATE cuenta SET nombre= :nombre, desc= :desc, monto= :monto, acctipo= :acctipo WHERE cuenta= :user")
    void updateAccount(String user, String nombre, String desc, String monto, Integer acctipo);

    @Query("UPDATE cuenta SET fecselc= :fecselc,  accselc= :accselc, moneda= :moneda, dolar= :dolar, ultfec= :ultfec WHERE cuenta= :user")
    void updateData(String user, Integer fecselc, Integer accselc, Integer moneda, String dolar, String ultfec);

    //----------------------------------------------------------------------------------------------

    @Query("DELETE FROM cuenta WHERE  cuenta= :user")
    void removerUser(String user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertUser(Cuenta user);
}
