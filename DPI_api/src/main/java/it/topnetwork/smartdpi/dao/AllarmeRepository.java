package it.topnetwork.smartdpi.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.Allarme;

public interface AllarmeRepository extends JpaRepository<Allarme, Long> {

	/**
	 * recupera allarme valido tramite id
	 * @param idAllarme
	 * @return
	 */
	@Query("SELECT a FROM Allarme a WHERE a.id = ?1")
	Allarme findValidById(Long idAllarme);
	
	/**
	 * recupera storico allarmi visibili ad un utente
	 * @param idUtente
	 * @return
	 */
//	@Query("SELECT a FROM Allarme a WHERE a.intervento.sedeCommessa.id IN ("
//			+ "SELECT usc.sedeCommessa.id FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
//		+ ")")
	@Query("SELECT a FROM Allarme a WHERE a.kit.operatore.id IN ("
			+ "SELECT osc.operatore.id FROM OperatoreSedeCommessa osc WHERE osc.sedeCommessa.id IN ("
				+ "SELECT usc.sedeCommessa.id FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
			+ ")"
		+ ") AND a.dpi.dataCancellazione > SYSDATE() AND a.kit.dataCancellazione > SYSDATE()")
	List<Allarme> findAllByUtente(Long idUtente);

	/**
	 * recupera storico allarmi da una certa data visibili ad un utente
	 * @param idUtente
	 * @param dataAllarmeDa
	 * @return
	 */
//	@Query("SELECT a FROM Allarme a WHERE a.dataAllarme >= ?2 AND a.intervento.sedeCommessa.id IN ("
//			+ "SELECT usc.sedeCommessa.id FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
//		+ ")")
	@Query("SELECT a FROM Allarme a WHERE a.dataAllarme >= ?2 AND a.kit.operatore.id IN ("
			+ "SELECT osc.operatore.id FROM OperatoreSedeCommessa osc WHERE osc.sedeCommessa.id IN ("
				+ "SELECT usc.sedeCommessa.id FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
			+ ")"
		+ ") AND a.dpi.dataCancellazione > SYSDATE() AND a.kit.dataCancellazione > SYSDATE()")
	List<Allarme> findAllByUtente(Long idUtente, Date dataAllarmeDa);
	
	/**
	 * recupera allarmi visibili ad un utente
	 * @param idUtente
	 * @return
	 */
//	@Query("SELECT a FROM Allarme a WHERE a.statoAllarme.statoFinale IS FALSE AND a.intervento.sedeCommessa.id IN ("
//			+ "SELECT usc.sedeCommessa.id FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
//		+ ")")
	@Query("SELECT a FROM Allarme a WHERE a.statoAllarme.statoFinale IS FALSE AND a.kit.operatore.id IN ("
			+ "SELECT osc.operatore.id FROM OperatoreSedeCommessa osc WHERE osc.sedeCommessa.id IN ("
				+ "SELECT usc.sedeCommessa.id FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
			+ ")"
		+ ") AND a.dpi.dataCancellazione > SYSDATE() AND a.kit.dataCancellazione > SYSDATE()")
	List<Allarme> findByUtente(Long idUtente);

	/**
	 * recupera allarmi da una certa data visibili ad un utente
	 * @param idUtente
	 * @param dataAllarmeDa
	 * @return
	 */
//	@Query("SELECT a FROM Allarme a WHERE a.statoAllarme.statoFinale IS FALSE AND a.dataAllarme >= ?2 AND a.intervento.sedeCommessa.id IN ("
//			+ "SELECT usc.sedeCommessa.id FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
//		+ ")")
	@Query("SELECT a FROM Allarme a WHERE a.statoAllarme.statoFinale IS FALSE AND a.dataAllarme >= ?2 AND  a.kit.operatore.id IN ("
			+ "SELECT osc.operatore.id FROM OperatoreSedeCommessa osc WHERE osc.sedeCommessa.id IN ("
				+ "SELECT usc.sedeCommessa.id FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
			+ ")"
		+ ") AND a.dpi.dataCancellazione > SYSDATE() AND a.kit.dataCancellazione > SYSDATE()")
	List<Allarme> findByUtente(Long idUtente, Date dataAllarmeDa);

	/**
	 * recupera allarmi per operatore da una certa data in poi
	 * @param idOperatore
	 * @return
	 */
	@Query("SELECT a FROM Allarme a WHERE a.intervento.operatore.id = ?1 AND a.dataAllarme >= ?2")
	List<Allarme> findAllByOperatore(Long idOperatore, Date dataAllarmeDa);
	
	/**
	 * recupera allarme aperto per dpi, kit e intevento
	 * @param idDPI
	 * @param idIntervento
	 * @param idKit
	 * @return
	 */
	@Query("SELECT a FROM Allarme a WHERE a.dpi.id = ?1 AND a.intervento.id = ?2 AND a.kit.id = ?3 AND a.statoAllarme.statoIniziale IS TRUE")
	Allarme findAperto(Long idDPI, Long idIntervento, Long idKit);

	/**
	 * recupera allarme non chiuso per dpi, kit e intervento
	 * @param idDPI
	 * @param idIntervento
	 * @param idKit
	 * @return
	 */
	@Query("SELECT a FROM Allarme a WHERE a.dpi.id = ?1 AND a.intervento.id = ?2 AND a.statoAllarme.statoFinale IS FALSE")
	Allarme find(Long idDPI, Long idIntervento);
	
}
