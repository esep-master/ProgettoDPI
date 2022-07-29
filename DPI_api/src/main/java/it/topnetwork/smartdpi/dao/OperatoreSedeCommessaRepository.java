package it.topnetwork.smartdpi.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.OperatoreSedeCommessa;

public interface OperatoreSedeCommessaRepository extends JpaRepository<OperatoreSedeCommessa, Long> {
	
	/**
	 * cerca associazione tramite id sede commessa e id operatore
	 * @param idOperatore
	 * @param idSedeCommessa
	 * @return
	 */
	@Query("SELECT o FROM OperatoreSedeCommessa o WHERE o.operatore.id = ?1 AND o.sedeCommessa.id = ?2")
	OperatoreSedeCommessa findByUtenteAndSedeCommessa(Long idOperatore, Long idSedeCommessa);
	
	/**
	 * cerca operatori associati ad una commessa tramite sede commessa
	 * @param idCommessa
	 * @return
	 */
	@Query("SELECT o FROM OperatoreSedeCommessa o WHERE o.sedeCommessa.commessa.id = ?1")
	List<OperatoreSedeCommessa> findByCommessa(Long idCommessa);
	
}
