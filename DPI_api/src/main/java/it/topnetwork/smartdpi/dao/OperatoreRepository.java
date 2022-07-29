package it.topnetwork.smartdpi.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import it.topnetwork.smartdpi.entity.Operatore;

public interface OperatoreRepository extends JpaRepository<Operatore, Long> {

	/**
	 * cerca operatore tramite id
	 * @param idOperatore
	 * @return
	 */
	@Query("SELECT o FROM Operatore o WHERE o.id = ?1")
	Operatore findValidById(Long idOperatore);
	
	/**
	 * cerca operatore tramite matricola
	 * @param matricola
	 * @return
	 */
	@Query("SELECT o FROM Operatore o WHERE o.matricola = ?1")
	Operatore findByMatricola(String matricola);
	
	/**
	 * cerca operatore tramite email
	 * @param email
	 * @return
	 */
	@Query("SELECT o FROM Operatore o WHERE o.email = ?1")
	Operatore findByEmail(String email);
	
	/**
	 * cerca operatore tramite id e password
	 * @param idOperatore
	 * @param password
	 * @return
	 */
	@Query("SELECT o FROM Operatore o WHERE o.id = ?1 AND o.password = ?2")
	Operatore findByIdAndPassword(Long idOperatore, String password);
	
	/**
	 * recupera lista operatori visibili ad un particolare utente (associati alla stessa commessa)
	 * @param idUtente
	 * @return
	 */
	@Query("SELECT o FROM Operatore o")
	List<Operatore> findPerUtente(Long idUtente);
	
	/**
	 * soft delete operatore
	 * @param idOperatore
	 * @param idUtenteOperazione
	 */
	@Procedure(procedureName = "ELIMINA_OPERATORE", name = "Operatore.eliminaOperatore")
	Map<String, Object> eliminaOperatore(@Param("ID_OPERATORE") long idOperatore, @Param("ID_UTENTE_OPERAZIONE") long idUtenteOperazione);
	
}
