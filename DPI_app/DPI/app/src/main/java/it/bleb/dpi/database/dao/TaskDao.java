package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.bleb.dpi.database.entity.Task;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task")
    public List<Task> getAll();

    @Query("SELECT * FROM task WHERE idApp = :id")
    Task getTask(int id);

    @Query("SELECT * FROM task WHERE id_sede_commessa = :id")
    Task getTaskFromSedeCommessa(int id);

    @Insert
    public void insertAll(Task... tasks);

    @Delete
    public void delete(Task tasks);

    @Update
    public void update(Task task);
}
