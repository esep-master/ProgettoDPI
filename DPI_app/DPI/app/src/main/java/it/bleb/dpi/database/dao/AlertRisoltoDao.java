package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.AlertRisolto;

@Dao
public interface AlertRisoltoDao {

    @Query("SELECT * FROM alertrisolto")
    public List<AlertRisolto> getAll();

    @Query("SELECT * FROM alertrisolto WHERE id = :id")
    AlertRisolto getAlert(String id);

    @Insert
    public void insertAll(AlertRisolto... alert);

    @Delete
    public void delete(AlertRisolto alert);

    @Query("DELETE from alertrisolto")
    public void deleteAll();
}
