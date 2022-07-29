package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.TipoAzioneOperatore;

@Dao
public interface TipoAzioneOperatoreDao {

    @Query("SELECT * FROM tipoAzioneOperatore")
    public List<TipoAzioneOperatore> getAll();

    @Query("SELECT * FROM tipoAzioneOperatore WHERE id = :id")
    TipoAzioneOperatore getTipoAzioneOperatore(int id);

    @Insert
    public void insert(TipoAzioneOperatore tipoAzioniOperatore);

    @Insert
    public void insertAll(List<TipoAzioneOperatore> tipiAzioniOperatore);

    @Delete
    public void delete(TipoAzioneOperatore tipoAzioneOperatoreDao);
}
