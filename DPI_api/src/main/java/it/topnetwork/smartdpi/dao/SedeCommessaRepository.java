package it.topnetwork.smartdpi.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import it.topnetwork.smartdpi.entity.SedeCommessa;

public interface SedeCommessaRepository extends JpaRepository<SedeCommessa, Long> {

	/**
	 * recupera tutte le sedi commesse valide per l'utente
	 * @return
	 */
	@Query("SELECT sc FROM SedeCommessa sc WHERE sc.id IN ("
			+ "SELECT usc.sedeCommessa FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
		+ ")")
	List<SedeCommessa> findAllValidi(Long idUtente);
	
	/**
	 * cerca sede commessa tramite id
	 * @param idSedeCommessa
	 * @return
	 */
	@Query("SELECT sc FROM SedeCommessa sc WHERE sc.id = ?1")
	SedeCommessa findValidById(Long idSedeCommessa);
	
	/**
	 * salva sede commessa
	 * @param idSedeCommessa
	 * @param nome
	 * @param idCommessa
	 * @param idUtenteOperazione
	 * @return
	 */
	@Procedure(procedureName = "SALVA_SEDE_COMMESSA", name = "SedeCommessa.salvaSedeCommessa")
	Map<String, Object> salvaSedeCommessa(@Param("ID_SEDE_COMMESSA") long idSedeCommessa, @Param("NOME") String nome, @Param("ID_COMMESSA") long idCommessa, 
			@Param("ID_UTENTE_OPERAZIONE") long idUtenteOperazione);
	
	/**
	 * soft delete sede commessa
	 * @param idSedeCommessa
	 * @param idUtenteOperazione
	 */
	@Procedure(procedureName = "ELIMINA_COMMESSA", name = "SedeCommessa.eliminaSedeCommessa")
	Map<String, Object> eliminaSedeCommessa(@Param("ID_SEDE_COMMESSA") long idSedeCommessa, @Param("ID_UTENTE_OPERAZIONE") long idUtenteOperazione);
	
}
