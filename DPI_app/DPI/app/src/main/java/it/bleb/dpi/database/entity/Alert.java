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

import it.bleb.dpi.utils.AlertType;
import it.bleb.dpi.utils.DateUtil;

@Entity
public class Alert implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NotNull
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "id_dpi")
    private int idDpi;

    @ColumnInfo(name = "id_intervento")
    private int idIntervento;

    @ColumnInfo(name = "id_app_intervento")
    private int idAppIntervento;

    @ColumnInfo(name = "data_allarme")
    private String dataAllarme;

    @ColumnInfo(name = "alert_type")
    private AlertType type;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @Ignore
    public Alert() {
    }

    @SuppressLint("SimpleDateFormat")
    public Alert(String title, int idDpi, int idIntervento, int idAppIntervento, AlertType type, double latitude, double longitude) {
        Calendar c = Calendar.getInstance();
        this.title = title;
        this.idDpi = idDpi;
        this.idIntervento = idIntervento;
        this.idAppIntervento = idAppIntervento;
        SimpleDateFormat formatter  = DateUtil.getDateFormatter();
        dataAllarme = formatter.format(c.getTime());
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getDataAllarme() {
        return dataAllarme;
    }

    public void setDataAllarme(String dataAllarme) {
        this.dataAllarme = dataAllarme;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIdAppIntervento() {
        return idAppIntervento;
    }

    public void setIdAppIntervento(int idAppIntervento) {
        this.idAppIntervento = idAppIntervento;
    }
}
