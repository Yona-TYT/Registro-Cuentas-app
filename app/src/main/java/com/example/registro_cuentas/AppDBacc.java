package com.example.registro_cuentas;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {Cuenta.class},
        version = 1
)
public abstract class AppDBacc extends RoomDatabase {
    public abstract DaoAcc daoUser();
}
