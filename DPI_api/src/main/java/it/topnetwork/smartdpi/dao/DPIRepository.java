package it.topnetwork.smartdpi.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import it.topnetwork.smartdpi.entity.DPI;

public interface DPIRepository extends JpaRepository<DPI, Long> {
	
	/**
	 * cerca dpi tramite id
	 * @param idDPI
	 * @return
	 */
	@Query("SELECT d FROM DPI d WHERE d.id = ?1")
	DPI findValidById(Long idDPI);

	/**
	 * recupera tutti i settori validi
	 * @return
	 */
	@Query("SELECT d FROM DPI d")
	List<DPI> findAllValidi();
	
	/**
	 * recupera i dpi disponibili per l'utente e per il settore (tutti i dpi validi meno quelli associati a kit di altri operatori)
	 * @param idOperatore
	 * @param idSettore
	 * @return
	 */
	@Query("SELECT d FROM DPI d WHERE d.dataScadenza > CURRENT_TIMESTAMP AND d.id NOT IN ("
				+ "SELECT dk.dpi.id FROM DPIKit dk WHERE dk.kit.operatore.id <> ?1"
			+ ") ")
//				AND d.id IN ("
//				+ "SELECT sd.dpi.id FROM SettoreDPI sd WHERE sd.settore.id = ?2"
//			+ ")")
	List<DPI> findDisponibili(Long idOperatore, Long idSettore);
	
	/**
	 * recupera DPI a cui è associato il beacon
	 * @param idBeacon
	 * @return
	 */
	@Query("SELECT d FROM DPI d WHERE d.beacon.id = ?1")
	DPI findByBeacon(Long idBeacon);
	
	/**
	 * recupera DPI in scadenza per cui non è stato ancora generato un alert
	 * @param dataScadenza
	 * @param idTipoAllarmeDPIInScadenza
	 * @return
	 */
	@Query("SELECT d FROM DPI d WHERE d.dataScadenza <= ?1 AND d.id NOT IN ("
			+ "SELECT a.dpi.id FROM Allarme a WHERE a.tipoAllarme.id = ?2 AND a.statoAllarme.statoFinale IS FALSE"
		+ ")")
	List<DPI> findInScadenza(Date dataScadenza, long idTipoAllarmeDPIInScadenza);
	
	/**
	 * count DPI non associati
	 * @return
	 */
	@Query("SELECT COUNT(d) FROM DPI d WHERE d.id NOT IN ("
				+ "SELECT dk.dpi.id FROM DPIKit dk"
			+ ")")
	int countNonAssociati();

	/**
	 * salva dati dpi e beacon
	 * @param idDpi
	 * @param codiceDpi
	 * @param marcaDpi
	 * @param modelloDpi
	 * @param dataScadenza
	 * @param noteDPI
	 * @param idTipoDpi
	 * @param idBeacon
	 * @param serialeBeacon
	 * @param idTipoBeacon
	 * @param idUtenteOperazione
	 * @return
	 */
	@Procedure(procedureName = "SALVA_DPI", name = "DPI.salvaDPI")
	Map<String, Object> saveDPI(@Param("ID_DPI") long idDpi, @Param("CODICE_DPI") String codiceDpi,
			@Param("MARCA_DPI") String marcaDpi, @Param("MODELLO_DPI") String modelloDpi,
			@Param("DATA_SCADENZA_DPI") Date dataScadenza, @Param("NOTE_DPI") String noteDPI, @Param("ID_TIPO_DPI") long idTipoDpi, 
			@Param("ID_BEACON") long idBeacon, @Param("SERIALE_BEACON") String serialeBeacon, 
			@Param("ID_TIPO_BEACON") long idTipoBeacon, @Param("ID_UTENTE_OPERAZIONE") long idUtenteOperazione);
	
	/**
	 * elimina dpi e/o beacon
	 * @param idDpi
	 * @param idBeacon
	 * @param idUtenteOperazione
	 * @return
	 */
	@Procedure(procedureName = "ELIMINA_DPI", name = "DPI.eliminaDPI")
	Map<String, Object> eliminaDPI(@Param("ID_DPI") long idDpi, @Param("ID_BEACON") long idBeacon, @Param("ID_UTENTE_OPERAZIONE") long idUtenteOperazione);
	
}
