package com.example.registro_cuentas.db;

import io.reactivex.annotations.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pagos {
    @PrimaryKey(autoGenerate = true)
    public long uid;
    public String pago;
    public String nombre;
    public String concep;
    public Float monto;
    public Integer oper;
    public Integer porc;
    public String imagen;
    public String fecha;
    public String time;
    public String cltid;
    public String accid;
    public Integer more4;
    public String more5;

    public Pagos(@NonNull String pago, String nombre, String concep, Float monto, Integer oper,
                 Integer porc, String imagen, String fecha, String time, String cltid, String accid,
                 Integer more4, String more5
                    )
    {
        this.pago = pago;
        this.nombre = nombre;
        this.concep = concep;
        this.monto = monto;
        this.oper = oper;
        this.porc = porc;
        this.imagen = imagen;
        this.fecha = fecha;
        this.time = time;
        this.cltid = cltid;
        this.accid = accid;
        this.more4 = more4;
        this.more5 = more5;
    }
}
