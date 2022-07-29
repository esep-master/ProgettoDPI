package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.Kit;

@Dao
public interface KitDao {

    @Query("SELECT * FROM kit")
    public List<Kit> getAll();

    @Query("SELECT * FROM kit WHERE id = :id")
    Kit getKit(int id);

    @Query("SELECT * FROM kit WHERE settore_id = :idSettore")
    Kit getKitFromSettore(int idSettore);

    @Insert
    public void insert(Kit kit);

    @Insert
    public void insertAll(List<Kit> kits);

    @Delete
    public void delete(Kit kit);
}
