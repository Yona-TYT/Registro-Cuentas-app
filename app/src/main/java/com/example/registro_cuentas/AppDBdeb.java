package com.example.registro_cuentas;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {Deuda.class},
        version = 1
)
public abstract class AppDBdeb extends RoomDatabase {
    public abstract DaoDeb daoUser();
}
