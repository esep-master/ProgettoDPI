package it.topnetwork.smartdpi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.DPIKit;

public interface DPIKitRepository extends JpaRepository<DPIKit, Long> {

	/**
	 * cancella associazioni dpi - kit per il kit specificato
	 * @param idKit
	 * @param idUtenteOperazione
	 */
	@Modifying
	@Query("UPDATE DPIKit SET dataCancellazione = CURRENT_TIMESTAMP, utenteUltimaModifica = ?2, dataUltimaModifica = CURRENT_TIMESTAMP WHERE kit.id = ?1 AND dataCancellazione > CURRENT_TIMESTAMP")
	void deleteDPIKit(Long idKit, Long idUtenteOperazione);
	
	/**
	 * recupera DPI associato ad un kit
	 * @param idKit
	 * @param idDPI
	 * @return
	 */
	@Query("SELECT dk FROM DPIKit dk WHERE dk.kit.id = ?1 AND dk.dpi.id = ?2")
	DPIKit findByKitAndDPI(Long idKit, Long idDPI);
	
}
