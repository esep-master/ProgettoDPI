package it.bleb.dpi.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class AzioneOperatore implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo(name = "id_app_Intervento")
    private int idAppIntervento;

    @ColumnInfo(name = "id_tipo_azione")
    private int idTipoAzione;

    @ColumnInfo(name = "data_azione")
    private String dataAzione;

    @Ignore
    public AzioneOperatore() {
    }

    public AzioneOperatore(int idAppIntervento, int idTipoAzione, String dataAzione) {
        this.idAppIntervento = idAppIntervento;
        this.idTipoAzione = idTipoAzione;
        this.dataAzione = dataAzione;
    }

    @Ignore
    public AzioneOperatore(int idTipoAzione, String dataAzione) {
        this.idTipoAzione = idTipoAzione;
        this.dataAzione = dataAzione;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAppIntervento() {
        return idAppIntervento;
    }

    public void setIdAppIntervento(int idAppIntervento) {
        this.idAppIntervento = idAppIntervento;
    }

    public int getIdTipoAzione() {
        return idTipoAzione;
    }

    public void setIdTipoAzione(int idTipoAzione) {
        this.idTipoAzione = idTipoAzione;
    }

    public String getDataAzione() {
        return dataAzione;
    }

    public void setDataAzione(String dataAzione) {
        this.dataAzione = dataAzione;
    }
}
