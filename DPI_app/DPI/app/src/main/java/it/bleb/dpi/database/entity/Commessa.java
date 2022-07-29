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
                entity = SedeCommessa.class,
                parentColumns = "id",
                childColumns = "sede_commessa_id"
        )})
public class Commessa implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "settore")
    private int settoreId;

    @ColumnInfo(name = "sede_commessa_id", index = true)
    private int sedeCommessaId;

    @Ignore
    public Commessa() {
    }

    public Commessa(int id, String nome, int settoreId, int sedeCommessaId) {
        this.id = id;
        this.nome = nome;
        this.settoreId = settoreId;
        this.sedeCommessaId = sedeCommessaId;
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

    public int getSettoreId() {
        return settoreId;
    }

    public void setSettoreId(int settoreId) {
        this.settoreId = settoreId;
    }

    public int getSedeCommessaId() {
        return sedeCommessaId;
    }

    public void setSedeCommessaId(int sedeCommessaId) {
        this.sedeCommessaId = sedeCommessaId;
    }
}
