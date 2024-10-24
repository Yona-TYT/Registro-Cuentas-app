package com.example.registro_cuentas;

import io.reactivex.annotations.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Cuenta {
    @PrimaryKey(autoGenerate = true)
    public long uid;
    public String cuenta;
    public String nombre;
    public String desc;
    public String monto;
    public Integer acctipo;
    public Integer accselc;
    public Integer moneda;
    public String dolar;


    public Cuenta(@NonNull String cuenta, String nombre, String desc, String monto, Integer acctipo, Integer accselc, Integer moneda, String dolar) {
            this.cuenta = cuenta;
            this.nombre = nombre;
            this.desc = desc;
            this.monto = monto;
            this.acctipo = acctipo;
            this.accselc = accselc;
            this.moneda = moneda;
            this.dolar = dolar;
    }
}
