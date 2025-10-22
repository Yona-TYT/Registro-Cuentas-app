package com.example.registro_cuentas.db;

import io.reactivex.annotations.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Deuda {
    @PrimaryKey(autoGenerate = true)
    public long uid;
    public String deuda;
    public String accid;
    public String cltid;
    public Float rent;
    public Integer porc;
    public String fecha;
    public Integer estat;
    public Integer pagado;
    public String ulfech;
    public Integer oper;
    public Float paid;
    public String disabfec;

    public Deuda(@NonNull String deuda, String accid, String cltid, Float rent, Integer porc, String fecha, Integer estat, Integer pagado, String ulfech, Integer oper, Float paid, String disabfec)
    {
        this.deuda = deuda;
        this.accid = accid;
        this.cltid = cltid;
        this.rent = rent;
        this.porc = porc;
        this.fecha = fecha;
        this.estat = estat;
        this.pagado = pagado;
        this.ulfech = ulfech;
        this.oper = oper;
        this.paid = paid;
        this.disabfec = disabfec;
    }
}
