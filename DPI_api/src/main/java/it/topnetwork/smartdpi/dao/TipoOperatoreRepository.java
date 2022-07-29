package it.topnetwork.smartdpi.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.TipoOperatore;
import it.topnetwork.smartdpi.utility.constants.CacheEntry;

public interface TipoOperatoreRepository extends JpaRepository<TipoOperatore, Long> {

	/**
	 * recupera tutti i tipi operatore
	 * @return
	 */
	@Query("SELECT to FROM TipoOperatore to")
	@Cacheable(value = CacheEntry.LISTA_TIPI_OPERATORI)
	List<TipoOperatore> findAllValidi();
	
	/**
	 * recupera tipo operatore valido tramite id
	 * @param idTipoOperatore
	 * @return
	 */
	@Query("SELECT to FROM TipoOperatore to WHERE to.id = ?1")
	TipoOperatore findValidById(Long idTipoOperatore);
	
}
