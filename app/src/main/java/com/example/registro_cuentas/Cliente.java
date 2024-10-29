package com.example.registro_cuentas;

import io.reactivex.annotations.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Cliente {
    @PrimaryKey(autoGenerate = true)
    public long uid;
    public String cliente;
    public String nombre;
    public String alias;
    public String total;
    public Integer porc;
    public String fecha;


    public Cliente(@NonNull String cliente, String nombre, String alias, String total, Integer porc, String fecha)
    {
            this.cliente = cliente;
            this.nombre = nombre;
            this.alias = alias;
            this.total = total;
            this.porc = porc;
            this.fecha = fecha;

    }
}
