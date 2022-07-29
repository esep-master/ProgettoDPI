package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.bleb.dpi.database.entity.Beacon;

@Dao
public interface BeaconDao {

    @Query("SELECT * FROM beacon")
    public List<Beacon> getAll();

    @Query("SELECT * FROM beacon WHERE id = :id")
    Beacon getBeacon(int id);

    @Update
    public void updateBeacon(Beacon beacon);

    @Query("SELECT id FROM beacon WHERE seriale = :seriale")
    int getBeaconIdBySeriale(String seriale);

    @Insert
    public void insertAll(Beacon... beacons);

    @Delete
    public void delete(Beacon beacon);
}
