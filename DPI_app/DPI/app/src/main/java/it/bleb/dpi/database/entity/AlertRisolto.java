package it.bleb.dpi.database.entity;

import android.annotation.SuppressLint;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import it.bleb.dpi.utils.DateUtil;

@Entity
public class AlertRisolto implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NotNull
    private int id;

    @ColumnInfo(name = "id_dpi")
    private int idDpi;

    @ColumnInfo(name = "id_intervento")
    private int idIntervento;

    @ColumnInfo(name = "id_app_intervento")
    private int idAppIntervento;

    @ColumnInfo(name = "data_chiusura")
    private String dataChiusura;

    @Ignore
    public AlertRisolto() {
    }

    @SuppressLint("SimpleDateFormat")
    public AlertRisolto(int idDpi, int idIntervento, int idAppIntervento) {
        Calendar c = Calendar.getInstance();
        this.idDpi = idDpi;
        this.idIntervento = idIntervento;
        this.idAppIntervento = idAppIntervento;
        SimpleDateFormat formatter  = DateUtil.getDateFormatter();
        dataChiusura = formatter.format(c.getTime());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDpi() {
        return idDpi;
    }

    public void setIdDpi(int idDpi) {
        this.idDpi = idDpi;
    }

    public int getIdIntervento() {
        return idIntervento;
    }

    public void setIdIntervento(int idIntervento) {
        this.idIntervento = idIntervento;
    }

    public int getIdAppIntervento() {
        return idAppIntervento;
    }

    public void setIdAppIntervento(int idAppIntervento) {
        this.idAppIntervento = idAppIntervento;
    }

    public String getDataChiusura() {
        return dataChiusura;
    }

    public void setDataChiusura(String dataChiusura) {
        this.dataChiusura = dataChiusura;
    }
}
