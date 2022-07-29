package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Admin implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "num_telefono")
    private String numeroTelefono;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "cognome")
    private String cognome;

    @ColumnInfo(name = "ruolo")
    private int ruoloId;

    @Ignore
    private List<UtenteSediCommesse> utenteSediCommesse;

    @Ignore
    public Admin() {
    }

    public Admin(int id, String username, String email, String numeroTelefono, String nome, String cognome, int ruoloId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.numeroTelefono = numeroTelefono;
        this.nome = nome;
        this.cognome = cognome;
        this.ruoloId = ruoloId;
        this.utenteSediCommesse = new ArrayList<>();
    }

    @Ignore
    public Admin(int id, String username, String email, String numeroTelefono, String nome, String cognome, int ruoloId, List<UtenteSediCommesse> utenteSediCommesse) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.numeroTelefono = numeroTelefono;
        this.nome = nome;
        this.cognome = cognome;
        this.ruoloId = ruoloId;
        this.utenteSediCommesse = utenteSediCommesse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public int getRuoloId() {
        return ruoloId;
    }

    public void setRuoloId(int ruoloId) {
        this.ruoloId = ruoloId;
    }

    public List<UtenteSediCommesse> getutenteSediCommesse() {
        return utenteSediCommesse;
    }

    public void setutenteSediCommesse(List<UtenteSediCommesse> utenteSediCommesse) {
        this.utenteSediCommesse = utenteSediCommesse;
    }
}
