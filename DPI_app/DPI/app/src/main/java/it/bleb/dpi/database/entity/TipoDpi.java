package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class TipoDpi implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "nome_modello_tf")
    private String nomeModelloTF;

    @ColumnInfo(name = "nome_icona")
    private String nomeIcona;

    @Ignore
    public TipoDpi() {
    }

    public TipoDpi(int id, String nome, String nomeModelloTF, String nomeIcona) {
        this.id = id;
        this.nome = nome;
        this.nomeModelloTF = nomeModelloTF;
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

    public String getNomeModelloTF() {
        return nomeModelloTF;
    }

    public void setNomeModelloTF(String nomeModelloTF) {
        this.nomeModelloTF = nomeModelloTF;
    }

    public String getNomeIcona() {
        return nomeIcona;
    }

    public void setNomeIcona(String nomeIcona) {
        this.nomeIcona = nomeIcona;
    }
}
