package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Kit.class,
                parentColumns = "id",
                childColumns = "kit_id"
        )})
public class DpiKit implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "dpi")
    private int dpiId;

    @ColumnInfo(name = "sblocco_allarme_da")
    private String sbloccoAllarmeDa;

    @ColumnInfo(name = "sblocco_allarme_a")
    private String sbloccoAllarmeA;

    @ColumnInfo(name = "kit_id", index = true)
    private int kitId;

    @Ignore
    public DpiKit() {
    }

    public DpiKit(int id, int dpiId, String sbloccoAllarmeDa, String sbloccoAllarmeA, int kitId) {
        this.id = id;
        this.dpiId = dpiId;
        this.sbloccoAllarmeDa = sbloccoAllarmeDa;
        this.sbloccoAllarmeA = sbloccoAllarmeA;
        this.kitId = kitId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDpiId() {
        return dpiId;
    }

    public void setDpiId(int dpiId) {
        this.dpiId = dpiId;
    }

    public String getSbloccoAllarmeDa() {
        return sbloccoAllarmeDa;
    }

    public void setSbloccoAllarmeDa(String sbloccoAllarmeDa) {
        this.sbloccoAllarmeDa = sbloccoAllarmeDa;
    }

    public String getSbloccoAllarmeA() {
        return sbloccoAllarmeA;
    }

    public void setSbloccoAllarmeA(String sbloccoAllarmeA) {
        this.sbloccoAllarmeA = sbloccoAllarmeA;
    }

    public int getKitId() {
        return kitId;
    }

    public void setKitId(int kitId) {
        this.kitId = kitId;
    }
}
