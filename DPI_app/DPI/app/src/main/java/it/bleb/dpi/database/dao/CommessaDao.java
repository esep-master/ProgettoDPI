package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.Commessa;

@Dao
public interface CommessaDao {

    @Query("SELECT * FROM commessa")
    public List<Commessa> getAll();

    @Query("SELECT * FROM commessa WHERE id = :id")
    Commessa getCommessa(int id);

    @Insert
    public void insertAll(Commessa... commesse);

    @Delete
    public void delete(Commessa commessa);
}
