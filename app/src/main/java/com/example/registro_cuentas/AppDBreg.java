package com.example.registro_cuentas;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {Registro.class},
        version = 1
)
public abstract class AppDBreg extends RoomDatabase {
    public abstract DaoReg daoUser();
}
