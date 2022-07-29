package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.Dpi;

@Dao
public interface DpiDao {

    @Query("SELECT * FROM dpi")
    public List<Dpi> getAll();

    @Query("SELECT * FROM dpi WHERE id = :id")
    Dpi getDpi(int id);

    @Query("SELECT * FROM dpi WHERE beacon = :beaconId")
    Dpi getDpiByBeaconId(int beaconId);

    @Insert
    public void insertAll(Dpi... dpi);

    @Delete
    public void delete(Dpi dpi);
}
