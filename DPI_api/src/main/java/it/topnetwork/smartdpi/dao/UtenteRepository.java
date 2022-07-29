package it.topnetwork.smartdpi.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import it.topnetwork.smartdpi.entity.Utente;

public interface UtenteRepository extends JpaRepository<Utente, Long> {
	
	/**
	 * cerca utente tramite id
	 * @param idUtente
	 * @return
	 */
	@Query("SELECT u FROM Utente u WHERE u.id = ?1")
	Utente findValidById(Long idUtente);
	
	/**
	 * cerca utente tramite username e password
	 * @param username
	 * @param password
	 * @return
	 */
	@Query("SELECT u FROM Utente u WHERE u.username = ?1 AND u.password = ?2")
	Utente findByUsernameAndPassword(String username, String password);
	
	/**
	 * cerca utente tramite id e password
	 * @param idUtente
	 * @param password
	 * @return
	 */
	@Query("SELECT u FROM Utente u WHERE u.id = ?1 AND u.password = ?2")
	Utente findByIdAndPassword(Long idUtente, String password);
	
	/**
	 * cerca utente tramite username
	 * @param username
	 * @return
	 */
	@Query("SELECT u FROM Utente u WHERE u.username = ?1")
	Utente findByUsername(String username);
	
	/**
	 * cerca utente tramite email
	 * @param email
	 * @return
	 */
	@Query("SELECT u FROM Utente u WHERE u.email = ?1")
	Utente findByEmail(String email);
	
//	/**
//	 * cerca utenti tramite sede commessa
//	 * @param idSedeCommessa
//	 * @return
//	 */
//	@Query("SELECT u FROM Utente u WHERE u.id IN ("
//				+ "SELECT usc.utente.id FROM UtenteSedeCommessa usc WHERE usc.sedeCommessa.id = ?1"
//		+ ")")
//	List<Utente> findBySedeCommessa(Long idSedeCommessa);
	
	/**
	 * cerca admin sedi commesse operatore
	 * @param idOperatore
	 * @return
	 */
	@Query("SELECT u FROM Utente u WHERE u.id IN ("
				+ "SELECT usc.utente.id FROM UtenteSedeCommessa usc WHERE usc.sedeCommessa.id IN ("
					+ "SELECT osc.sedeCommessa.id FROM OperatoreSedeCommessa osc WHERE osc.operatore.id = ?1"
				+ ")"
			+ ")")
	List<Utente> findAdminSediCommesseOperatore(Long idOperatore);
	
	/**
	 * recupera lista utenti visibili ad un particolare utente (associati alla stessa commessa)
	 * @param idUtente
	 * @return
	 */
//	@Query("SELECT u FROM Utente u WHERE u.id <> ?1 AND u.ruolo.superAdmin IS FALSE AND u.id IN (" + 
//				"SELECT usc1.utente.id FROM UtenteSedeCommessa usc1 WHERE usc1.sedeCommessa IS NULL OR usc1.sedeCommessa.id IN (" + 
//					"SELECT usc2.sedeCommessa.id FROM UtenteSedeCommessa usc2 WHERE usc2.utente.id = ?1" + 
//				")" + 
//			")")
	@Query("SELECT u FROM Utente u WHERE u.id <> ?1 AND u.ruolo.superAdmin IS FALSE")
	List<Utente> findPerUtente(Long idUtente);
	
	/**
	 * soft delete utente
	 * @param idUtente
	 * @param idUtenteOperazione
	 */
	@Procedure(procedureName = "ELIMINA_UTENTE", name = "Utente.eliminaUtente")
	Map<String, Object> eliminaUtente(@Param("ID_UTENTE") long idUtente, @Param("ID_UTENTE_OPERAZIONE") long idUtenteOperazione);

}
