package com.example.registro_cuentas;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {Cliente.class},
        version = 1
)
public abstract class AppDBclt extends RoomDatabase {
    public abstract DaoClt daoUser();
}
