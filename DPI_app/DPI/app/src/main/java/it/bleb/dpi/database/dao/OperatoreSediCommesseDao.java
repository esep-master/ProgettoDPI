package it.bleb.dpi.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.bleb.dpi.database.entity.OperatoreSediCommesse;

@Dao
public interface OperatoreSediCommesseDao {

    @Query("SELECT * FROM operatoreSediCommesse")
    public List<OperatoreSediCommesse> getAll();

    @Query("SELECT * FROM operatoreSediCommesse WHERE id = :id")
    OperatoreSediCommesse getOperatoriSediCommesse(int id);

    @Insert
    public void insert(OperatoreSediCommesse operatoreSediCommesse);

    @Insert
    public void insertAll(List<OperatoreSediCommesse> operatoriSediCommesse);

    @Delete
    public void delete(OperatoreSediCommesse operatoreSediCommesse);
}
