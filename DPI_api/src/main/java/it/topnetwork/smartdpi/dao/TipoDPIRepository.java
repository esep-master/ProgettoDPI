package it.topnetwork.smartdpi.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.TipoDPI;
import it.topnetwork.smartdpi.utility.constants.CacheEntry;

public interface TipoDPIRepository extends JpaRepository<TipoDPI, Long> {

	/**
	 * recupera tutti i tipi dpi validi
	 * @return
	 */
	@Query("SELECT td FROM TipoDPI td")
	@Cacheable(value = CacheEntry.LISTA_TIPI_DPI)
	List<TipoDPI> findAllValidi();
	
	/**
	 * recupera tipo dpi valido tramite id
	 * @param idTipoDPI
	 * @return
	 */
	@Query("SELECT td FROM TipoDPI td WHERE td.id = ?1")
	TipoDPI findValidById(Long idTipoDPI);
	
}
