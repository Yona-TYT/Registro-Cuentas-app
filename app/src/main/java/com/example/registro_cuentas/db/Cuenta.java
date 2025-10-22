package com.example.registro_cuentas.db;

import io.reactivex.annotations.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.registro_cuentas.db.dao.GenericDao;

import java.util.List;

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
    public Integer fecselc;
    public Integer moneda;
    public String dolar;
    public String ultfec;

    public Cuenta(@NonNull String cuenta, String nombre, String desc, String monto, Integer acctipo, Integer fecselc, Integer accselc, Integer moneda, String dolar, String ultfec) {
            this.cuenta = cuenta;
            this.nombre = nombre;
            this.desc = desc;
            this.monto = monto;
            this.acctipo = acctipo;
            this.fecselc = fecselc;
            this.accselc = accselc;
            this.moneda = moneda;
            this.dolar = dolar;
            this.ultfec = ultfec;
    }
}
