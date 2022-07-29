package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.Ruolo;

@Dao
public interface RuoloDao {

    @Query("SELECT * FROM ruolo")
    public List<Ruolo> getAll();

    @Query("SELECT * FROM ruolo WHERE id = :id")
    Ruolo getRuolo(int id);

    @Insert
    public void insertAll(Ruolo... ruoli);

    @Delete
    public void delete(Ruolo ruolo);
}
