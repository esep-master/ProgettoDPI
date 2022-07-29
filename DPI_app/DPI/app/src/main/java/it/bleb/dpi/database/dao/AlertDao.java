package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.Alert;

@Dao
public interface AlertDao {

    @Query("SELECT * FROM alert")
    public List<Alert> getAll();

    @Query("SELECT * FROM alert WHERE id = :id")
    Alert getAlert(String id);

    @Insert
    public void insertAll(Alert... alert);

    @Delete
    public void delete(Alert alert);

    @Query("DELETE from alert")
    public void deleteAll();
}
