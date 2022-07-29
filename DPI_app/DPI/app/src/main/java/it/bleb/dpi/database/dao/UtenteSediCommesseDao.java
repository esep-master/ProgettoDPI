package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.UtenteSediCommesse;

@Dao
public interface UtenteSediCommesseDao {

    @Query("SELECT * FROM utenteSediCommesse")
    public List<UtenteSediCommesse> getAll();

    @Query("SELECT * FROM utenteSediCommesse WHERE id = :id")
    UtenteSediCommesse getUtenteSediCommesse(int id);

    @Query("SELECT * FROM utenteSediCommesse WHERE admin_id = :id")
    List<UtenteSediCommesse> getUtenteSediCommesseForAdmin(int id);

    @Insert
    public void insert(UtenteSediCommesse utenteSediCommesse);

    @Insert
    public void insertAll(List<UtenteSediCommesse> utentiSediCommesse);

    @Delete
    public void delete(UtenteSediCommesse utenteSediCommesse);
}
