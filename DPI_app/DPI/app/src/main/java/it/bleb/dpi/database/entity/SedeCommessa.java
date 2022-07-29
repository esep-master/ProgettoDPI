package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class SedeCommessa implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "commessa")
    private int commessaId;

    @Ignore
    public SedeCommessa() {
    }

    public SedeCommessa(int id, String nome, int commessaId) {
        this.id = id;
        this.nome = nome;
        this.commessaId = commessaId;
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

    public int getCommessaId() {
        return commessaId;
    }

    public void setCommessaId(int commessaId) {
        this.commessaId = commessaId;
    }
}
