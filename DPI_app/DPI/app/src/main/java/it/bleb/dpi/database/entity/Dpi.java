package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Dpi implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "modello")
    private String modello;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "tipo_dpi")
    private int tipoDpiId;

    @ColumnInfo(name = "beacon")
    private int beaconId;

    @Ignore
    public Dpi() {
    }

    public Dpi(int id, String modello, String note, int tipoDpiId, int beaconId) {
        this.id = id;
        this.modello = modello;
        this.note = note;
        this.tipoDpiId = tipoDpiId;
        this.beaconId = beaconId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModello() {
        return modello;
    }

    public void setModello(String modello) {
        this.modello = modello;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getTipoDpiId() {
        return tipoDpiId;
    }

    public void setTipoDpiId(int tipoDpiId) {
        this.tipoDpiId = tipoDpiId;
    }

    public int getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(int beaconId) {
        this.beaconId = beaconId;
    }
}
