package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.bleb.dpi.database.entity.SedeCommessa;

@Dao
public interface SedeCommessaDao {

    @Query("SELECT * FROM sedecommessa")
    public List<SedeCommessa> getAll();

    @Query("SELECT * FROM sedeCommessa WHERE id = :id")
    SedeCommessa getSedeCommessa(int id);

    @Insert
    public void insertAll(SedeCommessa... sediCommessa);

    @Update
    public void update(SedeCommessa sedeCommessa);

    @Delete
    public void delete(SedeCommessa sedeCommessa);
}
