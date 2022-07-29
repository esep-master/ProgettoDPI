package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Beacon implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "seriale")
    private String seriale;

    @ColumnInfo(name = "livello_batteria")
    private int livelloBatteria;

    @ColumnInfo(name = "tipo_beacon")
    private int tipoBeaconId;

    @Ignore
    public Beacon() {
    }

    public Beacon(int id, String seriale, int livelloBatteria, int tipoBeaconId) {
        this.id = id;
        this.seriale = seriale;
        this.livelloBatteria = livelloBatteria;
        this.tipoBeaconId = tipoBeaconId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeriale() {
        return seriale;
    }

    public void setSeriale(String seriale) {
        this.seriale = seriale;
    }

    public int getLivelloBatteria() {
        return livelloBatteria;
    }

    public void setLivelloBatteria(int livelloBatteria) {
        this.livelloBatteria = livelloBatteria;
    }

    public int getTipoBeaconId() {
        return tipoBeaconId;
    }

    public void setTipoBeaconId(int tipoBeaconId) {
        this.tipoBeaconId = tipoBeaconId;
    }
}
