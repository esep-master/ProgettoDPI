package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class TipoAzioneOperatore implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "nome")
    private String nome;

    @Ignore
    public TipoAzioneOperatore() {
    }

    public TipoAzioneOperatore(int id, String nome) {
        this.id = id;
        this.nome = nome;
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
}
