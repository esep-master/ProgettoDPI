package it.topnetwork.smartdpi.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import it.topnetwork.smartdpi.entity.Commessa;

public interface CommessaRepository extends JpaRepository<Commessa, Long> {

	/**
	 * cerca commessa tramite id
	 * @param idCommessa
	 * @return
	 */
	@Query("SELECT c FROM Commessa c WHERE c.id = ?1")
	Commessa findValidById(Long idCommessa);
	
	/**
	 * sincronizza dati CRM
	 * @param matricola
	 * @param imei
	 * @param commessa
	 * @param settore
	 * @param operatore
	 * @param numeroTelefono
	 * @param email
	 * @param idUtenteOperazione
	 * @return
	 */
	@Procedure(procedureName = "SYNCHRONIZE_CRM_RECORD", name = "Commessa.synchronizeCRMData")
	Map<String, Object> synchronizeCRMData(@Param("MATRICOLA") String matricola, @Param("IMEI") String imei, @Param("COMMESSA") String commessa, @Param("SETTORE") String settore,
			@Param("OPERATORE") String operatore, @Param("NUMERO_TELEFONO") String numeroTelefono, @Param("EMAIL") String email, @Param("ID_UTENTE_OPERAZIONE") long idUtenteOperazione);
	
	/**
	 * salva commessa
	 * @param idCommessa
	 * @param nome
	 * @param idSettore
	 * @param idUtenteOperazione
	 * @return
	 */
	@Procedure(procedureName = "SALVA_COMMESSA", name = "Commessa.salvaCommessa")
	Map<String, Object> salvaCommessa(@Param("ID_COMMESSA") long idCommessa, @Param("NOME") String nome, @Param("ID_SETTORE") long idSettore, 
			@Param("ID_UTENTE_OPERAZIONE") long idUtenteOperazione);
	
	/**
	 * recupera tutte le commesse valide per un utente
	 * @param idUtente
	 * @return
	 */
	@Query("SELECT c FROM Commessa c WHERE c.id IN ("
				+ "SELECT usc.sedeCommessa.commessa FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
		+ ")")
	List<Commessa> findAllValidi(Long idUtente);
	
	/**
	 * conta tutte le commesse valide per un utente
	 * @param idUtente
	 * @return
	 */
	@Query("SELECT COUNT(c) FROM Commessa c WHERE c.id IN ("
				+ "SELECT usc.sedeCommessa.commessa FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
		+ ")")
	int countAllValidi(Long idUtente);
	
	/**
	 * recupera commesse tramite settore
	 * @param idSettore
	 * @return
	 */
	@Query("SELECT c FROM Commessa c WHERE c.settore.id = ?1")
	List<Commessa> findBySettore(Long idSettore);
	
	/**
	 * soft delete commessa
	 * @param idCommessa
	 * @param idUtenteOperazione
	 */
	@Procedure(procedureName = "ELIMINA_COMMESSA", name = "Commessa.eliminaCommessa")
	Map<String, Object> eliminaCommessa(@Param("ID_COMMESSA") long idCommessa, @Param("ID_UTENTE_OPERAZIONE") long idUtenteOperazione);
	
}
