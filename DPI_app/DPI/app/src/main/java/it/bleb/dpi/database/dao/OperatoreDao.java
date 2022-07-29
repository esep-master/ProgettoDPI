package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.Operatore;

@Dao
public interface OperatoreDao {

    @Query("SELECT * FROM operatore")
    List<Operatore> getAll();

    @Query("SELECT * FROM operatore WHERE id = :id")
    Operatore getOperatore(int id);

    @Insert
    void insertAll(Operatore... operatori);

    @Delete
    void delete(Operatore operatore);
}
