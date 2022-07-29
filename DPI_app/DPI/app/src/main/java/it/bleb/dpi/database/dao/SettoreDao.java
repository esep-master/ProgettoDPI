package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import it.bleb.dpi.database.entity.Settore;

@Dao
public interface SettoreDao {

    @Query("SELECT * FROM settore")
    public List<Settore> getAll();

    @Query("SELECT * FROM settore WHERE id = :id")
    Settore getSettore(int id);

    @Query("SELECT id FROM settore WHERE nome= :descrizione")
    int getIdFromDescrizione(String descrizione);

    @Insert
    public void insertAll(Settore... settori);

    @Delete
    public void delete(Settore settore);
}
