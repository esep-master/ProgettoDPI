package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.Admin;

@Dao
public interface AdminDao {

    @Query("SELECT * FROM admin")
    public List<Admin> getAll();

    @Query("SELECT * FROM admin WHERE id = :id")
    Admin getAdmin(int id);

    @Insert
    public void insertAll(Admin... admins);

    @Delete
    public void delete(Admin admin);

    /*@Query("SELECT num_telefono FROM admin where ruolo = :ruoloCommessa and id=:id")
    String getNumTelefonoAdmin(String ruoloCommessa, int id);*/
}
