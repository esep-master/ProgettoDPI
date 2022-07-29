package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.TipoDpi;

@Dao
public interface TipoDpiDao {

    @Query("SELECT * FROM tipoDpi")
    public List<TipoDpi> getAll();

    @Query("SELECT * FROM tipoDpi WHERE id = :id")
    TipoDpi getTipoDpi(int id);

    @Insert
    public void insertAll(TipoDpi... tipiDpi);

    @Delete
    public void delete(TipoDpi tipoDpi);
}
