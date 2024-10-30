package com.example.registro_cuentas;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {Fecha.class},
        version = 1
)
public abstract class AppDBfec extends RoomDatabase {
    public abstract DaoDat daoUser();
}
