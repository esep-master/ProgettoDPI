package it.bleb.dpi.database.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import it.bleb.dpi.database.entity.AzioneOperatore;

@Dao
public interface AzioneOperatoreDao {

    @Query("SELECT * FROM azioneOperatore")
    public List<AzioneOperatore> getAll();

    @Query("SELECT * FROM azioneOperatore WHERE id = :id")
    AzioneOperatore getAzioneOperatore(int id);

    @Insert
    public void insert(AzioneOperatore azioniOperatore);

    @Delete
    public void delete(AzioneOperatore azioneOperatoreDao);

    @Query("DELETE from azioneOperatore")
    public void deleteAll();

}
