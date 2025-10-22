package com.example.registro_cuentas.db.dao;

import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Insert;

import com.example.registro_cuentas.db.Pagos;

import java.util.List;

@Dao
public interface DaoPay extends GenericDao<Pagos>{
    @Query("SELECT * FROM Pagos")
    List<Pagos> getUsers();

    // Insertar una lista completa (grupoId ya está en cada objeto Pgos)
    @Insert
    void insertAll(List<Pagos> pagos);

    // Recuperar una lista específica por grupoId
    @Query("SELECT * FROM Pagos WHERE accid = :accid ORDER BY uid ASC")
    List<Pagos> getListByGroupId(String accid);  // Cambiado a String

    @Query("SELECT * FROM Pagos WHERE pago= :user")
    Pagos getUsers(String user);

    @Insert
    void insertUser(Pagos...registros);

    @Query("UPDATE Pagos SET nombre= :nombre, concep= :concep, monto= :monto, oper= :oper, porc= :porc, imagen= :imagen, fecha= :fecha, time= :time, cltid= :cltid, accid= :accid, more4= :more4, more5= :more5 WHERE pago= :user")
    void updateUser(String user, String nombre, String concep, Float monto, Integer oper, Integer porc, String imagen, String fecha, String time , String cltid, String accid, Integer more4, String more5 );

    // Para actualizar valores individuales --------------------------------------------------------
    @Query("UPDATE Pagos SET concep= :concep, monto= :monto, oper= :oper, porc= :porc, imagen= :imagen  WHERE pago= :user")
    void updatePay(String user, String concep, Float monto, Integer oper, Integer porc, String imagen);

    //----------------------------------------------------------------------------------------------

    @Query("DELETE FROM Pagos WHERE  pago= :user")
    void removerUser(String user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertUser(Pagos user);
}

