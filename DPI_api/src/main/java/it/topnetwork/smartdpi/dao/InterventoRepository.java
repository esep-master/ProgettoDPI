package it.topnetwork.smartdpi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.Intervento;

public interface InterventoRepository extends JpaRepository<Intervento, Long> {

	/**
	 * cerca intervento tramite id
	 * @param idIntervento
	 * @return
	 */
	@Query("SELECT i FROM Intervento i WHERE i.id = ?1")
	Intervento findValidById(Long idIntervento);
	
}
