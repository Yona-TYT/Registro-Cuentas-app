package com.example.registro_cuentas;

import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Insert;
import java.util.List;

@Dao
public interface DaoClt {
    @Query("SELECT * FROM cliente")
    List<Cliente> getUsers();

    @Query("SELECT * FROM cliente WHERE cliente= :user")
    Cliente getUsers(String user);

    // Se obtinen valores individuales de accselc, moneda, dolar------------------------------------
    @Query("SELECT nombre FROM cliente WHERE cliente= :user ")
    String getSaveName(String user);

    @Query("SELECT alias FROM cliente WHERE cliente= :user ")
    String getSaveAlias(String user);

    @Insert
    void insetUser(Cliente...clientes);

    @Query("UPDATE cliente SET nombre= :nombre, alias= :alias, total= :total, porc= :porc, fecha= :fecha WHERE cliente= :user")
    void updateUser(String user, String nombre, String alias, String total, Integer porc, String fecha);

    @Query("DELETE FROM cliente WHERE  cliente= :user")
    void removerUser(String user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertUser(Cliente user);
}

