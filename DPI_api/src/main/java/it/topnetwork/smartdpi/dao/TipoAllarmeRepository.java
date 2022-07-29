package it.topnetwork.smartdpi.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.TipoAllarme;
import it.topnetwork.smartdpi.utility.constants.CacheEntry;

public interface TipoAllarmeRepository extends JpaRepository<TipoAllarme, Long> {

	/**
	 * recupera tutti i tipi allarme validi
	 * @return
	 */
	@Query("SELECT ta FROM TipoAllarme ta")
	@Cacheable(value = CacheEntry.LISTA_TIPI_ALLARMI)
	List<TipoAllarme> findAllValidi();
	
	/**
	 * cerca tipo allarme tramite id
	 * @param idTipoAllarme
	 * @return
	 */
	@Query("SELECT ta FROM TipoAllarme ta WHERE ta.id = ?1")
	TipoAllarme findValidById(long idTipoAllarme);
	
}
