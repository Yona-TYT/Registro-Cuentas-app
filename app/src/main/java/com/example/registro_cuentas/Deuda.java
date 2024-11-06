package com.example.registro_cuentas;

import io.reactivex.annotations.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Deuda {
    @PrimaryKey(autoGenerate = true)
    public long uid;
    public String deuda;
    public String accidx;
    public String total;
    public Integer porc;
    public String fecha;
    public Integer estat;
    public Integer pagado;
    public String ulfech;
    public Integer oper;
    public String debe;

    public Deuda(@NonNull String deuda, String accidx, String total, Integer porc, String fecha, Integer estat, Integer pagado, String ulfech, Integer oper, String debe)
    {
        this.deuda = deuda;
        this.accidx = accidx;
        this.total = total;
        this.porc = porc;
        this.fecha = fecha;
        this.estat = estat;
        this.pagado = pagado;
        this.ulfech = ulfech;
        this.oper = oper;
        this.debe = debe;
    }
}
