package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.TipoBeacon;

@Dao
public interface TipoBeaconDao {

    @Query("SELECT * FROM tipoBeacon")
    public List<TipoBeacon> getAll();

    @Query("SELECT * FROM tipoBeacon WHERE id = :id")
    TipoBeacon getTipoBeacon(int id);

    @Insert
    public void insertAll(TipoBeacon... tipiBeacon);

    @Delete
    public void delete(TipoBeacon tipoBeacon);
}
