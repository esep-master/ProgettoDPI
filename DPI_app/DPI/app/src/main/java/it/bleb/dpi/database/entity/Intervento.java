package it.bleb.dpi.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity
public class Intervento implements Serializable {
    @PrimaryKey
    @NotNull
    private int idAppIntervento;

    @ColumnInfo(name = "id_intervento")
    private int idIntervento;

    @ColumnInfo(name = "id_sede_commessa")
    private int idSedeCommessa;

    @ColumnInfo(name = "id_kit")
    private int idKit;

    @ColumnInfo(name = "data_inizio")
    private String dataInizio;

    @ColumnInfo(name = "data_fine")
    private String dataFine;

    @ColumnInfo(name = "latitudine")
    private double latitudine;

    @ColumnInfo(name = "longitudine")
    private double longitudine;

    @Ignore
    public Intervento() {
    }

    public Intervento(int idAppIntervento, int idIntervento, int idSedeCommessa, int idKit, String dataInizio, String dataFine, double latitudine, double longitudine) {
        this.idAppIntervento = idAppIntervento;
        this.idIntervento = idIntervento;
        this.idSedeCommessa = idSedeCommessa;
        this.idKit = idKit;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    public void setIdAppIntervento(int idAppIntervento) {
        this.idAppIntervento = idAppIntervento;
    }

    public int getIdAppIntervento() {
        return idAppIntervento;
    }

    public int getIdIntervento() {
        return idIntervento;
    }

    public void setIdIntervento(int idIntervento) {
        this.idIntervento = idIntervento;
    }

    public int getIdSedeCommessa() {
        return idSedeCommessa;
    }

    public void setIdSedeCommessa(int idSedeCommessa) {
        this.idSedeCommessa = idSedeCommessa;
    }

    public int getIdKit() {
        return idKit;
    }

    public void setIdKit(int idKit) {
        this.idKit = idKit;
    }

    public String getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(String dataInizio) {
        this.dataInizio = dataInizio;
    }

    public String getDataFine() {
        return dataFine;
    }

    public void setDataFine(String dataFine) {
        this.dataFine = dataFine;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }
}
