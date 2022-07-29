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
public class Operatore implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "identificativo")
    private String identificativo;

    @ColumnInfo(name = "is_logged_in")
    private boolean isLoggedin;

    @ColumnInfo(name = "token")
    private String token;

    @Ignore
    private List<OperatoreSediCommesse> operatoreSediCommesse;

    @Ignore
    private List<Kit> kits;

    @Ignore
    public Operatore() {
    }

    public Operatore(int id, String identificativo, boolean isLoggedin) {
        this.id = id;
        this.identificativo = identificativo;
        this.isLoggedin = isLoggedin;
        this.operatoreSediCommesse = new ArrayList<>();
    }

    @Ignore
    public Operatore(int id, String identificativo, boolean isLoggedin, String token, List<OperatoreSediCommesse> operatoreSediCommesse, List<Kit> kits) {
        this.id = id;
        this.identificativo = identificativo;
        this.isLoggedin = isLoggedin;
        this.token = token;
        this.operatoreSediCommesse = operatoreSediCommesse;
        this.kits = kits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentificativo() {
        return identificativo;
    }

    public void setIdentificativo(String identificativo) {
        this.identificativo = identificativo;
    }

    public boolean isLoggedin() {
        return isLoggedin;
    }

    public void setLoggedin(boolean loggedin) {
        isLoggedin = loggedin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<OperatoreSediCommesse> getOperatoreSediCommesse() {
        return operatoreSediCommesse;
    }

    public void setOperatoreSediCommesse(List<OperatoreSediCommesse> operatoreSediCommesse) {
        this.operatoreSediCommesse = operatoreSediCommesse;
    }

    public List<Kit> getKits() {
        return kits;
    }

    public void setKits(List<Kit> kits) {
        this.kits = kits;
    }

    @Override
    public String toString() {
        return "Operatore{" +
                "id='" + id + '\'' +
                ", idLetturista='" + identificativo + '\'' +
                ", isLoggedin=" + isLoggedin +
                '}';
    }
}
