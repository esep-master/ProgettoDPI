package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Task implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int idApp;

    @ColumnInfo(name = "id_sede_commessa")
    private int idSedeCommessa;

    @ColumnInfo(name = "id_intervento")
    private int idIntervento;

    @ColumnInfo(name = "nome")
    private String name;

    @ColumnInfo(name = "settore")
    private String settore;

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

    @ColumnInfo(name = "is_started")
    private boolean isStarted;

    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;

    @Ignore
    public Task() {
    }

    public Task(int idSedeCommessa, String name, String settore, int idKit) {
        this.idSedeCommessa = idSedeCommessa;
        this.name = name;
        this.settore = settore;
        this.idKit = idKit;
        this.isStarted = false;
        this.isCompleted = false;
        this.idIntervento = 0;
    }

    public int getIdApp() {
        return idApp;
    }

    public void setIdApp(int idApp) {
        this.idApp = idApp;
    }

    public int getIdSedeCommessa() {
        return idSedeCommessa;
    }

    public void setIdSedeCommessa(int idSedeCommessa) {
        this.idSedeCommessa = idSedeCommessa;
    }

    public int getIdIntervento() {
        return idIntervento;
    }

    public void setIdIntervento(int idIntervento) {
        this.idIntervento = idIntervento;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSettore() {
        return settore;
    }

    public void setSettore(String settore) {
        this.settore = settore;
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

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
