package it.bleb.dpi.database.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import it.bleb.dpi.database.entity.Intervento;

@Dao
public interface InterventoDao {

    @Query("SELECT * FROM intervento")
    public List<Intervento> getAll();

    @Query("SELECT * FROM intervento WHERE idAppIntervento = :id")
    Intervento getIntervento(int id);

    @Insert
    public void insert(Intervento intervento);

    @Update
    public void updateIntervento(Intervento intervento);

    @Delete
    public void delete(Intervento intervento);

    @Query("DELETE from intervento")
    public void deleteAll();
}
