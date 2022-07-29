package it.topnetwork.smartdpi.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.StatoAllarme;
import it.topnetwork.smartdpi.utility.constants.CacheEntry;

public interface StatoAllarmeRepository extends JpaRepository<StatoAllarme, Long> {

	/**
	 * recupera tutti gli stati allarmi
	 * @return
	 */
	@Query("SELECT sa FROM StatoAllarme sa")
	@Cacheable(value = CacheEntry.LISTA_STATI_ALLARMI)
	List<StatoAllarme> findAllValidi();
	
	/**
	 * recupera stato iniziale
	 * @return
	 */
	@Query("SELECT sa FROM StatoAllarme sa WHERE sa.statoIniziale = TRUE AND sa.statoFinale = FALSE AND sa.sbloccoAutomaticoOperatore = FALSE")
	StatoAllarme findStatoIniziale();
	
	/**
	 * recupera stato per sblocco automatico
	 * @return
	 */
	@Query("SELECT sa FROM StatoAllarme sa WHERE sa.sbloccoAutomaticoOperatore = TRUE")
	StatoAllarme findStatoSbloccoAutomatico();

	/**
	 * recupera stato lavorazione
	 * @return
	 */
	@Query("SELECT sa FROM StatoAllarme sa WHERE sa.statoIniziale = FALSE AND sa.statoFinale = FALSE AND sa.sbloccoAutomaticoOperatore = FALSE")
	StatoAllarme findStatoLavorazione();

	/**
	 * recupera stato chiusura
	 * @return
	 */
	@Query("SELECT sa FROM StatoAllarme sa WHERE sa.statoIniziale = FALSE AND sa.statoFinale = TRUE AND sa.sbloccoAutomaticoOperatore = FALSE")
	StatoAllarme findStatoChiusura();
	
}