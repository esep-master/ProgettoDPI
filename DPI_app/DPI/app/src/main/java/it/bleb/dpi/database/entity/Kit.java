package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Operatore.class,
                parentColumns = "id",
                childColumns = "operatore_id"
        )})
public class Kit implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "settore_id")
    private int settoreId;

    @ColumnInfo(name = "modello")
    private String modello;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "note_sblocco_totale")
    private String noteSbloccoTotale;

    @ColumnInfo(name = "data_assegnazione")
    private String dataAssegnazione;

    @Ignore
    private List<DpiKit> dpiKits;

    @ColumnInfo(name = "operatore_id", index = true)
    private int operatoreId;

    @Ignore
    public Kit() {
    }

    public Kit(int id, int settoreId, String modello, String note, String noteSbloccoTotale, String dataAssegnazione, int operatoreId) {
        this.id = id;
        this.settoreId = settoreId;
        this.modello = modello;
        this.note = note;
        this.noteSbloccoTotale = noteSbloccoTotale;
        this.dataAssegnazione = dataAssegnazione;
        this.operatoreId = operatoreId;
        this.dpiKits = new ArrayList<>();
    }

    @Ignore
    public Kit(int id, int settoreId, String modello, String note, String noteSbloccoTotale, String dataAssegnazione, List<DpiKit> dpiKits, int operatoreId) {
        this.id = id;
        this.settoreId = settoreId;
        this.modello = modello;
        this.note = note;
        this.noteSbloccoTotale = noteSbloccoTotale;
        this.dataAssegnazione = dataAssegnazione;
        this.dpiKits = dpiKits;
        this.operatoreId = operatoreId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSettoreId() {
        return settoreId;
    }

    public void setSettoreId(int settoreId) {
        this.settoreId = settoreId;
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

    public String getNoteSbloccoTotale() {
        return noteSbloccoTotale;
    }

    public void setNoteSbloccoTotale(String noteSbloccoTotale) {
        this.noteSbloccoTotale = noteSbloccoTotale;
    }

    public String getDataAssegnazione() {
        return dataAssegnazione;
    }

    public void setDataAssegnazione(String dataAssegnazione) {
        this.dataAssegnazione = dataAssegnazione;
    }

    public int getOperatoreId() {
        return operatoreId;
    }

    public void setOperatoreId(int operatoreId) {
        this.operatoreId = operatoreId;
    }

    public List<DpiKit> getDpiKits() {
        return dpiKits;
    }

    public void setDpiKits(List<DpiKit> dpiKits) {
        this.dpiKits = dpiKits;
    }
}
