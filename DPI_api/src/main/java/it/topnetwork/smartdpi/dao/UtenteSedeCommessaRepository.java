package it.topnetwork.smartdpi.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.UtenteSedeCommessa;

public interface UtenteSedeCommessaRepository extends JpaRepository<UtenteSedeCommessa, Long> {

	/**
	 * cerca sedi commesse attive per l'utente
	 * @param idUtente
	 * @return
	 */
	@Query("SELECT u FROM UtenteSedeCommessa u WHERE u.utente.id = ?1")
	Set<UtenteSedeCommessa> findByUtente(Long idUtente);
	
	/**
	 * cerca associazione tramite id sede commessa e id utente
	 * @param idUtente
	 * @param idSedeCommessa
	 * @return
	 */
	@Query("SELECT u FROM UtenteSedeCommessa u WHERE u.utente.id = ?1 AND u.sedeCommessa.id = ?2")
	UtenteSedeCommessa findByUtenteAndSedeCommessa(Long idUtente, Long idSedeCommessa);
	
}
