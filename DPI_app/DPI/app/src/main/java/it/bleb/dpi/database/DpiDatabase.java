package it.bleb.dpi.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import it.bleb.dpi.database.dao.AdminDao;
import it.bleb.dpi.database.dao.AlertDao;
import it.bleb.dpi.database.dao.AlertRisoltoDao;
import it.bleb.dpi.database.dao.AzioneOperatoreDao;
import it.bleb.dpi.database.dao.BeaconDao;
import it.bleb.dpi.database.dao.CommessaDao;
import it.bleb.dpi.database.dao.DpiDao;
import it.bleb.dpi.database.dao.DpiKitDao;
import it.bleb.dpi.database.dao.InterventoDao;
import it.bleb.dpi.database.dao.KitDao;
import it.bleb.dpi.database.dao.OperatoreDao;
import it.bleb.dpi.database.dao.OperatoreSediCommesseDao;
import it.bleb.dpi.database.dao.RuoloDao;
import it.bleb.dpi.database.dao.SedeCommessaDao;
import it.bleb.dpi.database.dao.SettoreDao;
import it.bleb.dpi.database.dao.TaskDao;
import it.bleb.dpi.database.dao.TipoAzioneOperatoreDao;
import it.bleb.dpi.database.dao.TipoBeaconDao;
import it.bleb.dpi.database.dao.TipoDpiDao;
import it.bleb.dpi.database.dao.UtenteSediCommesseDao;
import it.bleb.dpi.database.entity.Admin;
import it.bleb.dpi.database.entity.Alert;
import it.bleb.dpi.database.entity.AlertRisolto;
import it.bleb.dpi.database.entity.AzioneOperatore;
import it.bleb.dpi.database.entity.Beacon;
import it.bleb.dpi.database.entity.Commessa;
import it.bleb.dpi.database.entity.Dpi;
import it.bleb.dpi.database.entity.DpiKit;
import it.bleb.dpi.database.entity.Intervento;
import it.bleb.dpi.database.entity.Kit;
import it.bleb.dpi.database.entity.Operatore;
import it.bleb.dpi.database.entity.OperatoreSediCommesse;
import it.bleb.dpi.database.entity.Ruolo;
import it.bleb.dpi.database.entity.SedeCommessa;
import it.bleb.dpi.database.entity.Settore;
import it.bleb.dpi.database.entity.Task;
import it.bleb.dpi.database.entity.TipoAzioneOperatore;
import it.bleb.dpi.database.entity.TipoBeacon;
import it.bleb.dpi.database.entity.TipoDpi;
import it.bleb.dpi.database.entity.UtenteSediCommesse;

@Database(entities = {Alert.class, AlertRisolto.class, DpiKit.class, Operatore.class, Task.class, Admin.class, Beacon.class,
        Commessa.class, Dpi.class, OperatoreSediCommesse.class, Ruolo.class, SedeCommessa.class, Settore.class,
        TipoAzioneOperatore.class, TipoBeacon.class, TipoDpi.class, UtenteSediCommesse.class, Kit.class, AzioneOperatore.class, Intervento.class}, version = 7, exportSchema = false)
public abstract class DpiDatabase extends RoomDatabase {

    public abstract AlertDao alertDao();
    public abstract AlertRisoltoDao alertRisoltoDao();
    public abstract DpiKitDao dpiKitDao();
    public abstract OperatoreDao operatoreDao();
    public abstract TaskDao taskDao();
    public abstract AdminDao adminDao();
    public abstract BeaconDao beaconDao();
    public abstract CommessaDao commessaDao();
    public abstract DpiDao dpiDao();
    public abstract OperatoreSediCommesseDao operatoreSediCommesseDao();
    public abstract RuoloDao ruoloDao();
    public abstract SedeCommessaDao sedeCommessaDao();
    public abstract SettoreDao settoreDao();
    public abstract TipoAzioneOperatoreDao tipoAzioneOperatoreDao();
    public abstract TipoBeaconDao tipoBeaconDao();
    public abstract TipoDpiDao tipoDpiDao();
    public abstract UtenteSediCommesseDao utenteSediCommesseDao();
    public abstract KitDao kitDao();
    public abstract AzioneOperatoreDao azioneOperatoreDao();
    public abstract InterventoDao interventoDao();

}
