package com.example.registro_cuentas.db.dao;

import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Insert;

import com.example.registro_cuentas.db.Deuda;

import java.util.List;

@Dao
public interface DaoDeb {
    @Query("SELECT * FROM deuda")
    List<Deuda> getUsers();

    @Query("SELECT * FROM deuda WHERE deuda= :user")
    Deuda getUsers(String user);

    // Recuperar una lista específica por grupoId
    @Query("SELECT * FROM Deuda WHERE cltid = :cltid ORDER BY uid ASC")
    List<Deuda> getListByGroupId(String cltid);

    // Se obtinen valores individuales de accselc, moneda, dolar------------------------------------

    @Insert
    void insetUser(Deuda...deudas);

    @Query("UPDATE deuda SET accid= :accid, cltid= :cltid, rent= :rent, porc= :porc, fecha= :fecha, estat= :estat, pagado= :pagado, ulfech= :ulfech, oper= :oper, paid= :paid WHERE deuda= :user")
    void updateUser(String user, String accid, String cltid, Float rent, Integer porc, String fecha, Integer estat, Integer pagado, String ulfech, Integer oper, Float paid);

    @Query("UPDATE deuda SET  pagado= :pagado, ulfech= :ulfech, paid= :paid WHERE deuda= :user")
    void updateDebt(String user, Integer pagado, String ulfech, Float paid);


    @Query("DELETE FROM deuda WHERE  deuda= :user")
    void removerUser(String user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertUser(Deuda user);
}

