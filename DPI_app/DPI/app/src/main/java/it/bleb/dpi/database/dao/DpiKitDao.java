package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.DpiKit;

@Dao
public interface DpiKitDao {

    @Query("SELECT * FROM dpiKit")
    public List<DpiKit> getAll();

    @Query("SELECT * FROM dpiKit WHERE id = :id")
    DpiKit getDpiKit(int id);

    @Query("SELECT * FROM dpikit WHERE kit_id = :kitId")
    List<DpiKit> getDpiKitByKitId(int kitId);

    @Insert
    public void insert(DpiKit dpiKit);

    @Insert
    public void insertAll(List<DpiKit> dpiKit);

    @Delete
    public void delete(DpiKit dpiKit);
}
