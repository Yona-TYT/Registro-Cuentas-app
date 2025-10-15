package com.example.registro_cuentas.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.registro_cuentas.db.dao.DaoAcc;
import com.example.registro_cuentas.db.dao.DaoCfg;
import com.example.registro_cuentas.db.dao.DaoClt;
import com.example.registro_cuentas.db.dao.DaoDat;
import com.example.registro_cuentas.db.dao.DaoDeb;
import com.example.registro_cuentas.db.dao.DaoPay;
import com.example.registro_cuentas.db.dao.QueueItemDao;

@Database(
        entities = {Cuenta.class, Conf.class, Cliente.class, Deuda.class, Fecha.class, Pagos.class, QueueItem.class},
        version = 1,
        exportSchema = false  // Opcional: evita exportar el esquema en builds de debug
)
public abstract class AllDao extends RoomDatabase {
    public abstract DaoAcc daoAcc();

    public abstract DaoCfg daoCfg();

    public abstract DaoClt daoClt();

    public abstract DaoDeb daoDeb();

    public abstract DaoDat daoDat();

    public abstract DaoPay daoPay();

    public abstract QueueItemDao daoQueue();

}