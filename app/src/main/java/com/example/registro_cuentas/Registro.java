package com.example.registro_cuentas;

import io.reactivex.annotations.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Registro {
    @PrimaryKey(autoGenerate = true)
    public long uid;
    public String registro;
    public String nombre;
    public String concep;
    public String monto;
    public Integer oper;
    public Integer porc;
    public String imagen;
    public String fecha;
    public String time;
    public String cltid;
    public String accid;
    public Integer estat;
    public String more5;

    public Registro(@NonNull String registro, String nombre, String concep, String monto, Integer oper,
                    Integer porc, String imagen, String fecha, String time, String cltid, String accid,
                    Integer estat, String more5
                    )
    {
            this.registro = registro;
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
            this.estat = estat;
            this.more5 = more5;
    }
}
