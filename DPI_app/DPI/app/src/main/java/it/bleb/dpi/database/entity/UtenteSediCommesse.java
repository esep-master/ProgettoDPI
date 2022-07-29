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
                entity = Admin.class,
                parentColumns = "id",
                childColumns = "admin_id"
        )})
public class UtenteSediCommesse implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "sede_commessa")
    private int sedeCommessaId;

    @ColumnInfo(name = "admin_id", index = true)
    private int adminId;

    @Ignore
    public UtenteSediCommesse() {
    }

    public UtenteSediCommesse(int id, int sedeCommessaId, int adminId) {
        this.id = id;
        this.sedeCommessaId = sedeCommessaId;
        this.adminId = adminId;
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

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
}
