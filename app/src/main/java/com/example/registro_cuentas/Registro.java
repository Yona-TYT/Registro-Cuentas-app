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
    public String more3;
    public String more4;
    public String more5;

    public Registro(@NonNull String registro, String nombre, String concep, String monto, Integer oper,
                    Integer porc, String imagen, String fecha, String time, String cltid, String more3,
                    String more4, String more5
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
            this.more3 = more3;
            this.more4 = more4;
            this.more5 = more5;
    }
}
