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
                entity = Operatore.class,
                parentColumns = "id",
                childColumns = "operatore_id"
        )})
public class OperatoreSediCommesse implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "sede_commessa")
    private int sedeCommessaId;

    @ColumnInfo(name = "operatore_id", index = true)
    private int operatoreId;

    @Ignore
    public OperatoreSediCommesse() {
    }

    public OperatoreSediCommesse(int id, int sedeCommessaId, int operatoreId) {
        this.id = id;
        this.sedeCommessaId = sedeCommessaId;
        this.operatoreId = operatoreId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSedeCommessaId() {
        return sedeCommessaId;
    }

    public void setSedeCommessaId(int sedeCommessaId) {
        this.sedeCommessaId = sedeCommessaId;
    }

    public int getOperatoreId() {
        return operatoreId;
    }

    public void setOperatoreId(int operatoreId) {
        this.operatoreId = operatoreId;
    }
}
