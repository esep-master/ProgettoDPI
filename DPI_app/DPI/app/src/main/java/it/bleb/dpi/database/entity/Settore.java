package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Settore implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "nome_icona")
    private String nomeIcona;

    @Ignore
    public Settore() {
    }

    public Settore(int id, String nome, String nomeIcona) {
        this.id = id;
        this.nome = nome;
        this.nomeIcona = nomeIcona;
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

    public String getNomeIcona() {
        return nomeIcona;
    }

    public void setNomeIcona(String nomeIcona) {
        this.nomeIcona = nomeIcona;
    }
}
