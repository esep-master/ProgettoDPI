package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Ruolo implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "is_super_admin")
    private boolean isSuperAdmin;

    @ColumnInfo(name = "funzioni_ruolo")
    private String funzioniRuolo;

    @Ignore
    public Ruolo() {
    }

    public Ruolo(int id, String nome, boolean isSuperAdmin, String funzioniRuolo) {
        this.id = id;
        this.nome = nome;
        this.isSuperAdmin = isSuperAdmin;
        this.funzioniRuolo = funzioniRuolo;
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

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }

    public String getFunzioniRuolo() {
        return funzioniRuolo;
    }

    public void setFunzioniRuolo(String funzioniRuolo) {
        this.funzioniRuolo = funzioniRuolo;
    }
}
