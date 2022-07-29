package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class TipoBeacon implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "is_beacon_dpi")
    private boolean beaconDPI;

    @Ignore
    public TipoBeacon() {
    }

    public TipoBeacon(int id, String nome, boolean beaconDPI) {
        this.id = id;
        this.nome = nome;
        this.beaconDPI = beaconDPI;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isBeaconDPI() {
        return beaconDPI;
    }

    public void setBeaconDPI(boolean beaconDPI) {
        this.beaconDPI = beaconDPI;
    }
}
