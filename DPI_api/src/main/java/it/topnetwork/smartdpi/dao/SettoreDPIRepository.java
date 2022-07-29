package it.topnetwork.smartdpi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.SettoreDPI;

public interface SettoreDPIRepository extends JpaRepository<SettoreDPI, Long> {

	/**
	 * cancella associazioni dpi - kit per il kit specificato
	 * @param idKit
	 * @param idUtenteOperazione
	 */
	@Modifying
	@Query("UPDATE SettoreDPI sd SET sd.dataCancellazione = CURRENT_TIMESTAMP, sd.utenteUltimaModifica = ?2, sd.dataUltimaModifica = CURRENT_TIMESTAMP WHERE sd.dpi.id = ?1 AND sd.dataCancellazione > CURRENT_TIMESTAMP")
	void deleteSettoriDPI(Long idDPI, Long idUtenteOperazione);
	
}
