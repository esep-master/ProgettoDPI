package it.topnetwork.smartdpi.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.TipoBeacon;
import it.topnetwork.smartdpi.utility.constants.CacheEntry;

public interface TipoBeaconRepository extends JpaRepository<TipoBeacon, Long> {
	
	/**
	 * recupera tutti i tipi beacon validi
	 * @return
	 */
	@Query("SELECT tb FROM TipoBeacon tb")
	@Cacheable(value = CacheEntry.LISTA_TIPI_BEACON)
	List<TipoBeacon> findAllValidi();
	

	/**
	 * recupera tipo beacon valido tramite id
	 * @param idTipoBeacon
	 * @return
	 */
	@Query("SELECT tb FROM TipoBeacon tb WHERE tb.id = ?1")
	TipoBeacon findValidById(Long idTipoBeacon);
	
}
