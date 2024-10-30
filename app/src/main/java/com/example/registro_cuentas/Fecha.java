package com.example.registro_cuentas;

import io.reactivex.annotations.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Fecha {
    @PrimaryKey(autoGenerate = true)
    public long uid;
    public String fecha;
    public String year;
    public String mes;
    public String dia;
    public String hora;

    public Fecha(@NonNull String fecha, String year, String mes, String dia, String hora )
    {
            this.fecha = fecha;
            this.year = year;
            this.mes = mes;
            this.dia = dia;
            this.hora = hora;
    }
}
