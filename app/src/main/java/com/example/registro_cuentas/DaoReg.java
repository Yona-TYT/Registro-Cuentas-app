package com.example.registro_cuentas;

import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Insert;
import java.util.List;

@Dao
public interface DaoReg {
    @Query("SELECT * FROM registro")
    List<Registro> getUsers();

    @Query("SELECT * FROM registro WHERE registro= :user")
    Registro getUsers(String user);

    @Insert
    void insetUser(Registro...registros);

    @Query("UPDATE registro SET nombre= :nombre, concep= :concep, monto= :monto, oper= :oper, porc= :porc, imagen= :imagen, fecha= :fecha, time= :time, cltid= :cltid, more3= :more3, more4= :more4, more5= :more5 WHERE registro= :user")
    void updateUser(String user, String nombre, String concep, String monto, Integer oper, Integer porc, String imagen, String fecha, String time , String cltid, String more3, String more4, String more5 );

    // Para actualizar valores individuales --------------------------------------------------------
    @Query("UPDATE registro SET concep= :concep, monto= :monto, oper= :oper, porc= :porc, imagen= :imagen  WHERE registro= :user")
    void updatePay(String user, String concep, String monto, Integer oper, Integer porc, String imagen);

    //----------------------------------------------------------------------------------------------

    @Query("DELETE FROM registro WHERE  registro= :user")
    void removerUser(String user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertUser(Registro user);
}

